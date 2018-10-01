package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.BotDao;
import me.exrates.model.*;
import me.exrates.model.dto.BotTradingSettingsShortDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.*;
import me.exrates.service.*;
import me.exrates.service.exception.BotException;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.job.bot.BotCreateOrderJob;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2(topic = "bot_trader")
@PropertySource("classpath:/bot_trader.properties")
public class BotServiceImpl implements BotService {

    private @Value("${bot.error.noMoney.email}") String errorEmail;

    private static final String JOB_FORMAT = "job_%s_%s";

    private static final String TRIGGER_FORMAT = "trigger_%s_%s";

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private BotDao botDao;

    @Autowired
    private Scheduler botOrderCreationScheduler;


    private final static ExecutorService botAcceptExecutors = Executors.newFixedThreadPool(10);

    @PostConstruct
    private void initBot() {
        retrieveBotFromDB().ifPresent(botTrader -> {
            if (botTrader.isEnabled()) {
                scheduleJobsForActiveCurrencyPairs(botTrader.getId());
            }
        });
    }

    private void scheduleJobsForActiveCurrencyPairs(Integer botId) {
        botDao.retrieveLaunchSettingsForAllPairs(botId, true).forEach(settings -> {
            scheduleJobsForCurrencyPair(settings.getCurrencyPairId(), settings.getLaunchIntervalInMinutes());
        });
    }

    @PreDestroy
    private void shutdownBot() {
        retrieveBotFromDB().ifPresent(botTrader -> {
            try {
                botOrderCreationScheduler.shutdown();
            } catch (SchedulerException e) {
                log.error(e);
            }
            botAcceptExecutors.shutdown();


        });
    }


    @Override
    @Transactional
    public void acceptAfterDelay(ExOrder exOrder) {
        if (checkNeedToAccept(exOrder)) {
            retrieveBotFromDB().ifPresent(botTrader -> {
                if (botTrader.isEnabled()) {
                    botAcceptExecutors.execute(() -> {
                        try {
                            Thread.sleep(botTrader.getAcceptDelayInMillis());
                            log.debug("Accepting order: {}", exOrder);
                            orderService.acceptOrdersList(botTrader.getUserId(), Collections.singletonList(exOrder.getId()), Locale.ENGLISH);
                        } catch (InsufficientCostsForAcceptionException e) {
                            Email email = new Email();
                            email.setMessage("Insufficient costs on bot account");
                            email.setSubject(e.getMessage());
                            email.setTo(errorEmail);
             //               sendMailService.sendInfoMail(email);
                            log.warn(e.getMessage());
                        } catch (OrderAcceptionException e) {
                            log.warn(e.getMessage());
                        } catch (InterruptedException e) {
                            log.error(e);
                        }
                    });
                }
            });
        }
    }

