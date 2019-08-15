package me.exrates.dao;

import me.exrates.model.merchants.AdGroupTx;

import java.util.List;

public interface AdGroupDao {

    AdGroupTx save(AdGroupTx adGroupTx);

    List<AdGroupTx> findByStatus(String status);

    boolean deleteTxById(int id);

    boolean deleteTxByTx(String tx);
}
