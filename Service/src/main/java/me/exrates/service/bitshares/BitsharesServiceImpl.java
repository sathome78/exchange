package me.exrates.service.bitshares;

import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.SneakyThrows;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
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
import java.util.concurrent.TimeUnit;

import static me.exrates.service.bitshares.memo.MemoDecryptor.decryptBTSmemo;


@Data
@ClientEndpoint
public abstract class BitsharesServiceImpl implements BitsharesService {

    private static final long RECONNECT_PERIOD = 60;
    protected Logger log;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    protected RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    protected MerchantSpecParamsDao merchantSpecParamsDao;
    @Autowired
    private GtagService gtagService;

    private String mainAddress;
    protected String mainAddressId;
    protected String merchantName;
    private String currencyName;
    private String wsUrl;
    private static final int MAX_TAG_DESTINATION_DIGITS = 9;
    protected int lastIrreversibleBlockValue; //
    private String privateKey;
    private int decimal;

    protected Merchant merchant;
    protected Currency currency;
    private URI WS_SERVER_URL;
    private volatile Session session;
    protected volatile RemoteEndpoint.Basic endpoint;
    protected final String lastIrreversebleBlockParam = "last_irreversible_block_num";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public BitsharesServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY, int decimal) {
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        this.decimal = decimal;
        log = Logger.getLogger(merchantName.toLowerCase());
        Properties props = new Properties();
        try {
            props.load(this.getClass().getClassLoader().getResourceAsStream(propertySource));
            mainAddress = props.getProperty("mainAddress");
            mainAddressId = props.getProperty("mainAddressId");
            wsUrl = props.getProperty("wsUrl");
            scheduler.scheduleAtFixedRate(this::reconnectAndSubscribe, SCANING_INITIAL_DELAY, RECONNECT_PERIOD, TimeUnit.MINUTES);
        } catch (IOException e){
            log.error(e);
        }
    }

    @PostConstruct
    public void setUp() {
        try {
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
        }catch (Exception ex){
            log.error(ex);
        }
    }

    private void reconnectAndSubscribe() {
        try {
            close();

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

    private void close() throws IOException {
        endpoint = null;
        if(session != null){
            session.close();
        }
        WS_SERVER_URL = null;
    }

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
            throw new MerchantInternalException("error generating new destination tag for aunit" + userId);
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

        Integer requestId;
        try {
            requestId = refillService.getRequestId(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            requestId = setIdAndAccept(requestAcceptDto);
        }
        final String username = refillService.getUsernameByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
    }

    private Integer setIdAndAccept(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {
        try {
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
            return requestId;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    public RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount) {
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.error("aunit transaction allready received!!! {}" + hash);
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

    protected void subscribeToTransactions() throws IOException {
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
        subscribe.put("params", new JSONArray().put(2).put("set_subscribe_callback").put(new JSONArray().put(7).put(false)));

        endpoint.sendText(login.toString());

        endpoint.sendText(db.toString());

        endpoint.sendText(netw.toString());

        endpoint.sendText(history.toString());

        endpoint.sendText(chainId.toString());

        endpoint.sendText(subscribe.toString());

        endpoint.sendText(get_object.toString());

    }

    @OnMessage
    public void onMessage(String msg) {
        try {
            if (isContainsLastIrreversibleBlockInfo(msg)) getUnprocessedBlocks(msg);
            else if (isIrreversibleBlockInfo(msg)) processIrreversebleBlock(msg);
            else log.info("unrecogrinzed msg from " + merchantName + "\n" + msg);
        } catch (Exception e) {
//            log.error("Web socket error" + merchantName + "  : \n" + e.getMessage());
        }

    }

    @SneakyThrows
    protected void getBlock(int blockNum) throws IOException {
        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("method", "call");
        block.put("params", new JSONArray().put(2).put("get_block").put(new JSONArray().put(blockNum)));
        endpoint.sendText(block.toString());
    }

    protected void processIrreversebleBlock(String trx) {
        JSONObject block = new JSONObject(trx);

        JSONArray transactions = extractTransactionsFromBlock(block);
        if (transactions.length() == 0) return;

        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());
        try {
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = extractTransaction(transactions, i);

                if (transaction.getString("to").equals(mainAddressId)) makeRefill(lisfOfMemo, transaction, StringUtils.EMPTY);

            }

        } catch (JSONException e) {
            log.debug(e);
        }
    }


    @SneakyThrows
    protected void makeRefill(List<String> lisfOfMemo, JSONObject transaction, String hash) {
        JSONObject memo = transaction.getJSONObject("memo");
        try {
            String memoText = decryptBTSmemo(privateKey, memo.toString(), merchantName);
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

    protected void prepareAndProcessTx(String hash, String address, BigDecimal amount) {
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

    protected BigDecimal reduceAmount(BigDecimal amount) {
        return amount.multiply(new BigDecimal(Math.pow(10, (-1)*decimal))).setScale(decimal, RoundingMode.HALF_DOWN);
    }

    protected void getUnprocessedBlocks(String msg) throws IOException {
        int currentLastIrreversibleBlock = getLastIrreversableBlock(msg);
        synchronized (this) {
            if (currentLastIrreversibleBlock > lastIrreversibleBlockValue) {
                for (; lastIrreversibleBlockValue <= currentLastIrreversibleBlock; lastIrreversibleBlockValue++) {
                    getBlock(lastIrreversibleBlockValue);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlockParam, String.valueOf(lastIrreversibleBlockValue));
            }
        }
    }


    protected boolean isContainsLastIrreversibleBlockInfo(String jsonRpc){
        return jsonRpc.contains("notice");
    }

    private boolean isIrreversibleBlockInfo(String msg) {
        return msg.contains("previous");
    }

    protected int getLastIrreversableBlock(String jsonRpc) {
        JSONObject message = new JSONObject(jsonRpc);
        return message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(0).getInt(lastIrreversebleBlockParam);
    }

    protected JSONObject extractTransaction(JSONArray transactions, int i) {
        return transactions.getJSONObject(i).getJSONArray("operations").getJSONArray(0).getJSONObject(1);
    }


    protected JSONArray extractTransactionsFromBlock(JSONObject block) {
        return block.getJSONObject("result").getJSONArray("transactions");
    }

    //Example for decrypting memo don't delete
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = decryptBTSmemo("5KJbFnkWbfqZFVdTVo1BfBRj7vFFaGv2irkDfCfpDyHJiSgNK3k", "{\"from\":\"PPY6xkszYqrmwwBeCrwg8FmJM3NLN2DLuDFz8jwb7wZZfUcku5aPP\",\"to\":\"PPY8VikXsDhYu42VQkMECGGrj7pZUxk34GWPH3MVLTgdzjvXgnEtQ\",\"nonce\":\"396729669771043\",\"message\":\"895066dc7b1e53df553b801d7e86a45d\"}", "PPY");

        System.out.println(s);
    }
}
