ALTER TABLE IEO_USER_AGREEMENT
    DROP FOREIGN KEY ieo_user_agreement_user_id_user_fk;

ALTER TABLE IEO_USER_AGREEMENT
    DROP FOREIGN KEY ieo_user_agreement_ieo_id__ieo_details_fk;

ALTER TABLE IEO_USER_AGREEMENT
    DROP PRIMARY KEY;

ALTER TABLE IEO_USER_AGREEMENT
    ADD PRIMARY KEY (user_id, ieo_id);

ALTER TABLE IEO_USER_AGREEMENT ADD CONSTRAINT ieo_user_agreement_user_id_user_fk FOREIGN KEY(user_id) REFERENCES USER(id);
ALTER TABLE IEO_USER_AGREEMENT ADD CONSTRAINT ieo_user_agreement_ieo_id__ieo_details_fk FOREIGN KEY(ieo_id) REFERENCES IEO_DETAILS(id);
