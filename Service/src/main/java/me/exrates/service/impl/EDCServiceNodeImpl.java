package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.*;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.EDCAccountDao;
import me.exrates.model.EDCAccount;
import me.exrates.model.Transaction;
import me.exrates.service.EDCServiceNode;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.util.BiTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2(topic = "edc_log")
@Service
@PropertySource({"classpath:/merchants/edc_cli_wallet.properties", "classpath:/merchants/edcmerchant.properties"})
public class EDCServiceNodeImpl implements EDCServiceNode {

  private @Value("${edcmerchant.token}") String token;
  private @Value("${edcmerchant.main_account}") String main_account;
  private @Value("${edcmerchant.hook}") String hook;
  private @Value("${edcmerchant.new_account}") String urlCreateNewAccount;

  private @Value("${edc.blockchain.host_delayed}") String RPC_URL_DELAYED;
  private @Value("${edc.blockchain.host_fast}") String RPC_URL_FAST;
  private @Value("${edc.account.registrar}") String REGISTRAR_ACCOUNT;
  private @Value("${edc.account.referrer}") String REFERRER_ACCOUNT;
  private @Value("${edc.account.main}") String MAIN_ACCOUNT;
  private @Value("${edc.account.main.private.key}") String MAIN_ACCOUNT_PRIVATE_KEY;
  private final String PENDING_PAYMENT_HASH = "1fc3403096856798ab8992f73f241334a4fe98ce";

  private final BigDecimal BTS = new BigDecimal(1000L);
  private final int DEC_PLACES = 2;

  private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

  private final BlockingQueue<String> rawTransactions = new LinkedBlockingQueue<>();
  private final BlockingQueue<BiTuple<String, String>> incomingPayments = new LinkedBlockingQueue<>();
  private final ExecutorService workers = Executors.newFixedThreadPool(2);
  private volatile boolean isRunning = true;

  private final String ACCOUNT_PREFIX = "ex1f";
  private final String REGISTER_NEW_ACCOUNT_RPC = "{\"method\":\"register_account\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", 0, \"true\"], \"id\":%s}";
  private final String NEW_KEY_PAIR_RPC = "{\"method\": \"suggest_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [], \"id\": %d}";
  private final String IMPORT_KEY = "{\"method\": \"import_key\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\",\"%s\"], \"id\": %s}";
  private final String TRANSFER_EDC = "{\"method\":\"transfer\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"true\"], \"id\":%s}";
  private final Pattern pattern = Pattern.compile("\"brain_priv_key\":\"([\\w\\s]+)+\",\"wif_priv_key\":\"(\\S+)\",\"pub_key\":\"(\\S+)\"");

  private volatile boolean debugLog = true;

  @Autowired
  TransactionService transactionService;

  @Autowired
  EDCAccountDao edcAccountDao;

  public void changeDebugLogStatus(final boolean status) {
    debugLog = true;
  }

  private void handleRawTransactions(final String tx) {

    try {

      final String transactions = tx.substring(tx.indexOf("transactions"), tx.length());
      final String[] operationses = transactions.split("operations");
      for (String str : operationses) {
        final int extensions = str.indexOf("extensions");
        if (extensions > 0) {
          str = str.substring(0, extensions);
          if (str.contains("\":[[0,{\"fee\"")) {
            str = str.substring(str.indexOf("to"));
            final String accountId = str.substring(str.indexOf("to") + 5, str.indexOf("amount") - 3);
            final String amount = str.substring(str.lastIndexOf("amount") + 8, str.indexOf("asset_id") - 2);
            incomingPayments.put(new BiTuple<>(accountId, amount));
          }
        }
      }
    } catch (InterruptedException e) {
      log.info("Method acceptTransaction InterruptedException........................................... error: ");
      log.error(e);
    } catch (Exception e) {
      log.info("Method acceptTransaction Exception........................................... error: ");
      log.error(e);
    }
  }

