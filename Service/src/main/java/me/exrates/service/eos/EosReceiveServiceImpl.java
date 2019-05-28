package me.exrates.service.eos;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jafka.jeos.EosApi;
import io.jafka.jeos.EosApiFactory;
import io.jafka.jeos.core.common.transaction.PackedTransaction;
import io.jafka.jeos.core.common.transaction.SignedPackedTransaction;
import io.jafka.jeos.core.common.transaction.TransactionAction;
import io.jafka.jeos.core.common.transaction.TransactionAuthorization;
import io.jafka.jeos.core.request.chain.json2bin.TransferArg;
import io.jafka.jeos.core.response.chain.AbiJsonToBin;
import io.jafka.jeos.core.response.chain.Block;
import io.jafka.jeos.core.response.chain.transaction.PushedTransaction;
import io.jafka.jeos.core.response.history.transaction.Transaction;
import io.jafka.jeos.exception.EosApiException;
import io.jafka.jeos.impl.EosApiServiceGenerator;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.EosDataDto;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
public class EosReceiveServiceImpl implements EosReceiveService {

    private EosApi client;
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";

    private static final String MERCHANT_NAME = "EOS";
    private static final String CURRENCY_NAME = "EOS";

    private static final String TRANSFER = "transfer";
    private static final String EOSIO_ACCOUNT = "eosio.token";
    private static final String EXECUTED = "executed";

    private String mainAccount = "";

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private EosService eosService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        BasicConfigurator.configure();
        client = EosApiFactory.create("http://127.0.0.1:8900", //
                "https://api.eosnewyork.io",//
                "https://api.eosnewyork.io");
        scheduler.scheduleAtFixedRate(this::checkRefills, 5, 5, TimeUnit.MINUTES);
    }


    private void checkRefills() {
        long lastBlock = loadLastBlock();
        long blockchainHeight = getLastBlockNum();
        while (lastBlock < blockchainHeight) {
            Block block = client.getBlock(String.valueOf(++lastBlock));
            List<Transaction> transactionList = Arrays.asList(block.getTransactions());
            transactionList.forEach(p -> {
                if (p.getStatus().equals(EXECUTED)) {
                    p.getTrx().ifPresent(s -> {
                        List<io.jafka.jeos.core.common.Action> actions = s.getTransaction().getActions();
                        actions.forEach(a -> {
                            String operation = a.getName();
                            if (operation.equalsIgnoreCase(TRANSFER) && a.getAccount().equals(EOSIO_ACCOUNT)) {
                                EosDataDto dataDto = new EosDataDto((LinkedHashMap) a.getData());
                                if (dataDto.getToAccount().equals(mainAccount) && dataDto.getCurrency().equals(CURRENCY_NAME)) {
                                    processTransaction(dataDto, s.getId());
                                }
                            }
                        });
                    });
                }
            });
            saveLastBlock(lastBlock);
        }
    }

    private long getLastBlockNum() {
        return client.getChainInfo().getLastIrreversibleBlockNum();
    }

    private void processTransaction(EosDataDto dataDto, String hash) {
        Map<String, String> map = new HashMap<>();
        map.put("address", dataDto.getMemo());
        map.put("hash", hash);
        map.put("amount", dataDto.getAmount().toPlainString());
        map.put("to", dataDto.getToAccount());
        map.put("from", dataDto.getFromAccount());

        eosService.processPayment(map);
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private Long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        BasicConfigurator.configure();
        EosApi client = EosApiFactory.create("http://127.0.0.1:8900", //
                "https://api.eosnewyork.io",//
                "https://api.eosnewyork.io");
        // ------------------------------------------------------------------------

        /*Block block = client.getBlock("14643107");
        List<Transaction> transactionList = Arrays.asList(block.getTransactions());
        transactionList.forEach(p -> {
            p.getTrx().ifPresent(s->{
                List<io.jafka.jeos.core.common.Action> actions = s.getTransaction().getActions();
                actions.forEach(a->{
                    System.out.println(a.getAccount());
                    String operation = a.getName();
                    if (operation.equalsIgnoreCase(TRANSFER)) {
                        EosDataDto dataDto = null;
                        try {
                            LinkedHashMap map = (LinkedHashMap) a.getData();
                            dataDto = new EosDataDto(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(dataDto);
                    }
                });

            });
        });*/

        System.out.println(client.getChainInfo());
    }
}
