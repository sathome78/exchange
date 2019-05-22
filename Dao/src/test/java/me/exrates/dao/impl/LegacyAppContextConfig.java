package me.exrates.dao.impl;

import config.AbstractDatabaseContextTest;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class LegacyAppContextConfig extends AbstractDatabaseContextTest.AppContextConfig {

    @Override
    protected String getSchema() {
        return StringUtils.isNotBlank(databaseConfig.getRootSchemeName())
                ? databaseConfig.getRootSchemeName()
                : "birzha";
    }
}
