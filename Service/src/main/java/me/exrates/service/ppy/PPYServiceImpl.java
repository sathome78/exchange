package me.exrates.service.ppy;

import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.SneakyThrows;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.bitshares.BitsharesService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static me.exrates.service.bitshares.MemoDecryptor.decryptBTSmemo;


//@PropertySource("classpath:/merchants/aunit.properties")
//@Log4j2(topic = "aunit")
@Data
@ClientEndpoint
public class PPYServiceImpl implements BitsharesService {

    public static final long PERIOD = 3L;
    private long SCANING_INITIAL_DELAY;
    private Logger log;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantSpecParamsDao merchantSpecParamsDao;

    private String mainAddress;
    private String mainAddressId;
    private String merchantName;
    private String currencyName;
    private String wsUrl;
    private static final int MAX_TAG_DESTINATION_DIGITS = 9;
    private int lastIrreversibleBlockValue; //
    private String privateKey;

    private Merchant merchant;
    private Currency currency;
    private URI WS_SERVER_URL;
    private volatile Session session;
    private volatile RemoteEndpoint.Basic endpoint;
    private final String lastIrreversebleBlockParam = "last_irreversible_block_num";
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PPYServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY) {
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        log = Logger.getLogger(merchantName);
        Properties props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(propertySource));
            mainAddress = props.getProperty("mainAddress");
            mainAddressId = props.getProperty("mainAddressId");
            wsUrl = props.getProperty("wsUrl");
            this.SCANING_INITIAL_DELAY = SCANING_INITIAL_DELAY;
//            scheduler.scheduleAtFixedRate(this::reconnect, SCANING_INITIAL_DELAY, PERIOD, TimeUnit.MINUTES);
            connectAndSubscribe();
        } catch (IOException e){
            log.error(e);
        }
    }

    @PostConstruct
    public void setUp() {
        privateKey = merchantService.getPassMerchantProperties(merchantName).getProperty("privateKey");
        currency = currencyService.findByName(currencyName);
        merchant = merchantService.findByName(merchantName);
        MerchantSpecParamDto merchantSpecParam = merchantSpecParamsDao.getByMerchantIdAndParamName(merchant.getId(), lastIrreversebleBlockParam);
        if(merchantSpecParam == null){
            log.error("Can not find merchant spec param with merchantId = " + merchant.getId() + " and param name = " + lastIrreversebleBlockParam + ", using default value = 0");
            lastIrreversibleBlockValue = 0;
        } else {
            lastIrreversibleBlockValue = Integer.valueOf(merchantSpecParam.getParamValue());
        }
    }

