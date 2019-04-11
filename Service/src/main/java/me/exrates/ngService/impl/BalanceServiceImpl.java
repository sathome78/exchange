package me.exrates.ngService.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.BalancesShortDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngModel.UserBalancesDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.ngDao.BalanceDao;
import me.exrates.ngService.BalanceService;
import me.exrates.ngService.NgWalletService;
import me.exrates.ngService.RefillPendingRequestService;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.MerchantNotFoundException;
import me.exrates.service.exception.MerchantServiceBeanNameNotDefinedException;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class BalanceServiceImpl implements BalanceService {

    private static final Logger log = LoggerFactory.getLogger(BalanceServiceImpl.class);

    private final BalanceDao balanceDao;
    private final InputOutputService inputOutputService;
    private final RefillPendingRequestService refillPendingRequestService;
    private final NgWalletService ngWalletService;
    private final UserService userService;
    private final ExchangeRatesHolder exchangeRatesHolder;
    private final MerchantServiceContext merchantServiceContext;
    private final UserDao userDao;
    private final WalletDao walletDao;
    private final CurrencyDao currencyDao;

    @Autowired
    public BalanceServiceImpl(BalanceDao balanceDao,
                              InputOutputService inputOutputService,
                              NgWalletService ngWalletService,
                              RefillPendingRequestService refillPendingRequestService,
                              UserService userService,
                              ExchangeRatesHolder exchangeRatesHolder,
                              MerchantServiceContext merchantServiceContext,
                              UserDao userDao, WalletDao walletDao, CurrencyDao currencyDao) {
        this.balanceDao = balanceDao;
        this.inputOutputService = inputOutputService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.ngWalletService = ngWalletService;
        this.userService = userService;
        this.exchangeRatesHolder = exchangeRatesHolder;
        this.merchantServiceContext = merchantServiceContext;
        this.userDao = userDao;
        this.walletDao = walletDao;
        this.currencyDao = currencyDao;
    }

    private static Predicate<MyWalletsDetailedDto> excludeRub(CurrencyType currencyType) {
        if (currencyType != null && currencyType == CurrencyType.CRYPTO) {
            return p -> !p.getCurrencyName().equalsIgnoreCase("rub");
        } else {
            return x -> true;
        }
    }

    @Override
    public List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId) {
        return balanceDao.getUserBalances(tikerName, sortByCreated, page, limit, userId);
    }

    @Override
    public PagedResult<MyWalletsDetailedDto> getWalletsDetails(BalanceFilterDataDto filterDataDto) {
        final String email = filterDataDto.getEmail();
        final Integer currencyId = filterDataDto.getCurrencyId();
        final String currencyName = filterDataDto.getCurrencyName();
        final CurrencyType currencyType = filterDataDto.getCurrencyType();
        final Boolean excludeZero = filterDataDto.getExcludeZero();
        final Integer offset = filterDataDto.getOffset();
        final Integer limit = filterDataDto.getLimit();

        Stream<MyWalletsDetailedDto> detailedDtoStream = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH, currencyType)
                .stream()
                .filter(excludeRub(currencyType));

        if (excludeZero) {
            detailedDtoStream = detailedDtoStream
                    .filter(wallet -> new BigDecimal(wallet.getActiveBalance()).compareTo(BigDecimal.ZERO) > 0
                            || new BigDecimal(wallet.getReservedByOrders()).compareTo(BigDecimal.ZERO) > 0
                            || new BigDecimal(wallet.getReservedByMerchant()).compareTo(BigDecimal.ZERO) > 0
                            || new BigDecimal(wallet.getOnConfirmation()).compareTo(BigDecimal.ZERO) > 0);
        }
        if (currencyId > 0) {
            detailedDtoStream = detailedDtoStream
                    .filter(wallet -> Objects.equals(currencyId, wallet.getCurrencyId()));
        } else if (currencyId == 0 && StringUtils.isNotBlank(currencyName)) {
            detailedDtoStream = detailedDtoStream
                    .filter(wallet -> wallet.getCurrencyName().contains(currencyName)
                            || wallet.getCurrencyDescription().contains(currencyName));
        }
        List<MyWalletsDetailedDto> balanceDetails = detailedDtoStream.collect(toList());

        PagedResult<MyWalletsDetailedDto> detailsPage = getSafeSubList(balanceDetails, offset, limit);

        setBtcUsdAmount(detailsPage.getItems());

        return detailsPage;
    }

    @Override
    public Optional<MyWalletsDetailedDto> findOne(String email, Integer currencyId) {
        List<MyWalletsDetailedDto> wallets = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH, null);
        return wallets
                .stream()
                .filter(w -> w.getCurrencyId().equals(currencyId))
                .findFirst();
    }

    private void setBtcUsdAmount(List<MyWalletsDetailedDto> walletsDetails) {
        Map<String, BigDecimal> btcRateMapped = exchangeRatesHolder.getRatesForMarket(TradeMarket.BTC);
        Map<String, BigDecimal> usdRateMapped = exchangeRatesHolder.getRatesForMarket(TradeMarket.USD);

        BigDecimal btcUsdRate = exchangeRatesHolder.getBtcUsdRate();
        walletsDetails.forEach(p -> {
            BigDecimal sumBalances = new BigDecimal(p.getActiveBalance()).add(new BigDecimal(p.getReservedBalance())).setScale(8, RoundingMode.HALF_DOWN);
            BigDecimal usdRate = usdRateMapped.getOrDefault(p.getCurrencyName(), BigDecimal.ZERO);
            BigDecimal btcRate = btcRateMapped.getOrDefault(p.getCurrencyName(), BigDecimal.ZERO);
            BalancesShortDto dto = count(sumBalances, p.getCurrencyName(), btcRate, usdRate, btcUsdRate);

            p.setBtcAmount(dto.getBalanceBtc().setScale(8, RoundingMode.HALF_DOWN).toPlainString());
            p.setUsdAmount(dto.getBalanceUsd().setScale(2, RoundingMode.HALF_DOWN).toPlainString());
        });
    }

    @Override
    public PagedResult<RefillPendingRequestDto> getPendingRequests(int offset, int limit, String currencyName, String email) {
        List<RefillPendingRequestDto> requests =
                refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email))
                        .stream()
                        .filter(o -> o.getDate() != null && containsCurrencyName(o, currencyName))
                        .sorted(((o1, o2) -> {
                            Date dateOne = getDateFromString(o1.getDate());
                            Date dateTwo = getDateFromString(o2.getDate());
                            return dateTwo.compareTo(dateOne);
                        }))
                        .collect(Collectors.toList());
        return getSafeSubList(requests, offset, limit);
    }

    private boolean containsCurrencyName(RefillPendingRequestDto dto, String currencyName) {
        if (StringUtils.isNotBlank(currencyName)) {
            return dto.getCurrency().toUpperCase().contains(currencyName.toUpperCase());
        }
        return true;
    }

    @Override
    public PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(String userEmail, Integer currencyId, String currencyName,
                                                                          LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                          Integer limit, Integer offset, Locale locale) {
        Integer recordsCount = inputOutputService.getUserInputOutputHistoryCount(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                locale);

        List<MyInputOutputHistoryDto> historyDtoList = Collections.emptyList();
        if (recordsCount > 0) {
            historyDtoList = getMyInputOutputHistoryDtos(
                    userEmail,
                    currencyId,
                    currencyName,
                    dateTimeFrom,
                    dateTimeTo,
                    limit,
                    offset,
                    locale);

            setAcceptedToDefineUserTransferOperation(historyDtoList, userEmail);
        }
        PagedResult<MyInputOutputHistoryDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(recordsCount);
        pagedResult.setItems(historyDtoList);
        return pagedResult;
    }

    private void setAcceptedToDefineUserTransferOperation(List<MyInputOutputHistoryDto> historyDtoList, String email) {
        final int principalUserId = userService.getIdByEmail(email);

        historyDtoList.forEach(inout -> {
            if (Objects.equals(TransactionSourceType.USER_TRANSFER, inout.getSourceType())) {
                final Integer userId = inout.getUserId();

                inout.setAccepted(principalUserId != userId);
            }
        });
    }

    @Override
    public List<MyInputOutputHistoryDto> getUserInputOutputHistoryExcel(String userEmail, Integer currencyId, String currencyName,
                                                                        LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                        Integer limit, Integer offset, Locale locale) {
        return getMyInputOutputHistoryDtos(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                locale);
    }

    private List<MyInputOutputHistoryDto> getMyInputOutputHistoryDtos(String userEmail, Integer currencyId, String currencyName,
                                                                      LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                      Integer limit, Integer offset, Locale locale) {
        List<MyInputOutputHistoryDto> history = inputOutputService.getUserInputOutputHistory(
                userEmail,
                currencyId,
                currencyName,
                dateTimeFrom,
                dateTimeTo,
                limit,
                offset,
                locale);

        history.forEach(dto -> {
            IRefillable merchant;
            int minConfirmations = 0;
            // todo to solve later
            if (dto.getCurrencyName().equalsIgnoreCase("USD") || dto.getCurrencyName().equalsIgnoreCase("EUR")) {
                dto.setMarket("Fiat");
            } else {
                try {
                    merchant = (IRefillable) merchantServiceContext.getMerchantServiceByName(dto.getMerchantName());
                    minConfirmations = Optional.ofNullable(merchant.minConfirmationsRefill()).orElse(0);
                } catch (ClassCastException ex) {
                    log.warn("Failed to cast IRefillable ", ex);
                } catch (MerchantNotFoundException | MerchantServiceBeanNameNotDefinedException ex) {
                    log.warn("Merchant: {} did not find: ", dto.getMerchantName(), ex);
                }
                dto.setMarket("BTC");
            }
            dto.setNeededConfirmations(minConfirmations);
        });
        return history;
    }

    @Override
    public Map<String, BigDecimal> getBalancesSumInBtcAndUsd() {
        String email = getPrincipalEmail();

        List<MyWalletsDetailedDto> cryptoWallet = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH, CurrencyType.CRYPTO);
        List<MyWalletsDetailedDto> fiatWallet = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH, CurrencyType.FIAT);

        List<MyWalletsDetailedDto> commonWallets = ListUtils.union(cryptoWallet, fiatWallet);

        BigDecimal btcBalances = BigDecimal.ZERO;
        BigDecimal usdBalances = BigDecimal.ZERO;
        BigDecimal btcUsdRate = exchangeRatesHolder.getBtcUsdRate();
        for (MyWalletsDetailedDto p : commonWallets) {

            BigDecimal activeBalance = new BigDecimal(p.getActiveBalance());
            BigDecimal orderBalance = new BigDecimal(p.getReservedByOrders());
            BigDecimal sumBalances = BigDecimalProcessing.doAction(activeBalance, orderBalance, ActionType.ADD);

            if (sumBalances.compareTo(BigDecimal.ZERO) == 0) continue;

            switch (p.getCurrencyName()) {
                case "USD":
                    usdBalances = usdBalances.add(sumBalances);
                    BigDecimal btcValue = BigDecimalProcessing.doAction(sumBalances, btcUsdRate, ActionType.DEVIDE);
                    btcBalances = btcBalances.add(btcValue);
                    break;
                case "BTC":
                    btcBalances = btcBalances.add(sumBalances);
                    usdBalances = usdBalances.add(sumBalances.multiply(btcUsdRate));
                    break;
                default:
                    BalancesShortDto shortDto = getBalanceForOtherCurrency(p.getCurrencyName(), sumBalances, btcUsdRate);
                    btcBalances = btcBalances.add(shortDto.getBalanceBtc());
                    usdBalances = usdBalances.add(shortDto.getBalanceUsd());
            }
        }
        Map<String, BigDecimal> balancesMap = new HashMap<>();
        balancesMap.put("BTC", btcBalances.setScale(8, RoundingMode.HALF_DOWN));
        balancesMap.put("USD", usdBalances.setScale(2, RoundingMode.HALF_DOWN));
        return balancesMap;
    }

    @Override
    public Map<String, String> getActiveBalanceByCurrencyNamesAndEmail(String email, Set<String> currencyNames) {
        User user = userDao.findByEmail(email);
        return walletDao.findUserCurrencyBalances(user, currencyNames);
    }

    private BalancesShortDto getBalanceForOtherCurrency(String currencyName, BigDecimal sumBalances, BigDecimal btcUsdRate) {

        BalancesShortDto result = BalancesShortDto.zeroBalances();

        Optional<ExOrderStatisticsShortByPairsDto> optionalBtc =
                exchangeRatesHolder.getAllRates()
                        .stream()
                        .filter(o -> o.getCurrencyPairName().equalsIgnoreCase(currencyName + "/BTC"))
                        .findFirst();

        Optional<ExOrderStatisticsShortByPairsDto> optionalUsd =
                exchangeRatesHolder.getAllRates()
                        .stream()
                        .filter(o -> o.getCurrencyPairName().equalsIgnoreCase(currencyName + "/USD"))
                        .findFirst();

        if (optionalBtc.isPresent() && optionalUsd.isPresent()) {
            BigDecimal btcRate = new BigDecimal(optionalBtc.get().getLastOrderRate());
            BigDecimal usdRate = new BigDecimal(optionalUsd.get().getLastOrderRate());

            if (btcRate.compareTo(BigDecimal.ZERO) > 0) result.setBalanceBtc(sumBalances.multiply(btcRate));
            if (usdRate.compareTo(BigDecimal.ZERO) > 0) result.setBalanceUsd(sumBalances.multiply(usdRate));

            return result;
        }

//        if (optionalBtc.isPresent()) {
//            BigDecimal btcRate = optionalBtc.get().getLastOrderRate();
//            if (btcRate.compareTo(BigDecimal.ZERO) > 0) {
//                BigDecimal btcValue = sumBalances.multiply(btcRate);
//                result.setBalanceBtc(btcValue);
//                BigDecimal usdValue = btcValue.multiply(btcUsdRate);
//                result.setBalanceUsd(usdValue);
//            }
//        }

        return result;
    }

    private BalancesShortDto count(BigDecimal sumBalances, String currencyName, BigDecimal btcRate, BigDecimal usdRate, BigDecimal btcUsdRate) {
        BalancesShortDto balancesShortDto = BalancesShortDto.zeroBalances();
        if (sumBalances.compareTo(BigDecimal.ZERO) > 0) {
            switch (currencyName) {
                case "BTC":
                    balancesShortDto.setBalanceBtc(sumBalances);
                    balancesShortDto.setBalanceUsd(sumBalances.multiply(btcUsdRate));
                    break;
                case "USD":
                    balancesShortDto.setBalanceBtc(sumBalances.divide(btcUsdRate, RoundingMode.HALF_UP).setScale(8, RoundingMode.HALF_UP));
                    balancesShortDto.setBalanceUsd(sumBalances);
                    break;
                default:
                    if (usdRate.compareTo(BigDecimal.ZERO) <= 0) {
                        usdRate = btcRate.multiply(btcUsdRate);
                    }
                    balancesShortDto.setBalanceBtc(btcRate.multiply(sumBalances));
                    balancesShortDto.setBalanceUsd(usdRate.multiply(sumBalances));
                    break;
            }
        }
        return balancesShortDto;
    }

    private <T> PagedResult<T> getSafeSubList(List<T> items, int offset, int limit) {
        if (items.isEmpty() || offset >= items.size()) {
            return new PagedResult<>(0, Collections.emptyList());
        }
        if ((offset + limit) > items.size()) {
            return new PagedResult<>(items.size(), items.subList(offset, items.size()));
        }
        return new PagedResult<>(items.size(), items.subList(offset, offset + limit));
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Date getDateFromString(String input) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date parsedDate = null;
        try {
            parsedDate = formatter.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

}
