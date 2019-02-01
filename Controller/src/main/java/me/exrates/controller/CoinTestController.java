package me.exrates.controller;

import me.exrates.CoinTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CoinTestController {

    @Autowired
    private ApplicationContext applicationContext;

    private StringBuilder logger = new StringBuilder("to be continued...");

    @GetMapping("/cointest/start")
    @ResponseBody
    public String startTesting(@RequestParam(name = "coin") String name, @RequestParam(name = "amount") String amount) throws Exception {
        logger = new StringBuilder();
        try {
            CoinTester kodTester = (CoinTester) applicationContext.getBean("ethTokenTester");
            kodTester.initBot(name.toUpperCase(), logger);
            kodTester.testCoin(amount);
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
