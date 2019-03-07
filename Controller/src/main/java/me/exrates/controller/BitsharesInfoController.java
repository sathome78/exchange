package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.BTSBlockInfo;
import me.exrates.service.bitshares.BitsharesDispatcher;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bitshares")
@Conditional(MonolitConditional.class)
public class BitsharesInfoController {

    private final BitsharesDispatcher bitsharesDispatcher;

    public BitsharesInfoController(BitsharesDispatcher bitsharesDispatcher) {
        this.bitsharesDispatcher = bitsharesDispatcher;
    }

    @GetMapping("/requestBlockInfo")
    public String requestBlockInfo(@RequestParam("blockNum") int blockNum, @RequestParam("previousHash") String previousHash, @RequestParam("name") String merchantName){
        try {
            bitsharesDispatcher.requestForBlockInfo(merchantName, new BTSBlockInfo(blockNum, previousHash));
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Done";
    }

    @GetMapping("/getBlockInfo")
    public String getBlockInfo(@RequestParam("blockNum") int blockNum, @RequestParam("name") String merchantName){
        try {
            return new ObjectMapper().writeValueAsString(bitsharesDispatcher.getBlocksInfo(merchantName, blockNum));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
