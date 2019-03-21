package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class B2XTransferToReserveAccountImpl implements B2XTransferToReserveAccount {

    private final static String RESERVE_ADDRESS = "1P6fgfBhPtcuLPrG2tScVY4QG5owiHnzst";

    @Autowired
    @Qualifier("b2xServiceImpl")
    private BitcoinService bitcoinService;

    @Override
    public void transferToReserveAccountFromNode(int countTransactions, String amount){
        try {
            List<String> allTx = new ArrayList<>();
            for (int i = 0; i < countTransactions; i++) {
                try {
                    Map<String, String> txId = bitcoinService.withdraw(WithdrawMerchantOperationDto.builder().accountTo(RESERVE_ADDRESS).amount(amount).build());
                    allTx.add(txId.get("hash"));
                }catch(Exception e){
                    log.error("B2X TRANSFER | TRANSACTION ERROR {}", e);
                }
            }

            saveToExcelFile(allTx);

            log.debug("B2X TRANSFER | Transactions count: {}", allTx.size());
        }catch (Exception e){
            log.error("B2X TRANSFER | ERROR {}", e);
        }

    }

    private void saveToExcelFile(List<String> transactionsId){
        try {
            B2XTransferReportTransactionId.generate(transactionsId);
        } catch (Exception e) {
            log.error("B2X TRANSFER | Save to excel ERROR file: {}", e);
        }
    }

}
