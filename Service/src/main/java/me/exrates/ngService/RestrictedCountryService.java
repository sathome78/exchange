package me.exrates.ngService;

import me.exrates.model.RestrictedCountry;
import me.exrates.model.enums.RestrictedOperation;

import java.util.Set;

public interface RestrictedCountryService {

    RestrictedCountry save(RestrictedCountry restrictedCountry);

    Set<RestrictedCountry> findAllByOperation(RestrictedOperation ... operations);

    boolean delete(RestrictedCountry restrictedCountry);

    boolean delete(RestrictedOperation operation, String countryName);
}
