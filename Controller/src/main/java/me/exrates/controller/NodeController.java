package me.exrates.controller;

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

    @GetMapping("/listOfCoins")
    public List<String> listOfBitcoinServicesNames(){
        return nodeCheckerService.listOfRefillableServicesNames();
    }

    @GetMapping(value = "/getBlocksCount")
    public Object getBlocksCount(@RequestParam("ticker") String ticker) {
        try {
            return nodeCheckerService.getBTCBlocksCount(ticker);
        } catch (Exception e){
            return e.toString();
        }
    }

    @GetMapping(value = "/getLastBlockTime")
    public Object getLastBlockTime(@RequestParam("ticker") String ticker) {
        try {
            return nodeCheckerService.getLastBlockTime(ticker);
        } catch (Exception e){
            return e.toString();
        }
    }
}
