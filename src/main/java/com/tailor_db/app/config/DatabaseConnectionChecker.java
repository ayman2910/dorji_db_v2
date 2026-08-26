package com.tailor_db.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionChecker implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

	private final JdbcTemplate jdbcTemplate;

	public DatabaseConnectionChecker(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void run(String... args) {
		Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM APP_USER;", Integer.class);
		log.info("Database connection successful. APP_USER row count: {}", userCount);
	}
}
