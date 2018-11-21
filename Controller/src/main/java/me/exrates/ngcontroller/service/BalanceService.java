package me.exrates.ngcontroller.service;

import me.exrates.ngcontroller.model.UserBalancesDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BalanceService {
    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);
}
