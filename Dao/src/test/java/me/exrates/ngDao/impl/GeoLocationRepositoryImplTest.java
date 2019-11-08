package me.exrates.ngDao.impl;

import me.exrates.model.dto.GeoLocation;
import me.exrates.ngDao.GeoLocationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GeoLocationRepositoryImplTest.InnerConfig.class)
public class GeoLocationRepositoryImplTest {

    private final String UNDEFINED = "UNDEFINED";

    @Autowired
    private GeoLocationRepository geoLocationRepository;

    @Test
    public void findById_whenIpIsValid() {
        final Optional<GeoLocation> result = geoLocationRepository.findByIP("3.18.138.166");
        assertTrue(result.isPresent());

        final GeoLocation geoLocation = result.get();
        assertEquals("United States", geoLocation.getCountry());
        assertEquals("Ohio", geoLocation.getRegion());
        assertEquals("Columbus", geoLocation.getCity());
    }

    @Test
    public void findById_whenIpIsPartiallyValid() {
        final Optional<GeoLocation> result = geoLocationRepository.findByIP("123.18.138.166");
        assertTrue(result.isPresent());

        final GeoLocation geoLocation = result.get();
        assertEquals("Vietnam", geoLocation.getCountry());
        assertNull(geoLocation.getRegion());
        assertNull(geoLocation.getCity());
    }

    @Test
    public void findById_whenIpIsInvalid() {
        final Optional<GeoLocation> result = geoLocationRepository.findByIP("192.168.0.1");
        assertTrue(result.isPresent());

        final GeoLocation geoLocation = result.get();
        assertEquals(UNDEFINED, geoLocation.getCountry());
        assertEquals(UNDEFINED, geoLocation.getRegion());
        assertEquals(UNDEFINED, geoLocation.getCity());
    }

    @Configuration
    static class InnerConfig {

        @Bean
        public GeoLocationRepository geoLocationRepository() {
            String dbFilePath = System.getProperty("geo.database.file.path", "./src/test/resources/geo-lite-city/GeoLite2-City.mmdb");
            return new GeoLocationRepositoryImpl(dbFilePath);
        }
    }
}
