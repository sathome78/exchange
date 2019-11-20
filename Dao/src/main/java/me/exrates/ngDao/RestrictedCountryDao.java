package me.exrates.ngDao;

import me.exrates.model.RestrictedCountry;
import me.exrates.model.enums.RestrictedOperation;

import java.util.Optional;
import java.util.Set;

public interface RestrictedCountryDao {

    String TABLE_NAME = "RESTRICTED_COUNTRY";
    String COLUMN_ID = "id";
    String COLUMN_RESTRICTED_OP_NAME =  "restricted_operation";
    String COLUMN_COUNTRY_NAME =  "country_name";
    String COLUMN_COUNTRY_CODE =  "country_code";

    RestrictedCountry save(RestrictedCountry restrictedCountry);

    Optional<RestrictedCountry> findById(int restrictedCountryId);

    Set<RestrictedCountry> findAll(RestrictedOperation operation);

    Set<RestrictedCountry> findAll();

    boolean delete(int restrictedCountryId);

    boolean delete(RestrictedOperation operation, String countryCode);
}
