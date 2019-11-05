package me.exrates.service.qiwi;

import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;

import java.util.List;
import java.util.Map;

public interface QiwiExternalService {

    Map<String, String> getResponseParams(int userId);

    List<QiwiResponseTransaction> getLastTransactions();
}