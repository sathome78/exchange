package me.exrates.dao.impl;

import config.AbstractDatabaseContextTest;

@Deprecated
public class LegacyAppContextConfig extends AbstractDatabaseContextTest.AppContextConfig {

    @Override
    protected String getSchema() {
        return "birzha";
    }
}
