package me.exrates.ngcontroller.service.impl;

import me.exrates.dao.WalletDao;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

@Service
public class NgWalletServiceImpl implements NgWalletService {

    private final WalletDao walletDao;

    @Autowired
    public NgWalletServiceImpl(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale, CurrencyType currencyType) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(toList());
        List<MerchantProcessType> processTypes = currencyType == null ? MerchantProcessType.getAllCoinsTypes() : currencyType.getMerchantProcessTypeList();
        return walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale, processTypes);
    }
}