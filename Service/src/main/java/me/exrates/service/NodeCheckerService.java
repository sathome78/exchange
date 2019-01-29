package me.exrates.service;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;

import java.util.List;

public interface NodeCheckerService {
    Long getBTCBlocksCount(String ticker) throws BitcoindException, CommunicationException;

    List<String> listOfRefillableServicesNames();

    Long getLastBlockTime(String ticker) throws BitcoindException, CommunicationException;
}
