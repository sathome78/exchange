package me.exrates.service;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface AlgorithmService {

    String computeMD5Hash(String string);

    byte[] computeMD5Byte(String string);

    String sha1(String string);

    String sha256(String string);

    String base64Encode(String string);

    String base64Decode(String string);

    BigDecimal computeAmount(BigDecimal amount, BigDecimal commission, OperationType type);

    BigDecimal computeCommission(BigDecimal amount, OperationType type);

    BigDecimal fromSatoshi(String amount);

    BigDecimal toBigDecimal(String value);

    String encodeByKey(String code, String txt);

    String decodeByKey(String code, String text);
}
