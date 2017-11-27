package me.exrates.service.neo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.neo.AssetMerchantCurrencyDto;
import me.exrates.model.dto.merchants.neo.NeoAsset;
import me.exrates.model.dto.merchants.neo.NeoTransaction;
import me.exrates.model.dto.merchants.neo.NeoVout;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.NeoApiException;
import me.exrates.service.exception.NeoPaymentProcessingException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.util.ParamMapUtils;
import me.exrates.service.vo.ProfileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j2(topic = "neo_log")
@Service("neoServiceImpl")
@PropertySource("classpath:/merchants/neo.properties")
public class NeoServiceImpl implements NeoService {
    @Autowired
    private RefillService refillService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private NeoNodeService neoNodeService;

    @Autowired
    private MessageSource messageSource;

    private @Value("${neo.main.address}") String mainAccount;
    private @Value("${neo.min.confirmations}") Integer minConfirmations;

    private Map<String, AssetMerchantCurrencyDto> neoAssetMap;

    private final String neoMerchantName = "NEO";
    private final String gasMerchantName = "GAS";
    private final String neoCurrencyName = "NEO";
    private final String gasCurrencyName = "GAS";
    private final String neoSpecParamName = "LastRecievedBlock";


    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        Merchant merchantNeo = merchantService.findByName(neoMerchantName);
        Currency currencyNeo = currencyService.findByName(neoCurrencyName);

        Merchant merchantGas = merchantService.findByName(gasMerchantName);
        Currency currencyGas = currencyService.findByName(gasCurrencyName);


        neoAssetMap = new HashMap<String, AssetMerchantCurrencyDto>() {{
            put(NeoAsset.NEO.getId(), new AssetMerchantCurrencyDto(NeoAsset.NEO, merchantNeo, currencyNeo));
            put(NeoAsset.GAS.getId(), new AssetMerchantCurrencyDto(NeoAsset.GAS, merchantGas, currencyGas));
        }};

