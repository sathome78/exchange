package me.exrates.service.casinocoin;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Log4j2(topic = "casinocoin_log")
@Service
@PropertySource("classpath:/merchants/casinocoin.properties")
@Conditional(MonolitConditional.class)
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
