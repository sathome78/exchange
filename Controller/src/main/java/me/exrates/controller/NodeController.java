package me.exrates.controller;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.NodeCheckerService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/nodes")
public class NodeController {

    private final NodeCheckerService nodeCheckerService;

    public NodeController(NodeCheckerService nodeCheckerService) {
        this.nodeCheckerService = nodeCheckerService;
    }

    @GetMapping(value = "/getBlocksCount")
    public Long getBlocksCount(@RequestParam("ticker") String ticker) throws BitcoindException, CommunicationException {
        return nodeCheckerService.getBTCBlocksCount(ticker);
    }
}
