package me.exrates.ngService.impl;

import me.exrates.dao.WalletDao;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.ngService.NgWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
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
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet()
                .stream()
                .map(InvoiceStatus::getCode)
                .collect(Collectors.toList());
        List<MerchantProcessType> processTypes = isNull(currencyType)
                ? MerchantProcessType.getAllCoinsTypes()
                : currencyType.getMerchantProcessTypeList();

        return walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale, processTypes);
    }
}