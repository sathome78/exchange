package me.exrates.service.tron;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.TronTransferDto;
import me.exrates.model.dto.TronTransferDtoTRC20;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j2(topic = "tron")
@Service
@Conditional(MonolitConditional.class)
@PropertySource("classpath:/merchants/tron.properties")
public class TronNodeServiceImpl implements TronNodeService {

    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            .toCharArray();
    private static final int[] INDEXES = new int[128];
    private static final int ADDRESS_SIZE = 21;
    private static byte addressPreFixByte = (byte) 0x41;

    static {
        for (int i = 0; i < INDEXES.length; i++) {
            INDEXES[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }

    private final RestTemplate restTemplate;

    private @Value("${tron.full_node.url}")String FULL_NODE_URL;
    private @Value("${tron.full_node_for_send.url}")String FULL_NODE_FOR_SEND_URL;
    private @Value("${tron.solidity_node_url}")String SOLIDITY_NODE_URL;
    private @Value("${tron.explorer.api}")String EXPLORER_API;


    private final static String GET_ADDRESS = "/wallet/generateaddress";
    private final static String EASY_TRANSFER = "/wallet/easytransferbyprivate";
    private final static String EASY_TRANSFER_ASSET = "/wallet/easytransferassetbyprivate";
    private final static String CREATE_TRANSACTION = "/wallet/createtransaction";
    private final static String SIGN_TRANSACTION = "/wallet/gettransactionsign";
    private final static String BROADCAST_TRANSACTION = "/wallet/broadcasttransaction ";
    private final static String GET_BLOCK_TX = "/wallet/getblockbynum";
    private final static String GET_TX = "/transaction-info?hash=";
    private final static String GET_LAST_BLOCK = "/wallet/getnowblock";
    private final static String GET_ACCOUNT_INFO = "/wallet/getaccount";

    @Autowired
    public TronNodeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public TronNewAddressDto getNewAddress() {
        String url = FULL_NODE_URL.concat(GET_ADDRESS);
        log.debug("trx url " + url);
        return TronNewAddressDto.fromGetAddressMethod(restTemplate.postForObject(url, null, String.class));
    }

    @SneakyThrows
    @Override
    public JSONObject transferFunds(TronTransferDto tronTransferDto) {
        String url = FULL_NODE_FOR_SEND_URL.concat(EASY_TRANSFER);
        RequestEntity<TronTransferDto> requestEntity = new RequestEntity<>(tronTransferDto, HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    //next 3 methods only for TRON trc20 until developers won`t create id for coin
    @SneakyThrows
    @Override
    public JSONObject transferFundsTRC20(TronTransferDtoTRC20 tronTransferDto) {
        String url = FULL_NODE_FOR_SEND_URL.concat(CREATE_TRANSACTION);
        RequestEntity<TronTransferDtoTRC20> requestEntity = new RequestEntity<>(tronTransferDto, HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject signTransferFundsTRC20(JSONObject jsonObject) {
        String url = FULL_NODE_FOR_SEND_URL.concat(SIGN_TRANSACTION);
        RequestEntity<JSONObject> requestEntity = new RequestEntity<>(jsonObject, HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject broadcastTransferFundsTRC20(JSONObject jsonObject) {
        String url = FULL_NODE_FOR_SEND_URL.concat(BROADCAST_TRANSACTION);
        RequestEntity<JSONObject> requestEntity = new RequestEntity<>(jsonObject, HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject transferAsset(TronTransferDto tronTransferDto) {
        String url = FULL_NODE_FOR_SEND_URL.concat(EASY_TRANSFER_ASSET);
        RequestEntity<TronTransferDto> requestEntity = new RequestEntity<>(tronTransferDto, HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject getTransactions(long blockNum) {
        String url = FULL_NODE_URL.concat(GET_BLOCK_TX);
        JSONObject object = new JSONObject() {{put("num", blockNum); }};
        RequestEntity<String> requestEntity = new RequestEntity<>(object.toString(), HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }


    @SneakyThrows
    @Override
    public JSONObject getTransaction(String hash) {
        String url = String.join("", EXPLORER_API, GET_TX, hash);
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject getLastBlock() {
        String url = FULL_NODE_URL.concat(GET_LAST_BLOCK);
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    @SneakyThrows
    @Override
    public JSONObject getAccount(String hexAddress) {
        String url = String.join("", FULL_NODE_URL, GET_ACCOUNT_INFO);
        JSONObject object = new JSONObject() {{put("address", hexAddress);}};
        RequestEntity<String> requestEntity = new RequestEntity<>(object.toString(), HttpMethod.POST, new URI(url));
        return new JSONObject(performRequest(requestEntity));
    }

    public static String base58checkToHexString(String base58) {
        return toHexString(decodeFromBase58Check(base58));
    }

    private static String toHexString(byte[] data) {
        return data == null ? "" : Hex.toHexString(data);
    }

    private static byte[] decodeFromBase58Check(String addressBase58) {
        if (StringUtils.isEmpty(addressBase58)) {
            log.warn("Warning: Address is empty !!");
            return null;
        }
        byte[] address = decode58Check(addressBase58);
        if (!addressValid(address)) {
            return null;
        }
        return address;
    }

    private static byte[] decode58Check(String input) {
        byte[] decodeCheck = decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = hash(decodeData);
        byte[] hash1 = hash(hash0);
        if (hash1[0] == decodeCheck[decodeData.length] &&
                hash1[1] == decodeCheck[decodeData.length + 1] &&
                hash1[2] == decodeCheck[decodeData.length + 2] &&
                hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        }
        return null;
    }


    private static byte[] decode(String input) throws IllegalArgumentException {
        if (input.length() == 0) {
            return new byte[0];
        }
        byte[] input58 = new byte[input.length()];
        // Transform the String to a base58 byte sequence
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            int digit58 = -1;
            if (c >= 0 && c < 128) {
                digit58 = INDEXES[c];
            }
            if (digit58 < 0) {
                throw new IllegalArgumentException("Illegal character " + c + " at " + i);
            }

            input58[i] = (byte) digit58;
        }
        // Count leading zeroes
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }
        // The encoding
        byte[] temp = new byte[input.length()];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = divmod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = mod;
        }
        // Do no add extra leading zeroes, move j to first non null byte.
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }

        return copyOfRange(temp, j - zeroCount, temp.length);
    }

    private static byte divmod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * 58 + digit58;

            number58[i] = (byte) (temp / 256);

            remainder = temp % 256;
        }

        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }

    private static byte[] hash(byte[] input) {
        return hash(input, 0, input.length);
    }

    private static byte[] hash(byte[] input, int offset, int length) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(input, offset, length);
        return digest.digest();
    }

    private static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            log.warn("Warning: Address is empty !!");
            return false;
        }
        if (address.length != ADDRESS_SIZE) {
            log.warn("Warning: Address length need " + ADDRESS_SIZE + " but " + address.length + " !!");
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != getAddressPreFixByte()) {
            log.warn("Warning: Address need prefix with " + getAddressPreFixByte() + " but "+ preFixbyte + " !!");
            return false;
        }
        //Other rule;
        return true;
    }

    private static byte getAddressPreFixByte() {
        return addressPreFixByte;
    }

    private String performRequest(RequestEntity requestEntity) {
        ResponseEntity<String> responseEntity;
        try {
            log.debug("trx request {}", requestEntity.getUrl());
            responseEntity = restTemplate.exchange(requestEntity, String.class);
            log.debug("trx response to url {} - {}", requestEntity.getUrl(), responseEntity);
            return new String(responseEntity.getBody().getBytes(),"utf-8");
        } catch (Exception e) {
            log.error("trx request {} {} {}", requestEntity.getUrl(), requestEntity.getMethod(), e);
            throw new RuntimeException(e);
        }
    }
}
