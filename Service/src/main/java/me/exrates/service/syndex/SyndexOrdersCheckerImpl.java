package me.exrates.service.syndex;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.model.enums.SyndexOrderStatusEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2(topic = "syndex")
@Component
public class SyndexOrdersCheckerImpl implements SyndexOrderChecker {

    private final SyndexService syndexService;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final ExecutorService checkerThreads = Executors.newFixedThreadPool(1);
    private final List<Integer> inPendingStatuses = Arrays.stream(SyndexOrderStatusEnum.values())
                                                            .filter(SyndexOrderStatusEnum::isInPendingStatus)
                                                            .map(SyndexOrderStatusEnum::getStatusId)
                                                            .collect(Collectors.toList());

    public SyndexOrdersCheckerImpl(SyndexService syndexService) {
        this.syndexService = syndexService;
    }

    @PostConstruct
    private void init() {
        executorService.scheduleWithFixedDelay(this::check, 1, 2, TimeUnit.MINUTES);
    }

    @Override
    public void check() {
        List<SyndexOrderDto> orderDtos = syndexService.getAllPendingPayments(inPendingStatuses, null);
        log.debug("check syndex orders");
        orderDtos.forEach(p -> {
            try {
                CompletableFuture.runAsync(() -> syndexService.checkOrder(p.getSyndexId()), checkerThreads);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }
}
