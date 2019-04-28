package com.medxoom;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class Migration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Migration.class);

    public void apply(DataSource dataSource, String description) {
        LOGGER.info("Running migration for " + description);

        try {
            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.migrate();
        } catch (Exception e) {
            throw new RuntimeException("Unable to migrate database " + description, e);
        }
    }
}