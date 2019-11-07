package me.exrates.ngService.impl;

import com.google.common.collect.ImmutableSet;
import me.exrates.model.RestrictedCountry;
import me.exrates.model.dto.GeoLocation;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.ngDao.GeoLocationRepository;
import me.exrates.ngService.GeoLocationService;
import me.exrates.ngService.RestrictedCountryService;
import me.exrates.service.util.IpUtils;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class GeoLocationServiceImplTest {

    private static String TEST_IP = "198.162.0.1";
    private static String COUNTRY = "Narnia";
    private static String CITY = "SlamCity";

    private GeoLocationRepository geoLocationRepository = Mockito.mock(GeoLocationRepository.class);
    private RestrictedCountryService restrictedCountryService = Mockito.mock(RestrictedCountryService.class);
    private GeoLocationService geoLocationService = new GeoLocationServiceImpl(geoLocationRepository, restrictedCountryService);

    @Before
    public void setUp() {
        when(geoLocationRepository.findByIP(anyString())).thenReturn(testGeolocation());
    }

    @Test
    public void findById() {
        final GeoLocation geoLocation = geoLocationService.findById(TEST_IP);
        assertEquals(COUNTRY, geoLocation.getCountry());
        assertEquals(CITY, geoLocation.getCity());
    }

    @Test
    public void isCountryRestrictedByIp_IsOk() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        new MockUp<IpUtils>() {
            @Mock
            public String getIpForDbLog(HttpServletRequest request) {
                return TEST_IP;
            }
        };
        when(restrictedCountryService.findAllByOperation(anyObject())).thenReturn(getRestrictedCountries());
        assertTrue(geoLocationService.isCountryRestrictedByIp(request, RestrictedOperation.TRADE));
    }

    private Optional<GeoLocation> testGeolocation() {
        final GeoLocation geoLocation = GeoLocation.builder()
                .country(COUNTRY)
                .region("Golden-Valley")
                .city(CITY)
                .build();
        return Optional.of(geoLocation);
    }

    private Set<RestrictedCountry> getRestrictedCountries() {
        final RestrictedCountry country = RestrictedCountry.builder()
                .countryName(COUNTRY)
                .countryCode("NR")
                .build();
        return ImmutableSet.of(country);
    }
}
