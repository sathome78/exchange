package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.freecoins.GiveawayClaimRequest;
import me.exrates.model.dto.freecoins.GiveawayResultDto;
import me.exrates.model.dto.freecoins.GiveawayStatus;
import me.exrates.model.dto.freecoins.ReceiveResultDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.freecoins.FreecoinsService;
import me.exrates.service.freecoins.FreecoinsSettingsService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class NgFreecoinsController {

    private final static String PUBLIC = "/api/public/v2/free-coins";
    private final static String PRIVATE = "/api/private/v2/free-coins";

    private final CurrencyService currencyService;
    private final WalletService walletService;
    private final G2faService g2faService;
    private final UserService userService;
    private final SecureService secureService;
    private final FreecoinsService freeCoinsService;
    private final FreecoinsSettingsService freecoinsSettingsService;
    private final StompMessenger stompMessenger;

    @Autowired
    public NgFreecoinsController(CurrencyService currencyService,
                                 WalletService walletService,
                                 G2faService g2faService,
                                 UserService userService,
                                 SecureService secureService,
                                 FreecoinsService freeCoinsService,
                                 FreecoinsSettingsService freecoinsSettingsService,
                                 StompMessenger stompMessenger) {
        this.currencyService = currencyService;
        this.walletService = walletService;
        this.g2faService = g2faService;
        this.userService = userService;
        this.secureService = secureService;
        this.freeCoinsService = freeCoinsService;
        this.freecoinsSettingsService = freecoinsSettingsService;
        this.stompMessenger = stompMessenger;
    }

    @PreAuthorize("hasAuthority('VIP_USER')")
    @PostMapping(value = PRIVATE + "/giveaway", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GiveawayResultDto> processGiveaway(@Valid @RequestBody GiveawayClaimRequest request,
                                                             Errors result) {
        if (result.hasErrors()) {
            final String errors = result.getAllErrors().stream()
                    .map(ObjectError::getObjectName)
                    .collect(Collectors.joining(","));

            final String message = String.format("Request data not valid: %s", errors);
            throw new NgResponseException(ErrorApiTitles.REQUEST_DATA_NOT_VALID, message);
        }
        final String creatorEmail = getUserEmailFromSecurityContext();

        check2faAuthorization(creatorEmail, request.getPin());

        try {
            GiveawayResultDto giveawayResultDto = freeCoinsService.processGiveaway(request.getCurrencyName(), request.getAmount(),
                    request.getPartialAmount(), request.isSingle(), request.getTimeRange(), creatorEmail);

            String text = giveawayResultDto.getStatus() == GiveawayStatus.CREATED
                    ? "Free coins giveaway process was created"
                    : "Free coins giveaway process was failed";
            sendPersonalMessageToUser(creatorEmail, text);

            return ResponseEntity.status(HttpStatus.CREATED).body(giveawayResultDto);
        } catch (Exception ex) {
            sendPersonalMessageToUser(creatorEmail, "Free coins giveaway process was failed");

            final String message = "Free coins giveaway process was failed";
            throw new NgResponseException(ErrorApiTitles.FREE_COINS_GIVE_AWAY_PROCESS_FAILED, message);
        }
    }

    @PreAuthorize("hasAnyAuthority('VIP_USER','ADMINISTRATOR')")
    @PostMapping(PRIVATE + "/giveaway/revoke")
    public ResponseEntity processRevokeGiveaway(@RequestParam("giveaway_id") int giveawayId,
                                                @RequestParam(value = "revoke_to_user", defaultValue = "false") boolean revokeToUser,
                                                @RequestParam String pin) {
        final String creatorEmail = getUserEmailFromSecurityContext();

        check2faAuthorization(creatorEmail, pin);

        try {
            boolean revoked = freeCoinsService.processRevokeGiveaway(giveawayId, revokeToUser, creatorEmail);

            if (revokeToUser) {
                sendPersonalMessageToUser(creatorEmail, "Free coins giveaway revoke process was ended, funds was returned");
            }

            return ResponseEntity.ok(revoked);
        } catch (Exception ex) {
            if (revokeToUser) {
                sendPersonalMessageToUser(creatorEmail, "Free coins giveaway revoke process was failed");
            }

            final String message = "Free coins giveaway revoke process was failed";
            throw new NgResponseException(ErrorApiTitles.FREE_COINS_GIVE_AWAY_PROCESS_FAILED, message);
        }
    }

    @GetMapping(PUBLIC + "/giveaway/all")
    public ResponseEntity<List<GiveawayResultDto>> getAllGiveaways() {
        return ResponseEntity.ok(freeCoinsService.getAllGiveaways());
    }

    @PostMapping(PRIVATE + "/receive")
    public ResponseEntity<ReceiveResultDto> processReceive(@RequestParam("giveaway_id") int giveawayId) {
        final String receiverEmail = getUserEmailFromSecurityContext();

        try {
            ReceiveResultDto receiveResultDto = freeCoinsService.processReceive(giveawayId, receiverEmail);

            sendPersonalMessageToUser(receiverEmail, "Free coins receive process was created");

            return ResponseEntity.ok(receiveResultDto);
        } catch (Exception ex) {
            sendPersonalMessageToUser(receiverEmail, "Free coins receive process was failed");

            final String message = "Free coins receive process was failed";
            throw new NgResponseException(ErrorApiTitles.FREE_COINS_RECEIVE_PROCESS_FAILED, message);
        }
    }

    @GetMapping(PRIVATE + "/receive/all")
    public ResponseEntity<Map<Integer, ReceiveResultDto>> getAllReceives() {
        List<ReceiveResultDto> resultList = freeCoinsService.getAllReceives(getUserEmailFromSecurityContext());
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        final Map<Integer, ReceiveResultDto> resultMap = resultList.stream()
                .collect(Collectors.toMap(
                        ReceiveResultDto::getGiveawayId,
                        Function.identity()));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping(PRIVATE + "/currencies")
    public ResponseEntity<Map<String, Currency>> getCurrencies() {
        List<Currency> resultList = currencyService.getAllActiveCurrencies();
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        final Map<String, Currency> resultMap = resultList.stream()
                .collect(Collectors.toMap(
                        Currency::getName,
                        Function.identity(),
                        (v1, v2) -> v1));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping(PRIVATE + "/balances")
    public ResponseEntity<Map<String, BigDecimal>> getBalances() {
        List<MyWalletsStatisticsDto> resultList = walletService.getAllWalletsForUserReduced(null, getUserEmailFromSecurityContext(), CurrencyPairType.MAIN);
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        final Map<String, BigDecimal> resultMap = resultList.stream()
                .collect(Collectors.toMap(
                        MyWalletsStatisticsDto::getCurrencyName,
                        dto -> new BigDecimal(dto.getActiveBalance()),
                        (v1, v2) -> v1));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping(PRIVATE + "/settings")
    public ResponseEntity<Map<String, FreecoinsSettingsDto>> getSettings() {
        List<FreecoinsSettingsDto> resultList = freecoinsSettingsService.getAll();
        if (CollectionUtils.isEmpty(resultList)) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        final Map<String, FreecoinsSettingsDto> resultMap = resultList.stream()
                .collect(Collectors.toMap(
                        FreecoinsSettingsDto::getCurrencyName,
                        Function.identity(),
                        (v1, v2) -> v1));

        return ResponseEntity.ok(resultMap);
    }

    @PostMapping(PRIVATE + "/settings/update")
    public ResponseEntity updateSettings(@RequestParam("currency_id") int currencyId,
                                         @RequestParam("min_amount") BigDecimal minAmount,
                                         @RequestParam("min_partial_amount") BigDecimal minPartialAmount) {
        return ResponseEntity.ok(freecoinsSettingsService.set(currencyId, minAmount, minPartialAmount));
    }

    @PostMapping(PRIVATE + "/pin")
    public ResponseEntity sendPinCode(HttpServletRequest request) {
        try {
            User user = userService.findByEmail(getUserEmailFromSecurityContext());
            boolean googleAuthenticatorEnabled = g2faService.isGoogleAuthenticatorEnable(user.getId());
            if (!googleAuthenticatorEnabled) {
                secureService.sendFreecoinsPincode(user, request);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            final String message = "Failed to send pin code on user email";
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL, message);
        }
    }

    private void check2faAuthorization(String email, String pin) {
        final int userId = userService.getIdByEmail(email);

        boolean googleAuthenticatorEnabled = g2faService.isGoogleAuthenticatorEnable(userId);
        if (googleAuthenticatorEnabled) {
            if (!g2faService.checkGoogle2faVerifyCode(pin, userId)) {
                final String message = String.format("Invalid google 2fa authorization code from user %s", email);
                throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
            }
        } else {
            if (!userService.checkPin(email, pin, NotificationMessageEventEnum.FREE_COINS)) {
                final String message = String.format("Invalid google 2fa authorization code from user %s", email);
                throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
            }
        }
    }

    private void sendPersonalMessageToUser(String creatorEmail, String text) {
        UserNotificationMessage notificationMessage = new UserNotificationMessage();
        notificationMessage.setNotificationType(UserNotificationType.INFORMATION);
        notificationMessage.setSourceTypeEnum(WsSourceTypeEnum.FREE_COINS);
        notificationMessage.setText(text);

        stompMessenger.sendPersonalMessageToUser(creatorEmail, notificationMessage);
    }

    private String getUserEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            final String message = "User not authorized";
            throw new NgResponseException(ErrorApiTitles.NOT_AUTHORIZED, message);
        }
        return authentication.getName();
    }
}