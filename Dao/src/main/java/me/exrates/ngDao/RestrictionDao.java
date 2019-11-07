package me.exrates.ngDao;

import me.exrates.model.Restriction;

import java.util.List;
import java.util.Optional;

public interface RestrictionDao {

    String TABLE_NAME = "DYNAMIC_RESTRICTION";
    String COLUMN_ID = "id";
    String COLUMN_PAIR_NAME =  "currency_pair_name";
    String COLUMN_NAME =  "res_name";
    String COLUMN_DESCRIPTION =  "res_description";
    String COLUMN_CONDITION = "res_condition";
    String COLUMN_ERROR_CODE = "error_code";
    String COLUMN_ERROR_MESSAGE = "error_message";

    Restriction save(Restriction restriction);

    boolean delete(int restrictionId);

    Optional<Restriction> findById(int restrictionId);

    List<Restriction> findAll();

    List<Restriction> findAllByPairName(String pairName);

    boolean matches(String sql);
}
