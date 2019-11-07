package me.exrates.ngDao;

import me.exrates.model.dto.GeoLocation;
import java.util.Optional;

public interface GeoLocationRepository {

    Optional<GeoLocation> findByIP(String ipAddress);
}
