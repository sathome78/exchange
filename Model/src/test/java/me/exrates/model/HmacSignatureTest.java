package me.exrates.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HmacSignatureTest {

    private HmacSignature hmacSignature;

    private String algorithm;
    private String delimiter;
    private String requestMethod;
    private String endpoint;
    private Long timestamp;
    private String publicKey;
    private String apiSecret;

    private String expectedSignature = "2deaeb9262a5d0849e0eeb0ae7098dafb56c5a1ad7b2b049ef58b6c3bdf6d843";



    @Before
    public void setUp() {
        algorithm = "HmacSHA256";
        delimiter = "|";
        requestMethod = "GET";
        endpoint = "/openapi/v1/orders/create";
        timestamp = 1111111L;
        publicKey = "qwerty";
        apiSecret = "ytrewq";
    }

    @Test
    public void testBuildCorrect() {
        hmacSignature = defaultBuild();
        assertEquals(hmacSignature.getSignatureHexString(), expectedSignature);
    }

    private HmacSignature defaultBuild() {
        return new HmacSignature.Builder()
                .algorithm(algorithm)
                .delimiter(delimiter)
                .requestMethod(requestMethod)
                .endpoint(endpoint)
                .timestamp(timestamp)
                .publicKey(publicKey)
                .apiSecret(apiSecret)
                .build();
    }

    @Test
    public void testCompareSignaturesCorrect() {
        hmacSignature = defaultBuild();
        assertTrue(hmacSignature.isSignatureEqual(expectedSignature));
    }

    @Test
    public void testCompareSignaturesIncorrect() {
        hmacSignature = defaultBuild();
        expectedSignature = "2dcaeb9262a5d0849e0eeb0ae7098dafb56c5a1ad7b2b049ef58b6c3bdf6d843";
        assertFalse(hmacSignature.isSignatureEqual(expectedSignature));
    }

}
