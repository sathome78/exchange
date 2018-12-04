package me.exrates.ngcontroller.service;

import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.CurrencyType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public interface NgWalletService {

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale, CurrencyType currencyType);

}
