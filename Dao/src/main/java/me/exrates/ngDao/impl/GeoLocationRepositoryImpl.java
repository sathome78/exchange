package me.exrates.ngDao.impl;

import com.google.common.annotations.VisibleForTesting;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.GeoLocation;
import me.exrates.ngDao.GeoLocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Log4j2
@Repository
@PropertySource(value = {"classpath:/angular.properties"})
public class GeoLocationRepositoryImpl implements GeoLocationRepository {

    private DatabaseReader dbReader;

    public GeoLocationRepositoryImpl(@Value("${geo.database.file.path}") String dbFilePath) {
        try {
            File dbFile = dbFilePath.startsWith("/geo")
                    ? new ClassPathResource(dbFilePath).getFile()
                    : new File(dbFilePath);
            dbReader = new DatabaseReader.Builder(dbFile).build();
        } catch (IOException e) {
            log.error("Failed to find geo database file", e);
            e.printStackTrace();
        }
    }

    @Override
    public Optional<GeoLocation> findByIP(String ipAddress) {
        return Optional.ofNullable(findLocation(ipAddress));
    }

    @VisibleForTesting
    protected GeoLocation findLocation(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);
            return getGeoLocation(response);
        } catch (UnknownHostException e) {
            log.info("Failed to define valid inet address for ip: " + ip, e);
        } catch (GeoIp2Exception | IOException e) {
            log.info("Failed to find city response for ip: " + ip, e);
        }
        return GeoLocation.empty();
    }

    private GeoLocation getGeoLocation(CityResponse response) {
        return GeoLocation.builder()
                .country(response.getCountry().getName())
                .region(response.getLeastSpecificSubdivision().getName())
                .city(response.getCity().getName())
                .build();
    }
}
