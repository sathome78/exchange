UPDATE MERCHANT_SPEC_PARAMETERS
set param_value = 64653000
where merchant_id = (SELECT id FROM MERCHANT WHERE name = 'EOS');