package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.User;
import me.exrates.model.dto.migrate.ExtendedUserDto;
import me.exrates.model.dto.migrate.UserBalanceDto;
import me.exrates.model.userOperation.UserOperationAuthorityOption;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.MigrateService;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.OrderService;
import me.exrates.service.RefillService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Transactional
@Service
public class MigrateServiceImpl implements MigrateService {

    private final UserService userService;
    private final UserOperationService userOperationService;
    private final OrderService orderService;
    private final RefillService refillService;
    private final WithdrawService withdrawService;
    private final TransferService transferService;
    private final OpenApiTokenService openApiTokenService;
    private final G2faService g2faService;
    private final WalletService walletService;

    @Autowired
    public MigrateServiceImpl(UserService userService,
                              UserOperationService userOperationService,
                              OrderService orderService,
                              RefillService refillService,
                              WithdrawService withdrawService,
                              TransferService transferService,
                              OpenApiTokenService openApiTokenService,
                              G2faService g2faService,
                              WalletService walletService) {
        this.userService = userService;
        this.userOperationService = userOperationService;
        this.orderService = orderService;
        this.refillService = refillService;
        this.withdrawService = withdrawService;
        this.transferService = transferService;
        this.openApiTokenService = openApiTokenService;
        this.g2faService = g2faService;
        this.walletService = walletService;
    }

    @Override
    public ExtendedUserDto migrate(String email) {
        User user = userService.findByEmail(email);

        //block user
        userService.blockUserByRequest(user.getId());

        //cancel all opened orders
        boolean canceled = orderService.cancelAllOpenOrders();
        if (canceled) {
            log.info("All opened orders have been canceled");
        }

        //cancel all refill requests
        refillService.getRefillRequestIdsToRevoke(user.getId()).forEach(refillService::revokeRefillRequest);

        //cancel all withdraw requests
        withdrawService.getWithdrawRequestIdsToRevoke(user.getId()).forEach(withdrawService::revokeWithdrawalRequest);

        //cancel all transfer requests
        transferService.getTransferRequestIdsToRevoke(user.getId()).forEach(transferService::revokeTransferRequest);

        //block all user operations
        List<UserOperationAuthorityOption> options = new ArrayList<>();
        options.add(UserOperationAuthorityOption.builder()
                .userOperationAuthority(UserOperationAuthority.INPUT)
                .enabled(false)
                .build());
        options.add(UserOperationAuthorityOption.builder()
                .userOperationAuthority(UserOperationAuthority.OUTPUT)
                .enabled(false)
                .build());
        options.add(UserOperationAuthorityOption.builder()
                .userOperationAuthority(UserOperationAuthority.TRANSFER)
                .enabled(false)
                .build());
        options.add(UserOperationAuthorityOption.builder()
                .userOperationAuthority(UserOperationAuthority.TRADING)
                .enabled(false)
                .build());
        options.add(UserOperationAuthorityOption.builder()
                .userOperationAuthority(UserOperationAuthority.TRADING_RESTRICTION)
                .enabled(false)
                .build());
        userOperationService.updateUserOperationAuthority(options, user.getId());

        //deactivate all API keys
        openApiTokenService.deleteAllTokensByUserId(user.getId());

        //migrate user
        userService.migrateUserByRequest(user.getId());

        //get user info
        String encodedPassword = userService.getPassword(user.getId());

        boolean enabled2fa = g2faService.isGoogleAuthenticatorEnable(user.getEmail());
        String secretCode = g2faService.getSecretCode(user.getId());

        //get user balances
        List<UserBalanceDto> balances = walletService.getBalancesForUser(user.getEmail()).stream()
                .filter(balance -> balance.getActiveBalance().compareTo(BigDecimal.ZERO) != 0)
                .map(balance -> UserBalanceDto.builder()
                        .currencyName(balance.getCurrencyName())
                        .balance(balance.getActiveBalance())
                        .build())
                .collect(Collectors.toList());

        return ExtendedUserDto.builder()
                .email(email)
                .encodedPassword(encodedPassword)
                .enabled2fa(enabled2fa)
                .secret2fa(secretCode)
                .balances(balances)
                .build();
    }
}