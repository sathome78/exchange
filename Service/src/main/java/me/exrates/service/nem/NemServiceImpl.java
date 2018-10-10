package me.exrates.service.nem;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.CheckDestinationTagException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.PublicKey;
import org.nem.core.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2(topic = "nem_log")
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemServiceImpl implements NemService {

    @Autowired
    private NemTransactionsService nemTransactionsService;
    @Autowired
    private NemNodeService nodeService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private NemMosaicStrategy mosaicStrategy;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;


    private static final String NEM_MERCHANT = "NEM";
    private static final int CONFIRMATIONS_COUNT_WITHDRAW = 2; /*must be 20, but in this case its safe for us to check only 2 confirmations*/
    private static final int CONFIRMATIONS_COUNT_REFILL = 20;

    private static final BigDecimal maxMosiacQuantity = new BigDecimal("9000000000000000");
    private static final BigDecimal xemMaxQuantity = new BigDecimal("8999999999");
    private static final List<MosaicIdDto> deniedMosaicsList = new ArrayList<>();

    private Merchant merchant;
    private Currency currency;


    @PostConstruct
    public void init() {
        deniedMosaicsList.add(new MosaicIdDto("ts", "warning_dont_accept_stolen_funds"));
        /*deniedMosaicsList.add(new MosaicIdDto("dim", "coin"));*/
        account = new Account(new KeyPair(PublicKey.fromHexString(publicKey)));
        currency = currencyService.findByName("XEM");
        merchant = merchantService.findByName(NEM_MERCHANT);
    }


    private @Value("${nem.address}")String address;
    private @Value("${nem.private.key}")String privateKey;
    private @Value("${nem.public.key}")String publicKey;

    private static final String DESTINATION_TAG_ERR_MSG = "message.nem.tagError";

    protected Account account;

    @Override
    public Account getAccount() {
        return account;
    }

    @Transactional
    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.debug("withdraw_XEM");
        if (!"XEM".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        return nemTransactionsService.withdraw(withdrawMerchantOperationDto, privateKey);
    }

    @Transactional
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.XEM",
                new Object[]{address, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address",  destinationTag);
            put("message", message);
        }};
    }

    private String generateUniqDestinationTag(int userId) {
        Optional<Integer> id = null;
        String destinationTag;
        int counter = 0;
        do {
            destinationTag = generateDestinationTag(String.valueOf(userId).concat(String.valueOf(counter)));
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(
                    destinationTag,
                    currency.getId(),
                    merchant.getId());
            counter = counter + 8;
        } while (id.isPresent());
        log.debug("tag is {}", destinationTag);
        return destinationTag;
    }

    private String generateDestinationTag(String id) {
       return algorithmService.sha256(String.valueOf(id)).substring(0, 8);
    }


    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.warn("nem tx duplicated {}", hash);
            return;
        }
        BigDecimal amount = new BigDecimal(params.get("amount"));
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
        if (!nemTransactionsService.checkIsConfirmed(new JSONObject(params.get("transaction")), CONFIRMATIONS_COUNT_REFILL)) {
            try {
                refillService.putOnBchExamRefillRequest(
                        RefillRequestPutOnBchExamDto.builder()
                        .requestId(requestId)
                        .merchantId(requestAcceptDto.getMerchantId())
                        .currencyId(requestAcceptDto.getCurrencyId())
                        .address(requestAcceptDto.getAddress())
                        .amount(requestAcceptDto.getAmount())
                        .hash(requestAcceptDto.getMerchantTransactionId())
                        .build());
            } catch (RefillRequestAppropriateNotFoundException e) {
                log.error(e);
            }
        } else {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    @Synchronized
    @Override
    public void processMosaicPayment(List<NemMosaicTransferDto> mosaics, Map<String, String> params) {
        mosaics.forEach(p -> {
            String address = params.get("address");
            String hash = params.get("hash");
            XemMosaicService mosaicService = (XemMosaicService) p.getService();
            Currency currency = currencyService.findByName(mosaicService.getCurrencyName());
            Merchant merchant = merchantService.findByName(mosaicService.getMerchantName());
            if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
                log.warn("{} tx duplicated {}", p.getMosaicIdDto().getNamespaceId(),hash);
                return;
            }
            BigDecimal amount = p.getQuantity().divide(BigDecimal.valueOf(mosaicService.getDecimals()));
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                    .address(address)
                    .merchantId(merchantService.findByName(mosaicService.getMerchantName()).getId())
                    .currencyId(currency.getId())
                    .amount(amount)
                    .merchantTransactionId(hash)
                    .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                    .build();
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            if (!nemTransactionsService.checkIsConfirmed(new JSONObject(params.get("transaction")), CONFIRMATIONS_COUNT_REFILL)) {
                try {
                    refillService.putOnBchExamRefillRequest(
                            RefillRequestPutOnBchExamDto.builder()
                                    .requestId(requestId)
                                    .merchantId(requestAcceptDto.getMerchantId())
                                    .currencyId(requestAcceptDto.getCurrencyId())
                                    .address(requestAcceptDto.getAddress())
                                    .amount(requestAcceptDto.getAmount())
                                    .hash(requestAcceptDto.getMerchantTransactionId())
                                    .build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                try {
                    refillService.autoAcceptRefillRequest(requestAcceptDto);
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            }
        });


    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }



    @Override
    public void checkRecievedTransaction(RefillRequestFlatDto dto) throws RefillRequestAppropriateNotFoundException {
        JSONObject transaction = nodeService.getSingleTransactionByHash(dto.getMerchantTransactionId());
        if (nemTransactionsService.checkIsConfirmed(transaction, CONFIRMATIONS_COUNT_REFILL)) {
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                    .address(dto.getAddress())
                    .merchantId(dto.getMerchantId())
                    .currencyId(dto.getCurrencyId())
                    .amount(dto.getAmount())
                    .merchantTransactionId(dto.getMerchantTransactionId())
                    .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                    .build();
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } else {
            log.debug("transaction {} not confirmed yet", dto.getId());
        }
    }


    @Override
    public boolean checkSendedTransaction(String hash, String additionalParams) {
        JSONObject transaction = nodeService.getSingleTransactionByHash(hash);
        if (nemTransactionsService.checkIsConfirmed(transaction, CONFIRMATIONS_COUNT_WITHDRAW)) {
            return true;
        }
        nemTransactionsService.checkForOutdate(transaction);
        return false;
    }

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId) {
        log.error("comission merchant {}", merchantId);
        if (!merchantId.equals(merchant.getId())) {
            return countSpecComissionForMosaic(amount, destinationTag, merchantId);
        }
        return nemTransactionsService.countTxFee(amount, destinationTag).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal countSpecComissionForMosaic(BigDecimal amount, String destinationTag, Integer merchantId) {
        XemMosaicService service = mosaicStrategy.getByMerchantName(merchantService.findById(merchantId).getName());
        log.info("merchant {}", service);
        long quantity = amount.multiply(BigDecimal.valueOf(service.getDecimals())).longValue();
        BigDecimal exrate = getExrateForMosaic(merchantId);

        BigDecimal tokenLevy = countTokenLevy(quantity, service);

        BigDecimal feeForTagInXem = nemTransactionsService.countMessageFeeInNem(service.getDivisibility(), destinationTag);

        BigDecimal feeForAmountInXem = countFeeForAmountInXem(amount, service);

        BigDecimal baseFeesInToken = BigDecimalProcessing.doAction(BigDecimalProcessing.doAction(feeForTagInXem, feeForAmountInXem, ActionType.ADD),
                                                                    exrate, ActionType.DEVIDE);
        log.debug("fees - levy {}, forTag in nem {}, for quantity in nem {} exrate {}, basefees in token {}",
                tokenLevy, feeForTagInXem, feeForAmountInXem, exrate, baseFeesInToken);
        return BigDecimalProcessing.doAction(tokenLevy, baseFeesInToken, ActionType.ADD).setScale(service.getDivisibility(), RoundingMode.HALF_UP);
    }

    private BigDecimal countTokenLevy(long quantity, XemMosaicService service) {
        if (service.getLevyFee().getRaw() == 0) {
            return BigDecimal.ZERO;
        }
        double feeFromMosaicInMosaicToken = (quantity * service.getLevyFee().getRaw() / 10000D)/service.getDecimals();
        return BigDecimal.valueOf(feeFromMosaicInMosaicToken);
    }

    /*private BigDecimal countFeeForAmountInXem(BigDecimal amount, XemMosaicService service) {
        BigDecimal initFee = new BigDecimal(0.05);
        int multiplier = amount.intValue() / 10000;
        BigDecimal fee = BigDecimalProcessing.doAction(initFee, new BigDecimal(multiplier), ActionType.MULTIPLY);
        return BigDecimalProcessing.doAction(initFee, fee, ActionType.ADD);

    }*/

    private BigDecimal countFeeForAmountInXem(BigDecimal amount, XemMosaicService service) {
        BigDecimal totalMosiacQuantity = BigDecimalProcessing.doAction(BigDecimal.valueOf(service.getSupply().getRaw()),
                BigDecimal.valueOf(service.getDecimals()), ActionType.MULTIPLY);
        BigDecimal supplyRelatedAdjustment = new BigDecimal(0.8 * Math.log(BigDecimalProcessing
                .doAction(maxMosiacQuantity, totalMosiacQuantity, ActionType.DEVIDE)
                .doubleValue())).setScale(0, RoundingMode.HALF_EVEN);
        BigDecimal xemEqu = BigDecimalProcessing.doAction(BigDecimalProcessing.doAction(xemMaxQuantity,
                    BigDecimalProcessing.doAction(amount, new BigDecimal(service.getDecimals()), ActionType.MULTIPLY),
                ActionType.MULTIPLY), totalMosiacQuantity, ActionType.DEVIDE).setScale(0, RoundingMode.DOWN);
        BigDecimal xemFee = BigDecimalProcessing.doAction(xemEqu,
                BigDecimal.valueOf(10000), ActionType.DEVIDE).setScale(0, RoundingMode.DOWN);
        BigDecimal unewightedFee = BigDecimalProcessing.doAction(xemFee, supplyRelatedAdjustment, ActionType.SUBTRACT).max(new BigDecimal(1));
        return BigDecimalProcessing.doAction(unewightedFee, BigDecimal.valueOf(0.05), ActionType.MULTIPLY).min(BigDecimal.valueOf(1.25));
    }


    /*message must be not more than 512 bytes*/
    @Override
    public void checkDestinationTag(String destinationTag) {
        try {
            if (destinationTag.getBytes("UTF-8").length > 512) {
                throw new CheckDestinationTagException(DESTINATION_TAG_ERR_MSG, this.additionalWithdrawFieldName());
            }
        } catch (UnsupportedEncodingException e) {
            log.error("unsupported encoding {}", e);
        }
    }

    @Override
    public String getMainAddress() {
        return address;
    }

    private BigDecimal getExrateForMosaic(int merchantId) {
        return new BigDecimal(specParamsDao.getByMerchantIdAndParamName(merchantId, "exrateToNem").getParamValue());
    }

    @Override
    public List<MosaicIdDto> getDeniedMosaicList() {
        return deniedMosaicsList;
    }
}
