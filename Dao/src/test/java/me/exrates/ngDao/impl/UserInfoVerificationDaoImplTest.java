package me.exrates.ngDao.impl;

import me.exrates.dao.util.AbstractDatabaseContextTest;
import me.exrates.dao.util.DataComparisonTest;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.fail;

public class UserInfoVerificationDaoImplTest extends DataComparisonTest {

    public UserInfoVerificationDaoImplTest() {
        super("UserInfoVerificationDaoImplTest");
    }

    @Test
    public void save() {
        fail();
    }

    @Test
    public void delete() {
        fail();
    }

    @Test
    public void findByUserId() {
        fail();
    }


    @Configuration
    static class Context extends AbstractDatabaseContextTest.AppContextConfig {

    }
}