    private boolean checkNeedToAccept(ExOrder exOrder) {
        UserRoleSettings userRoleSettings = userRoleService.retrieveSettingsForRole(
                userService.getUserRoleFromDB(exOrder.getUserId()).getRole());

        return exOrder.getOrderBaseType() == OrderBaseType.LIMIT && userRoleSettings.isBotAcceptionAllowedOnly();

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BotTrader> retrieveBotFromDB() {
        return botDao.retrieveBotTrader();
    }

    @Override
    @Transactional
    public void createBot(String nickname, String email, String password) {
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(UserRole.BOT_TRADER);
        user.setStatus(UserStatus.ACTIVE);

        userService.createUserByAdmin(user);
        Integer userId = userService.getIdByEmail(email);
        referralService.bindChildAndParent(userId, userService.getCommonReferralRoot().getId());
        botDao.createBot(userId);
    }

    @Override
    @Transactional
    public void updateBot(BotTrader botTrader, Locale locale) {
        BotTrader oldBot = botDao.findById(botTrader.getId()).orElseThrow(() -> new BotException(messageSource.getMessage("admin.autoTrading.bot.notFound",
                new Object[]{botTrader.getId()}, locale)));
        if (botTrader.isEnabled() != oldBot.isEnabled()) {
            if (botTrader.isEnabled()) {
                scheduleJobsForActiveCurrencyPairs(botTrader.getId());
            } else {
                try {
                    botOrderCreationScheduler.clear();
                } catch (SchedulerException e) {
                    throw new BotException(e.getMessage());
                }

            }
        }
        botDao.updateBot(botTrader);
    }


    @Override
    public void runOrderCreation(Integer currencyPairId, OrderType orderType) {
        retrieveBotFromDB().ifPresent(bot -> {
            CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
            runOrderCreationSequence(currencyPair, orderType, bot);
        });
    }

    private void runOrderCreationSequence(CurrencyPair currencyPair, OrderType orderType, BotTrader botTrader) {
        botDao.retrieveBotTradingSettingsForCurrencyPairAndOrderType(botTrader.getId(), currencyPair.getId(), orderType).ifPresent(settings -> {
            BotTradingCalculator calculator = new BotTradingCalculator(settings);
            String userEmail = userService.getEmailById(botTrader.getUserId());
            OperationType operationType = OperationType.valueOf(orderType.name());
            PriceGrowthDirection initialDirection = calculator.getDirection();
            BigDecimal lastPrice = orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, operationType)
                    .orElse(calculator.getLowerPriceBound());
            for(int i = 0; i < settings.getBotLaunchSettings().getQuantityPerSequence(); i++) {
                try {
                    int timeout = (int) new RandomDataGenerator().nextUniform(100, settings.getBotLaunchSettings()
                            .getCreateTimeoutInSeconds() * 1000);
                    Thread.sleep(timeout);
                    BigDecimal newPrice = calculator.nextPrice(lastPrice);
                    prepareAndSaveOrder(currencyPair, operationType, userEmail, calculator.getRandomizedAmount(), newPrice);
                    lastPrice = newPrice;
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (calculator.getDirection() != initialDirection) {
                botDao.updatePriceGrowthDirection(settings.getId(), calculator.getDirection());
            }
        });
    }


    @Override
    @Transactional
    public void prepareAndSaveOrder(CurrencyPair currencyPair, OperationType operationType, String userEmail, BigDecimal amount, BigDecimal rate) {
        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(currencyPair, operationType, userEmail, amount, rate, OrderBaseType.LIMIT);
        log.debug("Prepared order: {}", orderCreateDto);
       // orderService.createOrder(orderCreateDto, OrderActionEnum.CREATE);
        orderService.postBotOrderToDb(orderCreateDto);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableBotForCurrencyPair(Integer currencyPairId, Locale locale) {
        retrieveBotFromDB().ifPresent(bot -> {
            if (bot.isEnabled()) {
                BotLaunchSettings launchSettings =  botDao.retrieveBotLaunchSettingsForCurrencyPair(bot.getId(), currencyPairId);
                if (!launchSettings.isEnabledForPair()) {
                    botDao.setEnabledForCurrencyPair(bot.getId(), currencyPairId, true);
                    scheduleJobsForCurrencyPair(currencyPairId, launchSettings.getLaunchIntervalInMinutes() );
                }
            } else {
                throw new BotException(messageSource.getMessage("admin.autoTrading.bot.notEnabled", null, locale));
            }
        });

    }

    private void scheduleJobsForCurrencyPair(Integer currencyPairId, int intervalInMinutes) {

        int intervalInSeconds = intervalInMinutes * 60;
        try {
            scheduleJobForCurrencyPairAndOrderType(currencyPairId, OrderType.SELL, intervalInSeconds);
            scheduleJobForCurrencyPairAndOrderType(currencyPairId, OrderType.BUY, intervalInSeconds);
        } catch (SchedulerException e) {
            log.error(e);
            throw new BotException(e.getMessage());
        }
    }

    private void scheduleJobForCurrencyPairAndOrderType(Integer currencyPairId, OrderType orderType, Integer intervalInSeconds) throws SchedulerException {
        JobDetail jobDetail = createJobDetail(currencyPairId, orderType);
        Trigger trigger = createTrigger(currencyPairId, orderType, intervalInSeconds);
        log.info("SCHEDULING JOB FOR PAIR ID " + currencyPairId + " " + orderType.name());
        botOrderCreationScheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail createJobDetail(Integer currencyPairId, OrderType orderType) {
        return JobBuilder.newJob(BotCreateOrderJob.class)
                    .withIdentity(getJobName(currencyPairId, orderType))
                    .usingJobData("currencyPairId", currencyPairId)
                    .usingJobData("orderType", orderType.name())
                    .build();
    }

    private Trigger createTrigger(Integer currencyPairId, OrderType orderType, Integer intervalInSeconds) {
        return TriggerBuilder.newTrigger()
                .withIdentity(getTriggerName(currencyPairId, orderType))
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .withMisfireHandlingInstructionNowWithRemainingCount()
                        .repeatForever())
                .build();
    }

    private Trigger createTrigger(Integer currencyPairId, OrderType orderType, Integer intervalInSeconds, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerName(currencyPairId, orderType))
                    .forJob(jobDetail)
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalInSeconds)
                            .withMisfireHandlingInstructionNowWithRemainingCount()
                            .repeatForever())
                    .build();
    }

