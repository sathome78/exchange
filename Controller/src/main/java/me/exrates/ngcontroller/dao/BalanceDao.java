package me.exrates.ngcontroller.dao;

import me.exrates.ngcontroller.model.UserBalancesDto;

import java.util.List;

public interface BalanceDao {
    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);
}
