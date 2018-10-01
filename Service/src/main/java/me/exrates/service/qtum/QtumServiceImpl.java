package me.exrates.service.qtum;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.qtum.QtumTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.vo.ProfileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2(topic = "qtum_log")
@Service("qtumServiceImpl")
@PropertySource("classpath:/merchants/qtum.properties")
public class QtumServiceImpl implements QtumService {

    @Autowired
    private QtumNodeService qtumNodeService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private RefillService refillService;

    private @Value("${qtum.min.confirmations}") Integer minConfirmations;

    private @Value("${qtum.min.transfer.amount}") BigDecimal minTransferAmount;

    private @Value("${qtum.main.address.for.transfer}") String mainAddressForTransfer;

    private @Value("${qtum.node.endpoint}") String endpoint;

    private Merchant merchant;

    private Currency currency;

    private final String qtumSpecParamName = "LastRecievedBlock";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        merchant = merchantService.findByName("Qtum");
        currency = currencyService.findByName("QTUM");


        scheduler.scheduleAtFixedRate(() -> {
            try {
                scanBlocks();
            } catch (Exception e) {
                log.error(e);
            }

        }, 5L, 25L, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkBalanceAndTransfer();
            } catch (Exception e) {
                log.error(e);
            }

        }, 90L, 120L, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                backupWallet();
            } catch (Exception e) {
                log.error(e);
            }

        }, 1L, 12L, TimeUnit.HOURS);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = qtumNodeService.getNewAddress();

        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(params.get("address"))
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(new BigDecimal(params.get("amount")))
                .merchantTransactionId(params.get("hash"))
                .build();

        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return new HashMap<>();
    }

    @Synchronized
    private void scanBlocks() {

        log.debug("Start scanning blocks Qtum");
        ProfileData profileData = new ProfileData(500);

        final int lastReceivedBlock = Integer.parseInt(specParamsDao.getByMerchantNameAndParamName(merchant.getName(),
                qtumSpecParamName).getParamValue());
        Set<String> addresses = refillService.findAllAddresses(merchant.getId(), currency.getId()).stream().distinct().collect(Collectors.toSet());

        String blockNumberHash = qtumNodeService.getBlockHash(lastReceivedBlock);

        List<QtumTransaction> transactions = qtumNodeService.listSinceBlock(blockNumberHash).get().getTransactions();
        log.info("qtum transactions: " + transactions.toString());
        transactions.stream()
                .filter(t -> addresses.contains(t.getAddress()))
                .filter(t -> !refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(t.getAddress(),merchant.getId(),currency.getId(),t.getTxid()).isPresent())
                .filter(t -> transactions.stream().filter(tInner -> tInner.getTxid().equals(t.getTxid())).count() < 2)
                .filter(t -> t.getCategory().equals("receive"))
                .filter(t -> t.getConfirmations() >= minConfirmations)
                .filter(t -> t.getWalletconflicts().size() == 0)
                .filter(t -> t.isTrusted())
                .filter(t -> t.getAmount() > 0)
                .filter(t -> t.getVout() < 10)
                .forEach(t -> {
                            try {
                                log.info("before qtum transfer " + t.toString());
                                Map<String, String> mapPayment = new HashMap<>();
                                mapPayment.put("address", t.getAddress());
                                mapPayment.put("hash", t.getTxid());
                                mapPayment.put("amount", String.valueOf(t.getAmount()));
                                processPayment(mapPayment);
                                log.info("after qtum transfer");
                                specParamsDao.updateParam(merchant.getName(), qtumSpecParamName, String.valueOf(qtumNodeService.getBlock(t.getBlockhash()).getHeight()));
                            }catch (Exception e){
                                log.error(e);
                            }
                        }
                );
        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }

    private void checkBalanceAndTransfer(){
        log.debug("Start checking balance");
        ProfileData profileData = new ProfileData(500);

        qtumNodeService.setWalletPassphrase();

        BigDecimal balance = qtumNodeService.getBalance();
        if (balance.compareTo(minTransferAmount) > 0){
            qtumNodeService.transfer(mainAddressForTransfer, balance.subtract(minTransferAmount));
        }
        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }

    private void backupWallet(){
        log.debug("Start backup wallet");
        ProfileData profileData = new ProfileData(500);

        qtumNodeService.setWalletPassphrase();

        qtumNodeService.backupWallet();

        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
