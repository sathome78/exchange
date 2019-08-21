package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.RefillRequestDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RefillRequestDaoTest.InnerConf.class})
public class RefillRequestDaoTest extends DataComparisonTest {

    private final String TABLE_REFILL_REQUEST = "REFILL_REQUEST";

    @Autowired
    private RefillRequestDao refillRequestDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE_REFILL_REQUEST);
            String sql = "INSERT INTO REFILL_REQUEST"
                    + " (id, amount, date_creation, status_id, currency_id, user_id, commission_id, merchant_id, remark)"
                    + " VALUES "
                    + " (1, 100.00, NOW(), 1, 1, 1, 1, 1, 'PENDING')";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getRefillRequestByMerchantIdAndRemark() {
        String sql = "SELECT * FROM " + TABLE_REFILL_REQUEST;
        around()
                .withSQL(sql)
                .run(() -> refillRequestDao.getByMerchantIdAndRemark(1, "PENDING"));
    }

    @Test
    public void updateRemarkAndTransactionIdkRefillRequestByMerchantId() {
        String sql = "SELECT * FROM " + TABLE_REFILL_REQUEST;
        around()
                .withSQL(sql)
                .run(() -> refillRequestDao.setRemarkAndTransactionIdById( "SUCCESS", "some_transaction_id", 1));
    }

    @Configuration
    static class InnerConf extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "RefillRequestDaoImplTest";
        }

        @Bean
        public RefillRequestDao requestDao() {
            return new RefillRequestDaoImpl();
        }
    }
}
