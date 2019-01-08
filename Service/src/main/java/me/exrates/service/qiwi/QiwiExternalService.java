package me.exrates.service.qiwi;

import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;

import java.util.List;

public interface QiwiExternalService {
    String generateUniqMemo(int userId);

    List<QiwiResponseTransaction> getLastTransactions();

}
