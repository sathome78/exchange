package me.exrates.service;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;

public interface NodeCheckerService {
    Long getBTCBlocksCount(String ticker) throws BitcoindException, CommunicationException;
}
