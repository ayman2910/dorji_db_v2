You are an expert Java Spring Boot developer, database architect, and Thymeleaf expert.
This project is a STRICTLY SQL-first, server-rendered application for a university assignment.

CRITICAL ARCHITECTURAL RULES:
1. NO ORM: Do NOT use JPA, Hibernate, Spring Data JPA, CrudRepository, or @Entity annotations.
2. DATABASE ACCESS: Use JDBC and Spring's JdbcTemplate with explicit, parameterized SQL queries in DAO classes.
3. NO SPA/FRONTEND FRAMEWORKS: Do NOT use React, Vue, Angular, or Next.js. Do NOT build a REST API (no @RestController unless explicitly asked for a specific reason).
4. SERVER-SIDE RENDERING: Use Spring MVC (@Controller) and Thymeleaf templates for all views.
5. STYLING: Use Tailwind CSS via CDN or standard CSS within the Thymeleaf templates.
6. SOURCE OF TRUTH: The MySQL relational schema is the absolute source of truth. Rely on database constraints (FOREIGN KEY, CHECK, UNIQUE).

ARCHITECTURE LAYERS:
- Controller: Handles HTTP GET/POST, maps to Thymeleaf views.
- Service: Contains business logic, validation, and @Transactional boundaries.
- DAO: Contains pure SQL queries using JdbcTemplate.
- DTO/Model: Plain Java objects for mapping data, NOT database entities.