  @Transactional
  @Override
  public void rescanUnusedAccounts() {
    List<EDCAccount> list = edcAccountDao.getUnusedAccounts();
    for (EDCAccount account : list) {
      final String responseImportKey;
      try {
        responseImportKey = makeRpcCallFast(IMPORT_KEY, account.getAccountId(), account.getWifPrivKey(), account.getTransactionId());
        if (responseImportKey.contains("true")) {
          String accountBalance = extractBalance(account.getAccountId(), account.getTransactionId());
          if (Double.valueOf(accountBalance) > 0) {
            final String responseTransfer = makeRpcCallFast(TRANSFER_EDC, account.getAccountId(), MAIN_ACCOUNT, accountBalance, "EDC", "Inner transfer", String.valueOf(account.getTransactionId()));
            if (responseTransfer.contains("error")) {
              throw new InterruptedException("Could not transfer money to main account!\n" + responseTransfer);
            }
            edcAccountDao.setAccountUsed(account.getTransactionId());
            /*
              TODO REFILL
              Не вникал для чего этот метод rescanUnusedAccounts, но удалять в любом случае нельзя
            Optional<PendingPayment> payment = paymentDao.findByInvoiceId(account.getTransactionId());
            if (payment.isPresent()) {

              paymentDao.delete(account.getTransactionId());
            }
            */
            /*
            TODO REFILL
            этот код удалить. Но сам не стал, чтобы был перед глазами при изменении метода rescanUnusedAccounts в целом

            Transaction transaction = transactionService.findById(account.getTransactionId());
            if (!transaction.isProvided()) {

              final BigDecimal targetAmount = transaction.getAmount().add(transaction.getCommissionAmount()).setScale(DEC_PLACES, ROUND_HALF_UP);
              final BigDecimal currentAmount = new BigDecimal(accountBalance).add(new BigDecimal("0.001")).setScale(DEC_PLACES, ROUND_HALF_UP);
              if (targetAmount.compareTo(currentAmount) != 0) {
                transactionService.updateTransactionAmount(transaction, currentAmount);
              }
              transactionService.provideTransaction(transaction);
            }*/
          } else {
            edcAccountDao.setAccountUsed(account.getTransactionId());
          }
        }
      } catch (IOException e) {
        log.error(e);
      } catch (InterruptedException e) {
        log.error(e);
      }
    }
  }

  @PostConstruct
  public void init() {
    // cache warm
    /*

    TODO REFILL
    try {

      paymentDao.findAllByHash(PENDING_PAYMENT_HASH)
          .forEach(payment -> pendingPayments.put(payment.getAddress(), payment));
      workers.submit(() -> {  // processing json with transactions from server
        while (isRunning) {
          final String poll = rawTransactions.poll();
          if (poll != null) {
            handleRawTransactions(poll);
          }
        }
      });
      workers.submit(() -> {
        while (isRunning) { // accepting transactions
          final BiTuple<String, String> poll = incomingPayments.poll();
          if (poll != null) {
            acceptTransaction(poll);
          }
        }
      });
    } catch (Exception e) {
      LOG.info("Method init Exception........................................... error: ");
      LOG.error(e);
    }*/
  }

  @PreDestroy
  public void destroy() {
    isRunning = false;
    workers.shutdown();
  }

