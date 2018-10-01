package me.exrates.controller;

import me.exrates.service.EDCServiceNode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class UnsafeController {

    private final EDCServiceNode edcService;

    private static final Logger LOGGER = LogManager.getLogger(UnsafeController.class);

    @Autowired
    public UnsafeController(final EDCServiceNode edcService) {
        this.edcService = edcService;
    }
/*
    @RequestMapping(value = "unsafe/rescanEDCBlockchain")
    public ResponseEntity<String> rescanEDCBlockchain(@RequestParam("from") final int from, @RequestParam("to") final int to) {
        LOGGER.info("STARTING EDC BLOCKCHAIN RESCAN");
        final Thread job = new Thread(() -> blockchainEDC.rescanBlockchain(Math.abs(from), Math.abs(to)));
        job.start();
        return new ResponseEntity<>("Started blockchain rescan. Checkout merchant.log", HttpStatus.OK);
    }*/

   /* @RequestMapping(value = "unsafe/rescanUnusedAccounts")
    public ResponseEntity<String> rescanUnusedAccounts(){
        try {
            LOGGER.info("STARTING EDC RESCAN UNUSED ACCOUNTS");
            final Thread job = new Thread(() -> edcService.rescanUnusedAccounts());
            job.start();
            return new ResponseEntity<>("Started EDC rescan unused accouts. Checkout merchant.log", HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e);
            return new ResponseEntity<>("Error EDC rescan unused accouts. Checkout merchant.log", HttpStatus.BAD_REQUEST);
        }
    }*/
}
