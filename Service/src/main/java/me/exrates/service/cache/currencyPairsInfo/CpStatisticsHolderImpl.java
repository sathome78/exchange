package me.exrates.service.cache.currencyPairsInfo;


public class CpStatisticsHolderImpl  {


    /*private Map<Integer, Semaphore> synchronizersMap = new ConcurrentHashMap<>();
    private static final int SLEEP_TIME_MS = 2000;

    private final NgOrderService orderService;
    private final CpInfoRedisRepository redisRepository;
    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyPairsCache pairsCache;

    @Autowired
    public CpStatisticsHolderImpl(NgOrderService orderService, CpInfoRedisRepository redisRepository, StompMessenger stompMessenger, CurrencyPairsCache pairsCache) {
        this.orderService = orderService;
        this.redisRepository = redisRepository;
        this.stompMessenger = stompMessenger;
        this.pairsCache = pairsCache;
    }*/


    public void onOrderAccept(Integer pairId) {
      /*  if (!synchronizersMap.containsKey(pairId)) {
            putSynchronizer(pairId);
        }
        Semaphore semaphore = synchronizersMap.get(pairId);
        if (semaphore.tryAcquire()) {
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MS);
                ResponseInfoCurrencyPairDto dto = orderService.getCurrencyPairInfo(pairId);
                redisRepository.put(dto, pairId);
                stompMessenger.sendCpInfoMessage(pairsCache.getPairById(pairId).getName(), objectMapper.writeValueAsString(dto));
            } catch (Exception e) {
                log.error(e);
            } finally {
                semaphore.release();
            }
        }*/
    }
/*
    @Synchronized
    private void putSynchronizer(Integer pairId) {
        synchronizersMap.putIfAbsent(pairId, new Semaphore(1));
    }

    @Override
    public ResponseInfoCurrencyPairDto get(String pairName) {
        CurrencyPair cp = pairsCache.getPairByName(pairName);
        Preconditions.checkNotNull(cp);
        return get(cp.getId());
    }

    @Override
    public ResponseInfoCurrencyPairDto get(Integer pairId) {
        if (redisRepository.exist(pairId)) {
            return redisRepository.get(pairId);
        } else {
            ResponseInfoCurrencyPairDto dto = orderService.getCurrencyPairInfo(pairId);
            redisRepository.put(dto, pairId);
            return dto;
        }
    }*/




}
