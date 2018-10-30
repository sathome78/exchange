package me.exrates.service.ethereum;

import com.google.common.collect.Sets;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.EthAccountDao;
import me.exrates.model.dto.EthAccCredentials;
import me.exrates.model.dto.EthTransferAcc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2(topic = "eth_tokens_log")
@Component
public class EthAccountsHolder {

    private final BigDecimal feeAmount = new BigDecimal("0.01");

    private Set<EthTransferAcc> accs = Sets.newConcurrentHashSet();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private EthAccountDao ethAccountDao;

    @PostConstruct
    private void init() {
        accs.addAll(loadAllAccounts(Collections.singletonList(true)));
        log.debug("init accs size {}", accs.size());
        scheduler.scheduleAtFixedRate(this::loadAndCheckFreshAccounts, 1, 1, TimeUnit.MINUTES);
    }

    private Set<EthTransferAcc> loadAllAccounts(List<Boolean> states) {
        List<EthAccCredentials> ethAccCredentials = ethAccountDao.loadAll(states);
        log.debug("loaded accs size {}", ethAccCredentials.size());
        return ethAccCredentials.stream().map(EthTransferAcc::new).collect(Collectors.toSet());
    }

    private boolean hasEnougthBalance(EthTransferAcc acc) {
        log.debug("check balance acc {}", acc);
        BigDecimal ethBalance = BigDecimal.ZERO;
        try {
            ethBalance = Convert.fromWei(String.valueOf(acc.getWeb3j().ethGetBalance(acc.getCredentials().getAddress(), DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);
            log.debug("check balance {}", ethBalance);
        } catch (Exception e) {
            log.error(e);
        }
        return /*ethBalance.compareTo(feeAmount) >= 0*/true;
    }

    private EthTransferAcc findMostUnloadedAcc() {
        return accs.stream().min(Comparator.comparing(p->p.getCountOfUses().get())).orElseThrow(()->new RuntimeException("No availble ETH comission accounts"));
    }

    private void loadAndCheckFreshAccounts() {
        Set<EthTransferAcc> loadedAccs = loadAllAccounts(Collections.singletonList(true));
        log.debug("add new accs {}", loadedAccs.size());
        accs.retainAll(loadedAccs);
        accs.addAll(loadedAccs);
    }

    @Synchronized
    EthTransferAcc getAccForUse() {
        EthTransferAcc acc;
        do {
            acc = findMostUnloadedAcc();
            if (hasEnougthBalance(acc)) {
                return acc.useIt();
            } else {
                setUnactive(acc);
            }
        } while (!accs.isEmpty());
        throw new RuntimeException("No availble ETH comission accounts");
    }

    @Transactional
    public void setUnactive(EthTransferAcc acc) {
        accs.remove(acc);
        ethAccountDao.setStatus(acc.getId(), false);
    }
}