        scheduler.scheduleAtFixedRate(() -> {
            try {
                scanLastBlocksAndUpdatePayments();
            } catch (Exception e) {
                log.error(e);
            }

        }, 20, 120, TimeUnit.SECONDS);
    }


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {

        String address = neoNodeService.getNewAddress();

        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        String address = ParamMapUtils.getIfNotNull(params, "address");
        NeoTransaction transaction = neoNodeService.getTransactionById(txId).orElseThrow(() -> new NeoPaymentProcessingException("Transaction not found: " + txId));
        NeoVout vout = transaction.getVout().stream().filter(out -> address.equals(out.getAddress()))
                .findFirst().orElseThrow(() -> new NeoPaymentProcessingException(String.format("Tx %s does not contain address %s", txId, address)));
        processNeoPayment(txId, vout, transaction.getConfirmations(), transaction.getBlockhash());
    }



    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        BigDecimal withdrawAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
        NeoAsset asset = NeoAsset.valueOf(withdrawMerchantOperationDto.getCurrency());
        try {
            NeoTransaction neoTransaction = neoNodeService.sendToAddress(asset, withdrawMerchantOperationDto.getAccountTo(), withdrawAmount, mainAccount);
            return Collections.singletonMap("hash", neoTransaction.getTxid());
        } catch (NeoApiException e) {
           if (e.getCode() == -300) {
               throw new InsufficientCostsInWalletException();
           } else if (e.getCode() == -2146233033) {
               throw new InvalidAccountException();
           } else {
               throw new MerchantException(e.getMessage());
           }
        }
        catch (Exception e) {
            throw new MerchantException(e);
        }
    }


    @Override
 //   @Scheduled(initialDelay = 120000, fixedDelay = 120000)
    public void scanLastBlocksAndUpdatePayments() {
        log.debug("Start scanning blocks");
        ProfileData profileData = new ProfileData(500);
        scanBlocks();
        profileData.setTime1();
        updateExistingPayments();
        profileData.setTime2();
        log.debug("Profile results: " + profileData);

    }

    void scanBlocks() {
        Merchant merchantNeo = merchantService.findByName(neoMerchantName);
        Currency currencyNeo = currencyService.findByName(neoCurrencyName);
        final int lastReceivedBlock = Integer.parseInt(specParamsDao.getByMerchantIdAndParamName(neoMerchantName,
                neoSpecParamName).getParamValue());
        final int blockCount = neoNodeService.getBlockCount();
        Set<String> addresses = refillService.findAllAddresses(merchantNeo.getId(), currencyNeo.getId()).stream().distinct().collect(Collectors.toSet());
        List<NeoVout> outputs = IntStream.range(lastReceivedBlock, blockCount).parallel().mapToObj(neoNodeService::getBlock).filter(Optional::isPresent).map(Optional::get)
                .flatMap(block -> block.getTx().stream().filter(tx -> "ContractTransaction".equals(tx.getType()))
                        .flatMap(tx -> tx.getVout().stream().filter(vout -> addresses.contains(vout.getAddress()))
                                .filter(vout -> neoAssetMap.containsKey(vout.getAsset()))
                                .peek(vout -> {
                                    try {
                                        processNeoPayment(tx.getTxid(), vout, block.getConfirmations(), block.getHash());
                                    } catch (Exception e) {
                                        log.error(e);
                                    }
                                }))
                ).collect(Collectors.toList());
        log.debug("Processed outputs: " + outputs);
        specParamsDao.updateParam(neoMerchantName, neoSpecParamName, String.valueOf(blockCount));
    }



    void updateExistingPayments() {
        log.debug("Check and update existing payments");
        neoAssetMap.forEach((key, value) ->
                refillService.getInExamineByMerchantIdAndCurrencyIdList(value.getMerchant().getId(), value.getCurrency().getId())
        .stream().flatMap(dto -> Stream.of(neoNodeService.getTransactionById(dto.getMerchantTransactionId())).filter(Optional::isPresent).map(Optional::get)
                        .flatMap(tx -> tx.getVout().stream().filter(vout -> dto.getAddress().equals(vout.getAddress())).peek(vout -> {
                            try {
                                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                                        .requestId(dto.getId())
                                        .address(vout.getAddress())
                                        .amount(new BigDecimal(vout.getValue()))
                                        .confirmations(tx.getConfirmations())
                                        .currencyId(dto.getCurrencyId())
                                        .merchantId(dto.getMerchantId())
                                        .hash(dto.getMerchantTransactionId())
                                        .blockhash(tx.getBlockhash()).build(), vout.getAsset());
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }))).forEach(vout -> log.debug("Payment updated: " + vout)));

    }



    void processNeoPayment(String txId, NeoVout vout, Integer confirmations, String blockhash) {
        AssetMerchantCurrencyDto assetMerchantCurrencyDto = neoAssetMap.get(vout.getAsset());
        String address = vout.getAddress();
        BigDecimal amount = new BigDecimal(vout.getValue());
        int merchantId = assetMerchantCurrencyDto.getMerchant().getId();
        int currencyId = assetMerchantCurrencyDto.getCurrency().getId();

        Optional<Integer> refillRequestIdOnExamResult = refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(address,
                merchantId, currencyId, txId);
        if (refillRequestIdOnExamResult.isPresent()) {
            Integer refillRequestIdOnExam = refillRequestIdOnExamResult.get();
            changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                    .requestId(refillRequestIdOnExam)
                    .address(address)
                    .amount(amount)
                    .confirmations(confirmations)
                    .currencyId(currencyId)
                    .merchantId(merchantId)
                    .hash(txId)
                    .blockhash(blockhash).build(), vout.getAsset());
        } else {
            Optional<Integer> refillRequestIdResult = refillService.getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(
                    address, merchantId, currencyId);
            Integer requestId = refillRequestIdResult.orElseGet(() -> {
                        RefillRequestAcceptDto refillRequestAcceptDto = RefillRequestAcceptDto.builder()
                                .address(address)
                                .amount(amount)
                                .merchantId(merchantId)
                                .currencyId(currencyId)
                                .merchantTransactionId(txId).build();

                log.debug("Create request by fact! : " + refillRequestAcceptDto);
                return refillService.createRefillRequestByFact(refillRequestAcceptDto);
            });
            if (confirmations >= 0 && confirmations < minConfirmations) {
                try {
                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestId)
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(address)
                            .amount(amount)
                            .hash(txId)
                            .blockhash(blockhash).build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(requestId)
                        .address(address)
                        .amount(amount)
                        .confirmations(confirmations)
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(txId)
                        .blockhash(blockhash).build(), vout.getAsset());
            }
        }
    }

    void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto, String assetId) {
        try {
            if (dto.getConfirmations() != null) {
                refillService.setConfirmationCollectedNumber(dto);
                if (dto.getConfirmations() >= minConfirmations) {
                    log.debug("Providing transaction!");
                    RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                            .requestId(dto.getRequestId())
                            .address(dto.getAddress())
                            .amount(dto.getAmount())
                            .currencyId(dto.getCurrencyId())
                            .merchantId(dto.getMerchantId())
                            .merchantTransactionId(dto.getHash())
                            .build();
                    refillService.autoAcceptRefillRequest(requestAcceptDto);
                    transferCostsToMainAccount(assetId, dto.getAmount());
                }
            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }

    }

    private void transferCostsToMainAccount(String assetId, BigDecimal amount) {
        neoNodeService.sendToAddress(neoAssetMap.get(assetId).getAsset(), mainAccount, amount, mainAccount);
    }

}
