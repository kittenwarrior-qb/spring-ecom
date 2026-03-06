package com.example.spring_ecom.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;

@Slf4j
@Component
public class DatabaseLogger implements CommandLineRunner {
    private final DataSource dataSource;
    private final Flyway flyway;

    public DatabaseLogger(DataSource dataSource, @Autowired(required = false) Flyway flyway) {
        this.dataSource = dataSource;
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("----------------------------------------------------------------------");
            log.info("Database Connected Successfully");
            log.info("Database url: {}", dataSource.getConnection().getMetaData().getURL());
            log.info("Database name: {}", dataSource.getConnection().getMetaData().getUserName());
            log.info("----------------------------------------------------------------------");

            // Check Flyway migrations
            if (flyway != null) {
                log.info("Flyway Migration Status:");
                MigrationInfo[] migrations = flyway.info().all();

                if (migrations.length == 0) {
                    log.warn("No migrations found!");
                } else {
                    for (MigrationInfo migration : migrations) {
                        log.info("Version: {} | Description: {} | State: {} | Installed on: {}",
                                migration.getVersion(),
                                migration.getDescription(),
                                migration.getState(),
                                migration.getInstalledOn());
                    }
                }
                log.info("Total migrations: {}", migrations.length);
            } else {
                log.warn("Flyway is not configured or enabled!");
            }
            log.info("----------------------------------------------------------------------");

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }
}
