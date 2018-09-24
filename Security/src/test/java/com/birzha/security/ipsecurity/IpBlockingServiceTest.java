package com.birzha.security.ipsecurity;


import me.exrates.security.exception.BannedIpException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations = "classpath:ipsecurity-context.xml")
public class IpBlockingServiceTest {

    @Autowired
    IpBlockingService ipBlockingService;

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            ipBlockingService.failureProcessing("1", IpTypesOfChecking.OPEN_API);
        }
        try {
            ipBlockingService.checkIp("1", IpTypesOfChecking.OPEN_API);
            org.junit.Assert.fail("Exception should be thrown");
        } catch (Exception e) {
            assertEquals("IP banned: number of incorrect attempts exceeded!", e.getMessage());
            assertEquals(BannedIpException.class, e.getClass());
        }
    }
}
