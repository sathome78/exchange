package me.exrates.service.eos;

import io.jafka.jeos.EosApi;
import io.jafka.jeos.EosApiFactory;
import io.jafka.jeos.core.response.chain.Block;
import io.jafka.jeos.core.response.history.transaction.Transaction;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.EosDataDto;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Log4j2(topic = "eos_log")
@Service
@Conditional(MonolitConditional.class)
@PropertySource(value = {"classpath:/merchants/eos.properties", "classpath:/env.properties"})
public class EosReceiveService {

    private EosApi client;
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";

    private static final String MERCHANT_NAME = "EOS";
    private static final String CURRENCY_NAME = "EOS";

    private static final String TRANSFER = "transfer";
    private static final String EOSIO_ACCOUNT = "eosio.token";
    private static final String EXECUTED = "executed";
    private static final int CONFIRMATIONS_NEEDED = 500;


    @Value("${eos.main.address}")
    private String mainAccount;

    @Value("${env.name}")
    private String environment;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private EosService eosService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
            client = EosApiFactory.create("http://127.0.0.1:8900",
                    "https://api.eosnewyork.io",
                    "https://api.eosnewyork.io");
            scheduler.scheduleWithFixedDelay(() -> {
                try {
                  checkRefills();
               } catch (Exception e) {
                  log.error(e);
               }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void checkRefills() {
        long lastBlock = loadLastBlock();
        long blockchainHeight = getLastBlockNum();
        int maxRound = 1000;

        for (int i = 0; lastBlock < (blockchainHeight - CONFIRMATIONS_NEEDED) && i < maxRound; i++){
            Block block = client.getBlock(String.valueOf(++lastBlock));
            List<Transaction> transactionList = Arrays.asList(block.getTransactions());
            transactionList.forEach(transaction -> {
                if (transaction.getStatus().equals(EXECUTED)) {
                    transaction.getTrx().ifPresent(trx -> {
                        List<io.jafka.jeos.core.common.Action> actions = trx.getTransaction().getActions();
                        actions.forEach(action -> {
                            String operation = action.getName();
                            if (operation.equalsIgnoreCase(TRANSFER) && action.getAccount().equals(EOSIO_ACCOUNT)) {
                                EosDataDto dataDto = new EosDataDto((LinkedHashMap) action.getData());
                                if (dataDto.getToAccount().equals(mainAccount) && dataDto.getCurrency().equals(CURRENCY_NAME)) {
                                    processTransaction(dataDto, trx.getId());
                                }
                            }
                        });
                    });
                }
            });
            if (lastBlock % 500 == 0){
                saveLastBlock(lastBlock);
            }
        }
        saveLastBlock(lastBlock);
    }

    private long getLastBlockNum() {
        return client.getChainInfo().getLastIrreversibleBlockNum();
    }

    private void processTransaction(EosDataDto dataDto, String hash) {
        Map<String, String> map = new HashMap<>();
        map.put("address", dataDto.getMemo());
        map.put("hash", hash);
        map.put("amount", dataDto.getAmount().toPlainString());

        eosService.processPayment(map);
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private Long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }
}
