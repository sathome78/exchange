package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.NemTransactionException;
import me.exrates.service.nem.NemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by maks on 20.07.2017.
 */
@Log4j2(topic = "nem_log")
@Service
@Conditional(MonolitConditional.class)
public class NemJobs {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private NemService nemService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;

    private Currency currency;
    private Merchant merchant;


    private final static ExecutorService executor = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        currency = currencyService.findByName("XEM");
        merchant = merchantService.findByName("NEM");
       /* scheduler.scheduleAtFixedRate(this::checkWithdrawals, 1, 5, TimeUnit.MINUTES);*/
        scheduler.scheduleAtFixedRate(this::checkReffils, 3, 5, TimeUnit.MINUTES);
    }

    private void checkWithdrawals() {
        List<WithdrawRequestFlatDto> dtos = withdrawService.getRequestsByMerchantIdAndStatus(merchant.getId(),
                Collections.singletonList(WithdrawStatusEnum.ON_BCH_EXAM.getCode()));
        if (dtos != null && !dtos.isEmpty()) {
            dtos.forEach(p-> executor.execute(() -> checkWithdraw(p.getId(), p.getTransactionHash(), p.getAdditionalParams())));
        }
    }

    private void checkWithdraw(int id, String hash, String additionalParams) {
        try {
            boolean checked = nemService.checkSendedTransaction(hash, additionalParams);
            if (checked) {
                withdrawService.finalizePostWithdrawalRequest(id);
            }
        } catch (NemTransactionException e) {
            log.error("nem transaction not included in block " + e);
            withdrawService.rejectToReview(id);
        } catch (Exception e) {
            log.error("nem transaction check error, will check it next time " + e);
        }
    }


    private void checkReffils() {
        log.debug("check reffils");
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
        if (dtos != null && !dtos.isEmpty()) {
            dtos.forEach((RefillRequestFlatDto p) -> {
                executor.execute(() -> {
                    checkRefill(p);
                });
            });
        }
    }

    private void checkRefill(RefillRequestFlatDto dto) {
        try {
            nemService.checkRecievedTransaction(dto);
        } catch (Exception e) {
            log.error(e);
            log.error("error checking nem tx confirmations {}", dto);
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

}
