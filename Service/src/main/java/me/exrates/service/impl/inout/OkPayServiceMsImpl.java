package me.exrates.service.impl.inout;

import lombok.SneakyThrows;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.MerchantOperationDto;
import me.exrates.service.MerchantService;
import me.exrates.service.RabbitService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.impl.NixMoneyServiceImpl;
import me.exrates.service.impl.OkPayServiceImpl;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
public class OkPayServiceMsImpl extends OkPayServiceImpl {

    private final RabbitService rabbitMqService;
    private int merchantId;

    public OkPayServiceMsImpl(RabbitService rabbitService, MerchantService merchantService) {
        this.rabbitMqService = rabbitService;
        merchantId = merchantService.findByName(NixMoneyServiceImpl.MERCHANT_NAME).getId();
    }

    @Override
    @SneakyThrows
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        rabbitMqService.sendAcceptMerchantEvent(new MerchantOperationDto(merchantId, params));

    }
}
