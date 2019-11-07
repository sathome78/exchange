package me.exrates.ngService;

import me.exrates.model.dto.GeoLocation;

public interface GeoLocationService {

    GeoLocation findById(String ip);
}
