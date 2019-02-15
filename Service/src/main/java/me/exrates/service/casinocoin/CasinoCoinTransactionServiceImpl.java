package me.exrates.service.casinocoin;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RippleCheckConsensusException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "casinocoin_log")
@Service
@PropertySource("classpath:/merchants/casinocoin.properties")
public class CasinoCoinTransactionServiceImpl implements CasinoCoinTransactionService {

    @Value("${casinocoin.amount.multiplier}")
    private Integer cscAmountMultiplier;

    @Value("${casinocoin.decimals}")
    private Integer cscDecimals;

    @Override
    public BigDecimal normalizeAmountToDecimal(String amount) {
        return new BigDecimal(amount).divide(new BigDecimal(cscAmountMultiplier)).setScale(cscDecimals, RoundingMode.HALF_DOWN);
    }

}
