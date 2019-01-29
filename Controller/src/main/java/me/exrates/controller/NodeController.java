package me.exrates.controller;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.NodeCheckerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/nodes")
public class NodeController {

    private final NodeCheckerService nodeCheckerService;

    public NodeController(NodeCheckerService nodeCheckerService) {
        this.nodeCheckerService = nodeCheckerService;
    }

    @GetMapping("/listOfCoins") //todo make return all coins that implements IRefillable
    public List<String> listOfBitcoinServicesNames(){
        return nodeCheckerService.listOfRefillableServicesNames();
    }

    @GetMapping(value = "/getBlocksCount")
    public Long getBlocksCount(@RequestParam("ticker") String ticker) throws BitcoindException, CommunicationException {
        return nodeCheckerService.getBTCBlocksCount(ticker);
    }

    @GetMapping(value = "/getLastBlockTime")
    public Long getLastBlockTime(@RequestParam("ticker") String ticker) throws BitcoindException, CommunicationException {
        return nodeCheckerService.getLastBlockTime(ticker);
    }
}
