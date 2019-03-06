package me.exrates.service.impl;

//import com.liqpay.LiqPay;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.LiqpayService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/liqpay.properties")
@Conditional(MonolitConditional.class)
public class LiqpayServiceImpl implements LiqpayService {

  private @Value("${liqpay.url}") String url;
  private @Value("${liqpay.public_key}") String public_key;
  private @Value("${liqpay.private_key}") String private_key;
  private @Value("${liqpay.apiVersion}") String apiVersion;
  private @Value("${liqpay.action}") String action;


  private static final Logger logger = LogManager.getLogger(AdvcashServiceImpl.class);

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private AlgorithmService algorithmService;

  @Autowired
  private WithdrawUtils withdrawUtils;

  @Transactional
  public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {
    /*Transaction transaction = transactionService.createTransactionRequest(creditsOperation);

    BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
    final String currency = transaction.getCurrency().getName();
    final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

    Map params = new HashMap();
    params.put("version", Integer.parseInt(apiVersion));
    params.put("public_key", public_key);
    params.put("action", action);
    params.put("amount", amountToPay);
    params.put("currency", creditsOperation.getCurrency().getName());
    params.put("description", "Order: " + transaction.getId());
    params.put("order_id", transaction.getId());
    byte[] hashSha1 = sha1(transaction.getId() + private_key);
    String hash = base64_encode(hashSha1);
    params.put("info", hash);


    Gson gson = new Gson();
    String jsonData = gson.toJson(params);
    String data = algorithmService.base64Encode(jsonData);
    byte[] signatureSha1 = sha1((private_key + data + private_key));
    String signature = base64_encode(signatureSha1);

    final PendingPayment payment = new PendingPayment();
    payment.setTransactionHash(hash);
    payment.setInvoiceId(transaction.getId());
    pendingPaymentDao.create(payment);


    Properties properties = new Properties();
    properties.put("data", data);
    properties.put("signature", signature);

    RedirectView redirectView = new RedirectView(url);
    redirectView.setAttributes(properties);


    return redirectView;*/

    return null;

  }


  public static String base64_encode(byte[] bytes) {
    return DatatypeConverter.printBase64Binary(bytes);
  }

  public static byte[] sha1(String param) {
    try {
      MessageDigest e = MessageDigest.getInstance("SHA-1");
      e.reset();
      e.update(param.getBytes("UTF-8"));
      return e.digest();
    } catch (Exception var2) {
      throw new RuntimeException("Can\'t calc SHA-1 hash", var2);
    }
  }


  @Override
  public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
    throw new NotImplimentedMethod("for " + withdrawMerchantOperationDto);
  }

  @Override
  public Map<String, String> refill(RefillRequestCreateDto request) {
    Integer orderId = request.getId();
    BigDecimal sum = request.getAmount();
    String currency = request.getCurrencyName();
    BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        /**/
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("version", Integer.parseInt(apiVersion));
      put("public_key", public_key);
      put("action", action);
      put("amount", amountToPay);
      put("currency", currency);
      put("description", "Order: " + orderId);
      put("order_id", orderId);
      byte[] hashSha1 = sha1(orderId + private_key);
      String hash = base64_encode(hashSha1);
      put("info", hash);
    }};
    /**/
    Gson gson = new Gson();
    String jsonData = gson.toJson(params);
    String data = algorithmService.base64Encode(jsonData);
    byte[] signatureSha1 = sha1((private_key + data + private_key));
    String signature = base64_encode(signatureSha1);
    /**/
    Properties properties = new Properties();
    properties.put("data", data);
    properties.put("signature", signature);
    /**/
    String fullUrl = generateFullUrl(url, properties);
    return new HashMap<String, String>() {{
      put("$__redirectionUrl", fullUrl);
    }};
  }

  @Override
  public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    throw new NotImplimentedMethod("for " + params);
  }

  @Override
  public boolean isValidDestinationAddress(String address) {

    return withdrawUtils.isValidDestinationAddress(address);
  }

}
