package me.exrates.ngService;

import me.exrates.model.dto.GeoLocation;
import me.exrates.model.enums.RestrictedOperation;

import javax.servlet.http.HttpServletRequest;

public interface GeoLocationService {

    GeoLocation findById(String ip);

    boolean isCountryRestrictedByIp(HttpServletRequest request, RestrictedOperation ... trade);
}
