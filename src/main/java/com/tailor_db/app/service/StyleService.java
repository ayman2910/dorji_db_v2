package com.tailor_db.app.service;

import com.tailor_db.app.dao.MeasurementRequirementDao;
import com.tailor_db.app.dao.StyleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StyleService {

    private final StyleDao styleDao;
    private final MeasurementRequirementDao measurementRequirementDao;
    private final AuditLogService auditLogService;

    public StyleService(StyleDao styleDao, MeasurementRequirementDao measurementRequirementDao, AuditLogService auditLogService) {
        this.styleDao = styleDao;
        this.measurementRequirementDao = measurementRequirementDao;
        this.auditLogService = auditLogService;
    }

    public List<Map<String, Object>> getAllStyles() {
        return styleDao.findAll();
    }

    public Map<String, Object> getStyleById(int id) {
        return styleDao.findById(id);
    }

    public List<Map<String, Object>> getStyleImages(int id) {
        return styleDao.findImagesByStyleId(id);
    }

    @Transactional
    public void createStyle(Map<String, String> formData, MultipartFile image) {
        String name = formData.get("styleName");
        double price = parseDouble(formData.get("basePrice"));
        double hours = parseDouble(formData.get("estimatedHours"));

        String imagePath = "/images/styles/default-style.png";
        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String generatedFilename = UUID.randomUUID().toString() + ext;
                Path uploadDir = Paths.get("src/main/resources/static/images/styles");
                Files.createDirectories(uploadDir);
                Path targetPath = uploadDir.resolve(generatedFilename);
                Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Also copy to target/classes if available for instant runtime static serving
                Path targetClassesDir = Paths.get("target/classes/static/images/styles");
                if (Files.exists(Paths.get("target/classes"))) {
                    Files.createDirectories(targetClassesDir);
                    Files.copy(targetPath, targetClassesDir.resolve(generatedFilename), StandardCopyOption.REPLACE_EXISTING);
                }

                imagePath = "/images/styles/" + generatedFilename;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save style image", e);
            }
        }

        styleDao.insert(name, price, hours, imagePath);
    }

    @Transactional
    public void updateStyle(int id, Map<String, String> formData) {
        String name = formData.get("styleName");
        double price = parseDouble(formData.get("basePrice"));
        double hours = parseDouble(formData.get("estimatedHours"));
        styleDao.update(id, name, price, hours);
    }

    @Transactional
    public void deleteStyle(int id, int tailorId) {
        Map<String, Object> style = styleDao.findById(id);
        String styleName = style != null ? String.valueOf(style.get("Style_name")) : "ID: " + id;
        styleDao.delete(id);
        auditLogService.logActivity(tailorId, "DELETE", "STYLE_TEMPLATE", String.valueOf(id), styleName, null);
    }

    @Transactional
    public void addStyleImages(int styleId, List<MultipartFile> images) {
        List<Map<String, Object>> currentImages = styleDao.findImagesByStyleId(styleId);
        int currentCount = currentImages != null ? currentImages.size() : 0;
        int maxImages = 5;

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }
            if (currentCount >= maxImages) {
                break; // Enforce max limit of 5
            }
            try {
                String originalFilename = image.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String generatedFilename = UUID.randomUUID().toString() + ext;
                Path uploadDir = Paths.get("src/main/resources/static/images/styles");
                Files.createDirectories(uploadDir);
                Path targetPath = uploadDir.resolve(generatedFilename);
                Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                Path targetClassesDir = Paths.get("target/classes/static/images/styles");
                if (Files.exists(Paths.get("target/classes"))) {
                    Files.createDirectories(targetClassesDir);
                    Files.copy(targetPath, targetClassesDir.resolve(generatedFilename), StandardCopyOption.REPLACE_EXISTING);
                }

                String imagePath = "/images/styles/" + generatedFilename;
                styleDao.insertStyleImage(styleId, imagePath);
                currentCount++;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save style image", e);
            }
        }
    }

    @Transactional
    public void deleteStyleImage(int imageId, int tailorId) {
        Map<String, Object> image = styleDao.findImageById(imageId);
        if (image != null) {
            String imagePath = String.valueOf(image.get("Image_Path"));
            styleDao.deleteStyleImage(imageId);
            auditLogService.logActivity(tailorId, "DELETE", "STYLE_IMAGE", String.valueOf(imageId), imagePath, null);
        }
    }

    public List<Map<String, Object>> getRequirementsForStyle(int styleId) {
        return measurementRequirementDao.findByStyleId(styleId);
    }

    @Transactional
    public void addMeasurementRequirement(int styleId, String name) {
        measurementRequirementDao.addRequirement(styleId, name);
    }

    @Transactional
    public void removeMeasurementRequirement(int styleId, String name) {
        measurementRequirementDao.removeRequirement(styleId, name);
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return new BigDecimal(value.trim()).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}