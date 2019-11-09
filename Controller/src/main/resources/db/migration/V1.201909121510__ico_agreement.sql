CREATE TABLE IF NOT EXISTS IEO_USER_AGREEMENT (
    user_id INT(40) PRIMARY KEY                                 NOT NULL,
    ieo_id  INT(11)                                             NOT NULL,
    is_agree TINYINT(1)                                         NOT NULL DEFAULT TRUE,
    CONSTRAINT ieo_user_agreement_user_id_user_fk FOREIGN KEY(user_id) REFERENCES USER(id),
    CONSTRAINT ieo_user_agreement_ieo_id__ieo_details_fk FOREIGN KEY(ieo_id) REFERENCES IEO_DETAILS(id)
);

CREATE TABLE IF NOT EXISTS IEO_AGREEMENT_TEXT (
    ieo_id  INT(11) PRIMARY KEY                                 NOT NULL,
    agreement TEXT,
    CONSTRAINT ieo_user_agreement_ieo_id_user_fk FOREIGN KEY(ieo_id) REFERENCES USER(id)
)