    private String getJobName(Integer currencyPairId, OrderType orderType) {
        return String.format(JOB_FORMAT, currencyPairId, orderType.name());
    }

    private String getTriggerName(Integer currencyPairId, OrderType orderType) {
        return String.format(TRIGGER_FORMAT, currencyPairId, orderType.name());
    }

    @Override
    @Transactional
    public void disableBotForCurrencyPair(Integer currencyPairId) {
        retrieveBotFromDB().ifPresent(bot -> {
            botDao.setEnabledForCurrencyPair(bot.getId(), currencyPairId, false);
            try {
                botOrderCreationScheduler.deleteJob(JobKey.jobKey(getJobName(currencyPairId, OrderType.SELL)));
                botOrderCreationScheduler.deleteJob(JobKey.jobKey(getJobName(currencyPairId, OrderType.BUY)));
            } catch (SchedulerException e) {
                log.error(e);
                throw new BotException(e.getMessage());
            }

        });

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BotTradingSettingsShortDto> retrieveTradingSettingsShort(int botLaunchSettingsId) {
        BotTradingSettingsShortDto sellSettings = botDao.retrieveTradingSettingsShort(botLaunchSettingsId, OrderType.SELL.getType());
        BotTradingSettingsShortDto buySettings = botDao.retrieveTradingSettingsShort(botLaunchSettingsId, OrderType.BUY.getType());

        return new HashMap<String, BotTradingSettingsShortDto>() {{
            put(OrderType.SELL.name(), sellSettings);
            put(OrderType.BUY.name(), buySettings);
        }};
    }

    @Override
    @Transactional(readOnly = true)
    public List<BotLaunchSettings> retrieveLaunchSettings(int botId) {
        return botDao.retrieveLaunchSettingsForAllPairs(botId, null);
    }

    @Override
    @Transactional
    public void toggleBotStatusForCurrencyPair(Integer currencyPairId, boolean status, Locale locale) {
        if (status) {
            enableBotForCurrencyPair(currencyPairId, locale);
        } else {
            disableBotForCurrencyPair(currencyPairId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLaunchSettings(BotLaunchSettings launchSettings) {
        botDao.updateLaunchSettings(launchSettings);
        try {
            rescheduleJob(launchSettings.getCurrencyPairId(), OrderType.SELL, launchSettings.getLaunchIntervalInMinutes());
            rescheduleJob(launchSettings.getCurrencyPairId(), OrderType.BUY, launchSettings.getLaunchIntervalInMinutes());
        } catch (SchedulerException e) {
            log.error(e);
            throw new BotException(e.getMessage());
        }


    }

    private void rescheduleJob(int currencyPairId, OrderType orderType, int intervalInMinutes) throws SchedulerException {
        int intervalInSeconds = intervalInMinutes * 60;
        TriggerKey triggerKey = TriggerKey.triggerKey(getTriggerName(currencyPairId, orderType));
        JobDetail jobDetail = botOrderCreationScheduler.getJobDetail(JobKey.jobKey(getJobName(currencyPairId, orderType)));
        if (jobDetail != null) {
            botOrderCreationScheduler.rescheduleJob(triggerKey, createTrigger(currencyPairId, orderType, intervalInSeconds, jobDetail));
        }
    }

    @Override
    @Transactional
    public void updateTradingSettings(BotTradingSettingsShortDto tradingSettings) {
        botDao.updateTradingSettings(tradingSettings);

    }

    @Override
    @Transactional
    public void setConsiderUserOrders(int launchSettingsId, boolean considerUserOrders) {
        botDao.setConsiderUserOrders(launchSettingsId, considerUserOrders);
    }



}
