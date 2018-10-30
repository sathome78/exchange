package me.exrates.dao;

import me.exrates.model.dto.EthAccCredentials;

import java.util.List;

public interface EthAccountDao {
    List<EthAccCredentials> loadAll(List<Boolean> isActiveStatuses);

    void setStatus(int id, boolean isActive);
}
