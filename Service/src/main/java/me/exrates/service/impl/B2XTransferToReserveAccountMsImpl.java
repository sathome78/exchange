package me.exrates.service.impl;

import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.B2XTransferToReserveAccount;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(MicroserviceConditional.class)
public class B2XTransferToReserveAccountMsImpl implements B2XTransferToReserveAccount {
    @Override
    public void transferToReserveAccountFromNode(int countTransactions, String amount) {

    }
}
