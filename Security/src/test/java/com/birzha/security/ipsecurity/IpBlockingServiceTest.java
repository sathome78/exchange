package com.birzha.security.ipsecurity;


import me.exrates.security.exception.BannedIpException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpBlockingServiceImpl;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IpBlockingServiceTest.Config.class)
public class IpBlockingServiceTest {

    @Autowired
    IpBlockingService ipBlockingService;

    @Before
    public void setUp(){
        ipBlockingService.successfulProcessing("1", IpTypesOfChecking.OPEN_API);
    }

    @Test
    public void testLongBan() {
        for (int i = 0; i < 10; i++) {
            ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        }
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
            org.junit.Assert.fail("Exception should be thrown");
        } catch (Exception e) {
            String expected = "IP 1 is banned for 10 minutes: number of incorrect attempts exceeded: [10]!";
            assertEquals(expected, e.getMessage());
            assertEquals(BannedIpException.class, e.getClass());
        }
    }

    @Test
    public void testShortBanAfterBanTimeOver() {
        for (int i = 0; i < 3; i++) {
            ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        }
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
        } catch (BannedIpException e) {
            fail("It must be ok as only three tries are used");
        }
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
        } catch (BannedIpException e) {
            fail("It must be ok as ban time after three tries are over");
        }
    }

    @Test
    public void testShortBanAfterBanTimeNotOver() {
        for (int i = 0; i < 3; i++) {
            ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        }
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
        } catch (BannedIpException e) {
            fail("It must be ok as only three tries are used");
        }
        ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
            org.junit.Assert.fail("Exception should be thrown");
        } catch (Exception e) {
            String expected = "IP 1 is banned for 0 minutes: number of incorrect attempts exceeded: [4]!";
            assertEquals(expected, e.getMessage());
            assertEquals(BannedIpException.class, e.getClass());
        }
    }

    @Test
    public void testNotBannedIp() {
        for (int i = 0; i < 3; i++) {
            ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        }
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
        } catch (BannedIpException e) {
            fail("It must be ok as only three tries are used");
        }
    }

    @Configuration
    @PropertySource(value = {"classpath:/ip_ban.properties"})
    static class Config {

        @Bean
        public IpBlockingService ipBlockingService() {
            return new IpBlockingServiceImpl();
        }

    }
}