//    private void reconnect() {
//        System.out.println("RECONNECT");
//        if (!session.isOpen()) {
//            try {
//                connectAndSubscribe();
//            } catch (Exception e) {
//                log.error(e);
//            }
//        }
//    }

    @Override
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.xrp",
                new String[]{mainAddress, destinationTag.toString()}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", String.valueOf(destinationTag));
            put("message", message);
            put("qr", mainAddress);
        }};
    }

    private Integer generateUniqDestinationTag(int userId) {
        Optional<Integer> id;
        int destinationTag;
        do {
            destinationTag = generateDestinationTag(userId);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String.valueOf(destinationTag), //wtf
                    currency.getId(), merchant.getId());
        } while (id.isPresent());
        return destinationTag;
    }

    private Integer generateDestinationTag(int userId) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = MAX_TAG_DESTINATION_DIGITS - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("Error generating new destination tag for " + merchantName + " for user with id " + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return Integer.valueOf(idInString.concat(randomIntInstring.substring(0, randomNumberLength)));
    }


    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            setIdAndAccept(requestAcceptDto);
        }
    }

    private void setIdAndAccept(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {
        try {
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    public RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount) {
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.error(merchantName + " transaction allready received!!! " + hash);
            throw new RuntimeException("aunit transaction allready received!!!");
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {
        try {
            refillService.putOnBchExamRefillRequest(
                    RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestAcceptDto.getRequestId())
                            .merchantId(merchant.getId())
                            .currencyId(currency.getId())
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public String getMainAddress() {
        return mainAddress;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    @Override
    public String getMerchantName() {
        return merchantName;
    }

    private void connectAndSubscribe() {
        try {
            WS_SERVER_URL = URI.create(wsUrl);
            session = ContainerProvider.getWebSocketContainer()
                    .connectToServer(this, WS_SERVER_URL);
            session.setMaxBinaryMessageBufferSize(5012000);
            session.setMaxTextMessageBufferSize(5012000);
            session.setMaxIdleTimeout(Long.MAX_VALUE);

            endpoint = session.getBasicRemote();
            subscribeToTransactions();
        } catch (Exception e) {
            log.error(merchantName + " node error " + e.getMessage());
        }
    }

    private void subscribeToTransactions() throws IOException {
        JSONObject login = new JSONObject();
        login.put("id", 0);
        login.put("method", "call");
        login.put("params", new JSONArray().put(1).put("login").put(new JSONArray().put("").put("")));

        JSONObject db = new JSONObject();
        db.put("id", 1);
        db.put("method", "call");
        db.put("params", new JSONArray().put(1).put("database").put(new JSONArray()));

        JSONObject netw = new JSONObject();
        netw.put("id", 2);
        netw.put("method", "call");
        netw.put("params", new JSONArray().put(1).put("network_broadcast").put(new JSONArray()));

        JSONObject history = new JSONObject();
        history.put("id", 3);
        history.put("method", "call");
        history.put("params", new JSONArray().put(1).put("history").put(new JSONArray()));

        JSONObject orders = new JSONObject();
        orders.put("id", 4);
        orders.put("method", "call");
        orders.put("params", new JSONArray().put(1).put("orders").put(new JSONArray()));

        JSONObject chainId = new JSONObject();
        chainId.put("id", 5);
        chainId.put("method", "call");
        chainId.put("params", new JSONArray().put(2).put("get_chain_id").put(new JSONArray().put(new JSONArray())));

        JSONObject get_object = new JSONObject();
        get_object.put("id", 6);
        get_object.put("method", "call");
        get_object.put("params", new JSONArray().put(2).put("get_objects").put(new JSONArray().put(new JSONArray().put("2.1.0"))));


        JSONObject subscribe = new JSONObject();
        subscribe.put("id", 7);
        subscribe.put("method", "call");
        subscribe.put("params", new JSONArray().put(2).put("set_subscribe_callback").put(new JSONArray().put(0).put(false)));

        endpoint.sendText(login.toString());

        endpoint.sendText(db.toString());

        endpoint.sendText(netw.toString());

        endpoint.sendText(history.toString());

        endpoint.sendText(orders.toString());

        endpoint.sendText(chainId.toString());

        endpoint.sendText(subscribe.toString());

        endpoint.sendText(get_object.toString());

    }

    @OnMessage
    public void onMessage(String msg) {
        System.out.println(msg);
        try {
            if (msg.contains("last_irreversible_block_num")) setIrreversableBlock(msg);
            else if (msg.contains("previous")) processIrreversebleBlock(msg);
            else log.info("unrecogrinzed msg from aunit \n" + msg);
        } catch (Exception e) {
            log.error("Web socket error" + merchantName + "  : \n" + e.getMessage());
        }

    }

    @SneakyThrows
    private void getBlock(int blockNum) {
        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("method", "call");
        block.put("params", new JSONArray().put(2).put("get_block").put(new JSONArray().put(blockNum)));
        endpoint.sendText(block.toString());
    }

    private void processIrreversebleBlock(String trx) {
        JSONObject block = new JSONObject(trx);
        if (block.getJSONObject("result").getJSONArray("transactions").length() == 0) return;
        JSONArray transactions = block.getJSONObject("result").getJSONArray("transactions");

        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());
        try {
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = transactions.getJSONObject(i).getJSONArray("operations").getJSONArray(0).getJSONObject(1);

                if (transaction.getString("to").equals(mainAddressId)) makeRefill(lisfOfMemo, transaction);

            }

        } catch (JSONException e) {
            log.debug(e);
        }

    }


    @SneakyThrows
    private void makeRefill(List<String> lisfOfMemo, JSONObject transaction) {
        JSONObject memo = transaction.getJSONObject("memo");
        try {
            String memoText = decryptBTSmemo(privateKey, memo.toString());
            if (lisfOfMemo.contains(memoText)) {
                BigDecimal amount = reduceAmount(transaction.getJSONObject("amount").getBigDecimal("amount"));

                prepareAndProcessTx(Hashing.sha256()
                        .hashString(memo.toString(), StandardCharsets.UTF_8)
                        .toString(), memoText, amount);

            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Memo can not be decrypted : " + e.getClass());
        }
    }


    private void prepareAndProcessTx(String hash, String address, BigDecimal amount) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("hash", hash);
        map.put("amount", amount.toString());
        createRequest(hash, address, amount);
        try {
            processPayment(map);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    private BigDecimal reduceAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(Math.pow(10, -5))).setScale(5, RoundingMode.HALF_DOWN);
    }

    private void setIrreversableBlock(String msg) {
        JSONObject message = new JSONObject(msg);
        int blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(3).getInt(lastIrreversebleBlockParam);
        synchronized (this) {
            if (blockNumber > lastIrreversibleBlockValue) {
                for (; lastIrreversibleBlockValue <= blockNumber; lastIrreversibleBlockValue++) {
                    getBlock(lastIrreversibleBlockValue);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlockParam, String.valueOf(lastIrreversibleBlockValue));
            }
        }
    }


    //Example for decrypting memo
    public static void main(String[] args) throws NoSuchAlgorithmException {
//        String s = decryptBTSmemo("5Js88n7mstj3oetaWvmr2s6aYdd8Tfp6P55sCAidkDdaxFhzAAv", "{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT83A7sYcCZvVMphurvQPbGtw6BFHFxPFDZfKCJDqzcAeSfPrSgR\",\"nonce\":\"394474453593373\",\"message\":\"a3a22532efe98f3ab7d31d50761079d6\"}");
//
//        System.out.println(s);

        JSONObject jsonObject = new JSONObject("{\"id\":10,\"result\":{\"previous\":\"0042e245acd0f68f3bb8587959ba75aec84a7e9d\",\"timestamp\":\"2017-11-07T00:30:36\",\"witness\":\"1.6.13\",\"next_secret_hash\":\"ef2be95cc30aae30ee8352b58b7e1afe3f01b756\",\"previous_secret\":\"5a049fc34ab937861a4a14deae4c107818a5c016\",\"transaction_merkle_root\":\"0000000000000000000000000000000000000000\",\"extensions\":[],\"witness_signature\":\"206d5094985c8db1f9c9e32b8520a86609421fb005d3fc3b9cb588939ed1faaa9e195a9d19c5bea435bb1910ba60e97469e96d2be1c62c50c0fa04e1a931312be2\",\"transactions\":[]}}");

        JSONObject block = jsonObject;
//        if (block.getJSONObject("result").getJSONArray("transactions").length() == 0) return;
        JSONArray transactions = block.getJSONObject("result").getJSONArray("transactions");
        System.out.println(jsonObject);
    }
}
