package me.exrates.dao.impl;

import config.AbstractDatabaseContextTest;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Deprecated
public class LegacyAppContextConfig extends AbstractDatabaseContextTest.AppContextConfig {

    @Override
    protected String getSchema() {
        return Objects.nonNull(databaseConfig) && StringUtils.isNotBlank(databaseConfig.getRootSchemeName())
                ? databaseConfig.getRootSchemeName()
                : "birzha";
    }
}
