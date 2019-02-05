package me.exrates.controller;

import me.exrates.CoinDispatcher;
import me.exrates.CoinTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CoinTestController {

    private final CoinDispatcher coinDispatcher;

    private StringBuilder logger = new StringBuilder("to be continued...");

    @Autowired
    public CoinTestController(CoinDispatcher coinDispatcher) {
        this.coinDispatcher = coinDispatcher;
    }

    @GetMapping("/cointest/start")
    @ResponseBody
    public String startTesting(@RequestParam(name = "coin") String name, @RequestParam(name = "amount") String amount,
                               @RequestParam(name = "email", required = false) String email) throws Exception {
        logger = new StringBuilder();
        try {
            CoinTester coinTester = coinDispatcher.getCoinTester(name);
            coinTester.initBot(name.toUpperCase(), logger, email);
            coinTester.testCoin(amount);
        } catch (Exception e){
            logger.append(e.getMessage()).append("\n");
        }
        return "started";
    }

    @GetMapping(value = "/cointest/log", produces = "text/plain")
    @ResponseBody
    public String getLog(){
        return logger.toString();
    }

}