  @Override
  public void submitTransactionsForProcessing(String list) {
    try {
      rawTransactions.put(list);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String extractAccountId(final String account, final int invoiceId) throws IOException {
    final String GET_ACCOUNT_ID_RPC = "{\"method\": \"get_account_id\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %d}";
    final String response = makeRpcCallDelayed(GET_ACCOUNT_ID_RPC, account, invoiceId);
    final ObjectMapper mapper = new ObjectMapper();
    final Map<String, String> result = mapper.readValue(response, new TypeReference<Map<String, String>>() {
    });
    return result.get("result");
  }

  private String createAccount(final int id) throws Exception {
    log.info("Start method createAccount");
    final String accountName = (ACCOUNT_PREFIX + id + UUID.randomUUID()).toLowerCase();
    final EnumMap<KEY_TYPE, String> keys = extractKeys(makeRpcCallFast(NEW_KEY_PAIR_RPC, id)); // retrieve public and private from server
    final String response = makeRpcCallFast(REGISTER_NEW_ACCOUNT_RPC, accountName, keys.get(KEY_TYPE.PUBLIC), keys.get(KEY_TYPE.PUBLIC), REGISTRAR_ACCOUNT, REFERRER_ACCOUNT, String.valueOf(id));
    log.info("bit_response: " + response.toString());
    if (response.contains("error")) {
      throw new Exception("Could not create new account!\n" + response);
    }
    final EDCAccount edcAccount = new EDCAccount();
    edcAccount.setTransactionId(id);
    edcAccount.setBrainPrivKey(keys.get(KEY_TYPE.BRAIN));
    edcAccount.setPubKey(keys.get(KEY_TYPE.PUBLIC));
    edcAccount.setWifPrivKey(keys.get(KEY_TYPE.PRIVATE));
    edcAccount.setAccountName(accountName);
    edcAccountDao.create(edcAccount);
    return accountName;
  }

  private EnumMap<KEY_TYPE, String> extractKeys(final String json) {
    final Matcher matcher = pattern.matcher(json);
    if (matcher.find()) {
      final EnumMap<KEY_TYPE, String> result = new EnumMap<>(KEY_TYPE.class);
      result.put(KEY_TYPE.BRAIN, matcher.group(1));
      result.put(KEY_TYPE.PRIVATE, matcher.group(2));
      result.put(KEY_TYPE.PUBLIC, matcher.group(3));
      return result;
    }
    throw new RuntimeException("Invalid response from server:\n" + json);
  }

  public String extractBalance(final String accountId, final int invoiceId) throws IOException {
    final String LIST_ACCOUNT_BALANCE = "{\"method\": \"list_account_balances\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %s}";
    final String response = makeRpcCallFast(LIST_ACCOUNT_BALANCE, accountId, invoiceId);
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(response).getAsJsonObject();
    JsonArray result = object.getAsJsonArray("result");
    if (result.size() != 1) {
      return new BigDecimal("0.000").toString();
    }
    BigDecimal amount = BigDecimal.valueOf(result.get(0).getAsJsonObject().get("amount").getAsLong()).divide(BTS);

    return amount.subtract(new BigDecimal("0.001")).toString();
  }

  @Override
  public void transferToMainAccount(final String accountId, final Transaction tx) throws IOException, InterruptedException {

    final EDCAccount edcAccount = edcAccountDao.findByTransactionId(tx.getId());
    final String responseImportKey = makeRpcCallFast(IMPORT_KEY, accountId, edcAccount.getWifPrivKey(), tx.getId());
    if (responseImportKey.contains("true")) {
      String accountBalance = extractBalance(accountId, tx.getId());
      final String responseTransfer = makeRpcCallFast(TRANSFER_EDC, accountId, MAIN_ACCOUNT, accountBalance, "EDC", "Inner transfer", String.valueOf(tx.getId()));
      if (responseTransfer.contains("error")) {
        throw new InterruptedException("Could not transfer money to main account!\n" + responseTransfer);
      }
    }
  }

  @Override
  public void transferFromMainAccount(final String accountName, final String amount) throws IOException, InterruptedException {
    final String responseImportKey = makeRpcCallFast(IMPORT_KEY, MAIN_ACCOUNT, MAIN_ACCOUNT_PRIVATE_KEY, 1);
    if (responseImportKey.contains("true")) {
      final String responseTransfer = makeRpcCallFast(TRANSFER_EDC,MAIN_ACCOUNT, accountName, amount, "EDC", "Output transfer", 1);
      if (responseTransfer.contains("error")) {
        log.error(responseTransfer);
        if (responseTransfer.contains("rec && rec->name == account_name_or_id")){
          throw new InvalidAccountException();
        }
        if (responseTransfer.contains("Insufficient Balance")){
          throw new InsufficientCostsInWalletException();
        }
        throw new MerchantException(responseTransfer);
      }
    }
  }

  private String makeRpcCallDelayed(String rpc, Object... args) throws IOException {
   /* final String rpcCall = String.format(rpc, args);
    final Request request = new Request.Builder()
        .url(RPC_URL_DELAYED)
        .post(RequestBody.create(MEDIA_TYPE, rpcCall))
        .build();
    return HTTP_CLIENT.newCall(request)
        .execute()
        .body()
        .string();*/
   return "";
  }

  private String makeRpcCallFast(String rpc, Object... args) throws IOException {
    final String rpcCall = String.format(rpc, args);
    final Request request = new Request.Builder()
        .url(RPC_URL_FAST)
        .post(RequestBody.create(MEDIA_TYPE, rpcCall))
        .build();
    return HTTP_CLIENT.newCall(request)
        .execute()
        .body()
        .string();
  }

  private enum KEY_TYPE {
    BRAIN("brain_priv_key"),
    PUBLIC("pub_key"),
    PRIVATE("wif_priv_key");

    public final String type;

    KEY_TYPE(final String type) {
      this.type = type;
    }
  }

  private String getAddress() {
    final OkHttpClient client = new OkHttpClient();
    client.setReadTimeout(60, TimeUnit.SECONDS);

    final FormEncodingBuilder formBuilder = new FormEncodingBuilder();
    formBuilder.add("account", main_account);
    formBuilder.add("hook", hook);

    final Request request = new Request.Builder()
        .url(urlCreateNewAccount + token)
        .post(formBuilder.build())
        .build();
    final String returnResponse;

    try {
      returnResponse = client
          .newCall(request)
          .execute()
          .body()
          .string();
    } catch (IOException e) {
      throw new MerchantInternalException(e);
    }

    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(returnResponse).getAsJsonObject();

    return object.get("address").getAsString();

  }



}
