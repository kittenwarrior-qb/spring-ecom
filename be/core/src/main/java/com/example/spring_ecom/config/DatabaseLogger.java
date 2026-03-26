package com.example.spring_ecom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class DatabaseLogger implements CommandLineRunner {
    private final DataSource dataSource;

    public DatabaseLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("----------------------------------------------------------------------");
            log.info("Database Connected Successfully");
            log.info("Database url: {}", dataSource.getConnection().getMetaData().getURL());
            log.info("Database name: {}", dataSource.getConnection().getMetaData().getUserName());
            log.info("----------------------------------------------------------------------");
            log.info("Note: Database migrations are now handled separately by migration-postgres");
            log.info("----------------------------------------------------------------------");

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }
}
