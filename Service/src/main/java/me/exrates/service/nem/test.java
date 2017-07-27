package me.exrates.service.nem;

import com.google.common.primitives.Bytes;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.NemTransactionException;
import me.exrates.service.exception.NisNotReadyException;
import me.exrates.service.exception.NisTransactionException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.handler.RestResponseErrorHandler;
import me.exrates.service.util.RestUtil;
import org.apache.axis.utils.ByteArray;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.PrivateKey;
import org.nem.core.crypto.PublicKey;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.*;
import org.nem.core.model.ncc.RequestPrepareAnnounce;
import org.nem.core.model.primitive.Amount;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.*;
import org.nem.core.time.TimeInstant;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Maks on 22.07.2017.
 */
public class test {

    static RestTemplate restTemplate = new RestTemplate();
    static SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    static {
        requestFactory.setOutputStreaming(false);
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.setRequestFactory(requestFactory);
    }
    static String nisServer = "http://23.228.67.85:7890";
    private final static String pathExtendedInfo = "/node/extended-info";
    private final static String pathPrepareAnounce = "/transaction/prepare-announce";
    private final static String pathGetTransaction = "/transaction/get?hash=";
    private final static String pathGetCurrentBlockHeight = "/chain/last-block";
    private final static String pathGetIncomeTransactions = "/account/transfers/incoming?address=%s";
    private static final int decimals = 6;
    int version_main = 1744830465;
    int version_test = -1744830463;


    public static void main(String[] args) {
        KeyPair keyPair = new KeyPair(PublicKey
                .fromHexString("fdb3bbba4d70fb483592c69a9dff6a52bc81499e2a7f6ff094344172a4c818ac"));
        Account account1 = new Account(keyPair);
            BigDecimal decimal = new BigDecimal("5.343");
            TransferTransaction transaction = prepareTransaction(WithdrawMerchantOperationDto.builder()
                    .accountTo("TBXU7F3WQJQQRPWJ53UJUYI6IJL2A34YESG7FVA5")
                    .amount(decimal.toPlainString())
                    .destinationTag("hello11")
                    .build(), account1);


        System.out.println(transaction.getVersion());

        JsonSerializer serializer = new JsonSerializer();
        RequestPrepareAnnounce announce = new RequestPrepareAnnounce(transaction,
                PrivateKey.fromHexString("765b9ef2829ee9c5810b3e59148a15779b059175dd920ab91f859b855afb0eee"));
        announce.serialize(serializer);
        System.out.println(serializer.getObject());
        System.out.println(anounceTransaction(serializer.getObject().toJSONString()));



    }

    static JSONObject anounceTransaction(String serializedTransaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(serializedTransaction ,headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(nisServer.concat(pathPrepareAnounce), entity, String.class);
        JSONObject result = new JSONObject(response.getBody());
        if (RestUtil.isError(response.getStatusCode())) {
            System.out.println(response);
            String error = result.getString("message");
            try {
                defineAndThrowException(error);
            } catch (RuntimeException e) {
                throw e;
            }
        }

        return result;
    }

    protected static JSONArray getIncomeTransactions(String address, String hash) {
        String url = nisServer.concat(String.format(pathGetIncomeTransactions, address));
        if (!StringUtils.isEmpty(hash)) {
            url = url.concat("&hash=").concat(hash);
        }
        System.out.println(url);
        ResponseEntity<String> response = restTemplate
                .getForEntity(url, String.class);
        if (RestUtil.isError(response.getStatusCode()) || response.getBody().contains("error")) {
            throw new NemTransactionException(response.toString());
        }
        return new JSONObject(response.getBody()).getJSONArray("data");
    }

    private static TransferTransaction prepareTransaction(WithdrawMerchantOperationDto withdrawMerchantOperationDto,
                                                          Account account) {
        TransactionFeeCalculatorAfterFork calculatorAfterFork = new TransactionFeeCalculatorAfterFork();
        Account reipient = new Account(Address.fromEncoded(withdrawMerchantOperationDto.getAccountTo()));
        TimeInstant currentTimeStamp = getCurrentTimeStamp();
        TransferTransactionAttachment attachment = null;
        try {
            attachment = new TransferTransactionAttachment(new PlainMessage(withdrawMerchantOperationDto.getDestinationTag().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
        }
        TransferTransaction transaction = new  TransferTransaction(currentTimeStamp,
                account, reipient, transformToNemAmount(withdrawMerchantOperationDto.getAmount()),  attachment);
        transaction.setDeadline(currentTimeStamp.addHours(2));
        transaction.setFee(calculatorAfterFork.calculateMinimumFee(transaction));
        return transaction;
    }

    private static Amount transformToNemAmount(String amount) {
        BigDecimal a = new BigDecimal(amount).setScale(decimals, RoundingMode.HALF_DOWN).multiply(new BigDecimal(1000000));
        return new Amount(a.longValue());
    }

    /*static BigDecimal countTxFee(BigDecimal amount, String destinationTag, Account account) {
        Transaction transaction = prepareTransaction(WithdrawMerchantOperationDto.builder()
                .accountTo("")
                .amount(amount.toPlainString())
                .destinationTag(destinationTag)
                .build(), account);

        Amount feeAmount = NemGlobals.getTransactionFeeCalculator().calculateMinimumFee(transaction);
        System.out.println("is fee valid? " + NemGlobals.getTransactionFeeCalculator().isFeeValid(transaction, new BlockHeight(getLastBlockHeight())));
        return new BigDecimal(transformToString(feeAmount.getNumMicroNem()));
    }*/

    protected static String transformToString(long nemAmount) {
        BigDecimal a = new BigDecimal(nemAmount).setScale(decimals, RoundingMode.HALF_DOWN).divide(new BigDecimal(1000000));
        return a.toPlainString();
    }

    protected static long getLastBlockHeight() {
        String response = restTemplate.getForObject(nisServer.concat(pathGetCurrentBlockHeight), String.class);
        return new org.json.JSONObject(response).getLong("height");
    }


    protected static TimeInstant getCurrentTimeStamp() {
        try {
            int time = getNodeExtendedInfo().getJSONObject("nisInfo").getInt("currentTime");
            return new TimeInstant(time);
        } catch (Exception e) {
            throw new NisNotReadyException();
        }
    }

    private static JSONObject getNodeExtendedInfo() {
        String response = restTemplate.getForObject(nisServer.concat(pathExtendedInfo), String.class);
        return new org.json.JSONObject(response);
    }

    static private void defineAndThrowException(String errorMessage) {
        switch (errorMessage) {
            case "address must be valid" : {
                throw new InvalidAccountException(errorMessage);
            }
            case "FAILURE_INSUFFICIENT_BALANCE" : {
                throw new InsufficientCostsInWalletException("NEM BALANCE LOW");
            }
            default: throw new NisTransactionException(errorMessage);
        }
    }

    static protected JSONObject getSingleTransactionByHash(String hash) {
        ResponseEntity<String> response = restTemplate
                .getForEntity(nisServer.concat(pathGetTransaction).concat(hash), String.class);
        if (RestUtil.isError(response.getStatusCode()) || response.getBody().contains("error")) {
            throw new NemTransactionException(response.toString());
        }
        return new JSONObject(response.getBody());
    }
}
