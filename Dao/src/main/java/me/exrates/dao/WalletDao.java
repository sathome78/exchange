package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.IEOClaim;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.ExternalReservedWalletAddressDto;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.OrderDetailDto;
import me.exrates.model.dto.UserGroupBalanceDto;
import me.exrates.model.dto.UserRoleBalanceDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.WalletFormattedDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.WalletOperationData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface WalletDao {

    BigDecimal getWalletABalance(int walletId);

    BigDecimal getWalletRBalance(int walletId);

    int getWalletId(int userId, int currencyId);

    BigDecimal getActiveBalanceAndBlockByWalletId(Integer walletId);

    int createNewWallet(Wallet wallet);

    int getUserIdFromWallet(int walletId);

    List<Wallet> findAllForNotHiddenCurByUser(int userId);

    List<MyWalletsStatisticsDto> getAllWalletsForUserAndCurrenciesReduced(String email, Set<Integer> currencyIds);

    List<WalletBalanceDto> getBalancesForUser(String userEmail);

    MyWalletsStatisticsApiDto getWalletShortStatistics(int walletId);

    List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId, List<Integer> withdrawEndStatusIds,
                                                               List<Integer> withdrawSuccessStatusIds,
                                                               List<Integer> refillSuccessStatusIds);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, List<Integer> withdrawStatusIds, Locale locale);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> withdrawStatusIds, Locale locale, List<MerchantProcessType> processType);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> withdrawStatusIds, Locale locale);

    List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale);

    List<Wallet> findAllByUser(int userId);

    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email);

    Wallet findByUserAndCurrency(int userId, int currencyId);

    Wallet findByUserAndCurrency(int userId, String currencyName);

    Wallet findById(Integer walletId);

    Wallet findByIdAndLock(Integer walletId);

    Wallet createWallet(User user, int currencyId);

    Wallet createWallet(int userId, int currencyId);

    boolean update(Wallet wallet);

    /**
     * Returns dto with:
     * - IDs the currency
     * - IDs the wallets -
     * - balances the wallets of participants the order: user-creator, user-acceptor, company
     * - status the order
     * and blocks the order and the set wallets within current transaction
     *
     * @param orderId
     * @param userAcceptorId
     * @return dto with data
     */
    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId);

    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType);

    WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, CurrencyPair currencyPair);

    BigDecimal getAvailableAmountInBtcLocked(int userId, int currencyId);

    boolean reserveUserBtcForIeo(int userId, BigDecimal amountInBtc, int currencyId);

    boolean rollbackUserBtcForIeo(int userId, BigDecimal amountInBtc, int currencyId);

    List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId);

    void addToWalletBalance(Integer walletId, BigDecimal addedAmountActive, BigDecimal addedAmountReserved);

    List<UserWalletSummaryDto> getUsersWalletsSummaryNew(Integer requesterUserId, List<Integer> roleIds);

    boolean isUserAllowedToManuallyChangeWalletBalance(int adminId, int walletHolderUserId);

    List<UserGroupBalanceDto> getWalletBalancesSummaryByGroups();

    List<UserRoleBalanceDto> getWalletBalancesSummaryByRoles(List<Integer> roleIdsList);

    int getWalletIdAndBlock(Integer userId, Integer currencyId);

    List<ExternalWalletBalancesDto> getExternalMainWalletBalances();

    void updateExternalMainWalletBalances(ExternalWalletBalancesDto externalWalletBalancesDto);

    List<InternalWalletBalancesDto> getInternalWalletBalances();

    void updateInternalWalletBalances(InternalWalletBalancesDto internalWalletBalancesDto);

    void createReservedWalletAddress(int currencyId);

    void deleteReservedWalletAddress(int id, int currencyId);

    void updateReservedWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto);

    boolean updateSignOfCertaintyForCurrency(int currencyId, boolean signOfCertainty);

    List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId);

    List<InternalWalletBalancesDto> getWalletBalances();

    BigDecimal retrieveSummaryUSD();

    BigDecimal retrieveSummaryBTC();

    void updateExternalReservedWalletBalances(int currencyId, String walletAddress, BigDecimal balance, LocalDateTime lastReservedBalanceUpdate);

    Map<String, String> findUserCurrencyBalances(User user, Collection<String> currencyNames);

    BigDecimal findUserCurrencyBalance(IEOClaim ieoClaim);

    BigDecimal getActiveBalanceByUserAndCurrency(String email, Integer currencyId);
}
