package me.exrates.service.messaging;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.chartservicemodels.TradeDataDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RabbitMessengerImpl  implements RabbitMessnger {

    private @Value("${rabbit.chartservice.queue}")
    String chartServiceTopic;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMessengerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendTradeInfoToChartService(TradeDataDto dataDto) {
        try {
            System.out.println("send to chart service " + dataDto);
            rabbitTemplate.convertAndSend(chartServiceTopic, dataDto);
        } catch (Exception e) {
            log.error("error sending message to redis ", e);
        }
    }

}
