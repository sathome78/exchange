package me.exrates.dao.impl;

import me.exrates.dao.KycDao;
import me.exrates.model.Gender;
import me.exrates.model.kyc.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class KycDaoImpl implements KycDao {

    private static final Logger LOG = LogManager.getLogger(KycDao.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int saveIndividual(int userId, KYC kyc) {
        String personSQL = "insert into KYC_PERSON(name,surname,middle_name,gender,birth_date,phone,nationality,id_number,confirm_document_path)" +
                "values(:name,:surname,:middle_name,:gender,:birth_date,:phone,:nationality,:id_number,:confirm_document_path)";
        MapSqlParameterSource personParams = new MapSqlParameterSource();
        personParams.addValue("name", null);
        personParams.addValue("surname", null);
        personParams.addValue("middle_name", null);
        personParams.addValue("name", kyc.getPerson().getName());
        personParams.addValue("surname", kyc.getPerson().getSurname());
        personParams.addValue("middle_name", kyc.getPerson().getMiddleName());
        personParams.addValue("gender", kyc.getPerson().getGender().getName());
        personParams.addValue("birth_date", kyc.getPerson().getBirthDate());
        personParams.addValue("phone", kyc.getPerson().getPhone());
        personParams.addValue("nationality", kyc.getPerson().getNationality());
        personParams.addValue("id_number", kyc.getPerson().getIdNumber());
        personParams.addValue("confirm_document_path", kyc.getPerson().getConfirmDocumentPath());
        KeyHolder personKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(personSQL, personParams, personKeyHolder);

        String sqlAddress =  "insert into KYC_ADDRESS(country,city,street,zip_code) values(:country,:city,:street,:zip_code)";
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("country", kyc.getAddress().getCountry());
        addressParams.addValue("city", kyc.getAddress().getCity());
        addressParams.addValue("street", kyc.getAddress().getStreet());
        addressParams.addValue("zip_code", kyc.getAddress().getZipCode());
        KeyHolder addressKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlAddress, addressParams, addressKeyHolder);

        String kycSQL = "insert into KYC(kyc_type,kyc_status,user_id,address_id,person_id)" +
                "values(:kyc_type,:kyc_status,:user_id,:address_id,:person_id)";
        MapSqlParameterSource kycParams = new MapSqlParameterSource();
        kycParams.addValue("kyc_type", kyc.getKycType().getName());
        kycParams.addValue("kyc_status", kyc.getKycStatus().getName());
        kycParams.addValue("user_id", userId);
        kycParams.addValue("person_id", personKeyHolder.getKey().intValue());
        kycParams.addValue("address_id", addressKeyHolder.getKey().intValue());
        KeyHolder kycKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(kycSQL, kycParams, kycKeyHolder);

        return kycKeyHolder.getKey().intValue();
    }

    @Override
    public int saveLegalEntity(int userId, KYC kyc) {
        String personSQL = "insert into KYC_PERSON(position,name,surname,middle_name,phone,confirm_document_path)" +
                "values(:position,:name,:surname,:middle_name,:phone,:confirm_document_path)";
        MapSqlParameterSource personParams = new MapSqlParameterSource();
        personParams.addValue("position", kyc.getPerson().getPosition());
        personParams.addValue("name", null);
        personParams.addValue("surname", null);
        personParams.addValue("middle_name", null);
        personParams.addValue("name", kyc.getPerson().getName());
        personParams.addValue("surname", kyc.getPerson().getSurname());
        personParams.addValue("middle_name", kyc.getPerson().getMiddleName());
        personParams.addValue("phone", kyc.getPerson().getPhone());
        personParams.addValue("confirm_document_path", kyc.getPerson().getConfirmDocumentPath());
        KeyHolder personKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(personSQL, personParams, personKeyHolder);

        String sqlAddress =  "insert into KYC_ADDRESS(country,city,street,zip_code) values(:country,:city,:street,:zip_code)";
        MapSqlParameterSource addressParams = new MapSqlParameterSource();
        addressParams.addValue("country", kyc.getAddress().getCountry());
        addressParams.addValue("city", kyc.getAddress().getCity());
        addressParams.addValue("street", kyc.getAddress().getStreet());
        addressParams.addValue("zip_code", kyc.getAddress().getZipCode());
        KeyHolder addressKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlAddress, addressParams, addressKeyHolder);

        String kycSQL = "insert into KYC(company_name,reg_country,reg_number,kyc_type,kyc_status,user_id,address_id,person_id,commercial_registry_path,company_charter_path)" +
                "values(:company_name,:reg_country,:reg_number,:kyc_type,:kyc_status,:user_id,:address_id,:person_id,:commercial_registry_path,:company_charter_path)";
        MapSqlParameterSource kycParams = new MapSqlParameterSource();
        kycParams.addValue("company_name", kyc.getCompanyName());
        kycParams.addValue("reg_country", kyc.getRegCountry());
        kycParams.addValue("reg_number", kyc.getRegNumber());
        kycParams.addValue("kyc_type", kyc.getKycType().getName());
        kycParams.addValue("kyc_status", kyc.getKycStatus().getName());
        kycParams.addValue("user_id", userId);
        kycParams.addValue("person_id", personKeyHolder.getKey().intValue());
        kycParams.addValue("address_id", addressKeyHolder.getKey().intValue());
        kycParams.addValue("commercial_registry_path", kyc.getCommercialRegistryPath());
        kycParams.addValue("company_charter_path", kyc.getCompanyCharterPath());
        KeyHolder kycKeyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(kycSQL, kycParams, kycKeyHolder);
        return kycKeyHolder.getKey().intValue();
    }

    @Override
    public int updateIndividual(int userId, KYC kyc) {
        KycInfo info = this.getInfo(userId);
        KycPerson person = kyc.getPerson();
        KycAddress address = kyc.getAddress();

        String personSQL = "UPDATE KYC_PERSON SET";
        StringBuilder fieldsStr = new StringBuilder(" ");
        if (person.getName() != null) {
            fieldsStr.append("name = '" + person.getName()).append("',");
        }
        if (person.getSurname() != null) {
            fieldsStr.append("surname = '" + person.getSurname()).append("',");
        }
        if (person.getMiddleName() != null) {
            fieldsStr.append("middle_name = '" + person.getMiddleName()).append("',");
        }
        if (person.getBirthDate() != null) {
            fieldsStr.append("birth_date = '" + person.getBirthDate()).append("',");
        }
        if (person.getNationality() != null) {
            fieldsStr.append("nationality = '" + person.getNationality()).append("',");
        }
        if (person.getIdNumber() != null) {
            fieldsStr.append("id_number = '" + person.getIdNumber()).append("',");
        }
        if (person.getPhone() != null) {
            fieldsStr.append("phone = '" + person.getPhone()).append("',");
        }
        if (person.getGender() != null) {
            fieldsStr.append("gender = '" + person.getGender().getName()).append("',");
        }
        if (person.getConfirmDocumentPath() != null) {
            fieldsStr.append("confirm_document_path = '" + person.getConfirmDocumentPath()).append("',");
        }
        personSQL = personSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        Map<String, Integer> params = new HashMap<>();
        params.put("id", info.getPersonId());
        namedParameterJdbcTemplate.update(personSQL, params);

        String addressSQL = "UPDATE KYC_ADDRESS SET";
        fieldsStr = new StringBuilder(" ");
        if (address.getCountry() != null) {
            fieldsStr.append("country = '" + address.getCountry()).append("',");
        }
        if (address.getCity() != null) {
            fieldsStr.append("city = '" + address.getCity()).append("',");
        }
        if (address.getStreet() != null) {
            fieldsStr.append("street = '" + address.getStreet()).append("',");
        }
        if (address.getZipCode() != null) {
            fieldsStr.append("zip_code = '" + address.getZipCode()).append("',");
        }
        addressSQL = addressSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        params.clear();
        params.put("id", info.getAddressId());
        namedParameterJdbcTemplate.update(addressSQL, params);

        String kycSQL = "UPDATE KYC SET";
        fieldsStr = new StringBuilder(" ");
        if (kyc.getKycType() != null) {
            fieldsStr.append("kyc_type = '" + kyc.getKycType().getName()).append("',");
        }
        kycSQL = kycSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        params.clear();
        params.put("id", info.getId());
        namedParameterJdbcTemplate.update(kycSQL, params);

        return info.getId();
    }

    @Override
    public int updateLegalEntity(int userId, KYC kyc) {
        KycInfo info = this.getInfo(userId);
        KycPerson person = kyc.getPerson();
        KycAddress address = kyc.getAddress();

        String personSQL = "UPDATE KYC_PERSON SET";
        StringBuilder fieldsStr = new StringBuilder(" ");
        if (person.getPosition() != null) {
            fieldsStr.append("position = '" + person.getPosition()).append("',");
        }
        if (person.getName() != null) {
            fieldsStr.append("name = '" + person.getName()).append("',");
        }
        if (person.getSurname() != null) {
            fieldsStr.append("surname = '" + person.getSurname()).append("',");
        }
        if (person.getMiddleName() != null) {
            fieldsStr.append("middle_name = '" + person.getMiddleName()).append("',");
        }
        if (person.getPhone() != null) {
            fieldsStr.append("phone = '" + person.getPhone()).append("',");
        }
        if (person.getConfirmDocumentPath() != null) {
            fieldsStr.append("confirm_document_path = '" + person.getConfirmDocumentPath()).append("',");
        }
        personSQL = personSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        Map<String, Integer> params = new HashMap<>();
        params.put("id", info.getPersonId());
        namedParameterJdbcTemplate.update(personSQL, params);

        String addressSQL = "UPDATE KYC_ADDRESS SET";
        fieldsStr = new StringBuilder(" ");
        if (address.getCountry() != null) {
            fieldsStr.append("country = '" + address.getCountry()).append("',");
        }
        if (address.getCity() != null) {
            fieldsStr.append("city = '" + address.getCity()).append("',");
        }
        if (address.getStreet() != null) {
            fieldsStr.append("street = '" + address.getStreet()).append("',");
        }
        if (address.getZipCode() != null) {
            fieldsStr.append("zip_code = '" + address.getZipCode()).append("',");
        }
        addressSQL = addressSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        params.clear();
        params.put("id", info.getAddressId());
        namedParameterJdbcTemplate.update(addressSQL, params);

        String kycSQL = "UPDATE KYC SET";
        fieldsStr = new StringBuilder(" ");
        if (kyc.getKycType() != null) {
            fieldsStr.append("kyc_type = '" + kyc.getKycType().getName()).append("',");
        }
        if (kyc.getCompanyName() != null) {
            fieldsStr.append("company_name = '" + kyc.getCompanyName()).append("',");
        }
        if (kyc.getRegCountry() != null) {
            fieldsStr.append("reg_country = '" + kyc.getRegCountry()).append("',");
        }
        if (kyc.getRegNumber() != null) {
            fieldsStr.append("reg_number = '" + kyc.getRegNumber()).append("',");
        }
        if (kyc.getCommercialRegistryPath() != null) {
            fieldsStr.append("commercial_registry_path = '" + kyc.getCommercialRegistryPath()).append("',");
        }
        if (kyc.getCompanyCharterPath() != null) {
            fieldsStr.append("company_charter_path = '" + kyc.getCompanyCharterPath()).append("',");
        }
        kycSQL = kycSQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE id = :id";
        params.clear();
        params.put("id", info.getId());
        namedParameterJdbcTemplate.update(kycSQL, params);

        return info.getId();
    }

    @Override
    public KycInfo getInfo(int userId) {
        String SQL = "select * from KYC where user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(SQL, params, (rs, row) -> {
                KycInfo kyc = new KycInfo();
                kyc.setId(rs.getInt("id"));
                kyc.setAddressId(rs.getInt("address_id"));
                kyc.setPersonId(rs.getInt("person_id"));
                kyc.setAdmin(rs.getString("admin"));
                kyc.setKycType(Enum.valueOf(KycType.class, rs.getString("kyc_type")));
                kyc.setKycStatus(Enum.valueOf(KycStatus.class, rs.getString("kyc_status")));
                return kyc;
            });
        } catch (EmptyResultDataAccessException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public KYC getDetailed(int userId) {
        String SQL = "select KYC.id, KYC.user_id, KYC.kyc_type, KYC.kyc_status, KYC.company_name, KYC.reg_country, KYC.reg_number, KYC.commercial_registry_path,"
        + "KYC.admin, KYC.company_charter_path, KYC_PERSON.position as position, KYC_PERSON.name as name, KYC_PERSON.surname as surname,"
        + "KYC_PERSON.middle_name as middle_name, KYC_PERSON.phone as phone,KYC_PERSON.confirm_document_path as confirm_document_path,"
        + "KYC_PERSON.nationality as nationality, KYC_PERSON.id_number as id_number, KYC_PERSON.gender as gender, KYC_PERSON.birth_date as birth_date ,"
        + "KYC_ADDRESS.country as country, KYC_ADDRESS.city as city, KYC_ADDRESS.street as street, KYC_ADDRESS.zip_code as zip_code "
        + "from KYC RIGHT JOIN KYC_PERSON on KYC.person_id = KYC_PERSON.id RIGHT JOIN KYC_ADDRESS on KYC.address_id = KYC_ADDRESS.id "
        + "where user_id = :user_id";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(SQL, namedParameters,(rs, row) -> {
                KYC kyc = new KYC();
                KycPerson person = new KycPerson();
                KycAddress address = new KycAddress();
                kyc.setId(rs.getInt("id"));
                kyc.setUserId(rs.getInt("user_id"));
                if (rs.getString("admin") != null) {
                    kyc.setAdmin(rs.getString("admin"));
                }
                kyc.setKycType(Enum.valueOf(KycType.class, rs.getString("kyc_type")));
                kyc.setKycStatus(Enum.valueOf(KycStatus.class, rs.getString("kyc_status")));
                kyc.setCompanyName(rs.getString("company_name"));
                kyc.setRegCountry(rs.getString("reg_country"));
                kyc.setRegNumber(rs.getString("reg_number"));
                kyc.setCommercialRegistryPath(rs.getString("commercial_registry_path"));
                kyc.setCompanyCharterPath(rs.getString("company_charter_path"));

                person.setPosition(rs.getString("position"));
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));
                person.setMiddleName(rs.getString("middle_name"));
                person.setBirthDate(rs.getString("birth_date"));
                person.setPhone(rs.getString("phone"));
                person.setConfirmDocumentPath(rs.getString("confirm_document_path"));
                person.setNationality(rs.getString("nationality"));
                person.setIdNumber(rs.getString("id_number"));
                if (rs.getString("gender") != null) {
                    person.setGender(Enum.valueOf(Gender.class, rs.getString("gender")));
                }
                address.setCountry(rs.getString("country"));
                address.setCity(rs.getString("city"));
                address.setStreet(rs.getString("street"));
                address.setZipCode(rs.getString("zip_code"));

                kyc.setPerson(person);
                kyc.setAddress(address);
                return kyc;
            });
        } catch (EmptyResultDataAccessException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public void setStatus(int userId, KycStatus status, String admin) {
        String SQL = "UPDATE KYC SET kyc_status = :kyc_status";
        StringBuilder fieldsStr = new StringBuilder(" ");
        if (admin != null) {
            fieldsStr.append(",admin = '" + admin).append("',");
        }
        SQL = SQL + fieldsStr.toString().replaceAll(",$", " ") + "WHERE user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("kyc_status", status.getName());
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(SQL, params);
    }

    @Override
    public boolean inProgress(int userId) {
        return this.getInfo(userId) != null;
    }

    @Override
    public KycStatus getKycStatus(int userId) {
        String SQL = "select kyc_status from KYC where user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(SQL, params, (rs, row) -> {
                return Enum.valueOf(KycStatus.class, rs.getString("kyc_status"));
            });
        } catch (EmptyResultDataAccessException e) {
            LOG.error(e);
            return null;
        }
    }
}
