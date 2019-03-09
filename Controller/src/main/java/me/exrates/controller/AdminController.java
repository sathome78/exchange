package me.exrates.controller;

import com.google.common.base.Preconditions;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.exception.*;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.Currency;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.form.AuthorityOptionsForm;
import me.exrates.model.form.UserOperationAuthorityOptionsForm;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.*;
import me.exrates.service.aidos.AdkService;
import me.exrates.service.aidos.AdkServiceImpl;
import me.exrates.service.exception.*;
import me.exrates.service.B2XTransferToReserveAccount;
import me.exrates.service.exception.process.NotCreatableOrderException;
import me.exrates.service.exception.process.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.process.OrderAcceptionException;
import me.exrates.service.exception.process.OrderCancellingException;
import me.exrates.service.exception.process.OrderCreationException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.Subscribable;
import me.exrates.service.omni.OmniService;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.BigDecimalConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static me.exrates.model.enums.GroupUserRoleEnum.*;
import static me.exrates.model.enums.GroupUserRoleEnum.ADMINS;
import static me.exrates.model.enums.GroupUserRoleEnum.BOT;
import static me.exrates.model.enums.GroupUserRoleEnum.USERS;
import static me.exrates.model.enums.UserCommentTopicEnum.GENERAL;
import static me.exrates.model.enums.UserRole.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.*;
import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Log4j2
@Controller
public class AdminController {

    private static final Logger LOG = LogManager.getLogger(AdminController.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserSecureService userSecureService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOperationService userOperationService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private WalletService walletService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserFilesService userFilesService;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private Map<String, BitcoinService> bitcoinLikeServices;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PhraseTemplateService phraseTemplateService;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserTransferService userTransferService;
    @Autowired
    WithdrawService withdrawService;
    @Autowired
    StopOrderService stopOrderService;
    @Autowired
    RefillService refillService;
    @Autowired
    BotService botService;
    @Autowired
    private MerchantServiceContext serviceContext;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private EDCServiceNode edcServiceNode;
    @Autowired
    private UsersAlertsService alertsService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private AdkService adkService;
    @Autowired
    private OmniService omniService;

    @Autowired
    private B2XTransferToReserveAccount b2XTransferToReserveAccount;

    @Autowired
    @Qualifier("ExratesSessionRegistry")
    private SessionRegistry sessionRegistry;

    @Autowired
    private BigDecimalConverter converter;

    public static String adminAnyAuthority;
    public static String nonAdminAnyAuthority;
    public static String traderAuthority;
    public static String botAuthority;


    @PostConstruct
    private void init() {
        traderAuthority = retrieveHasAuthorityStringByBusinessRole(BusinessUserRoleEnum.TRADER);
        adminAnyAuthority = retrieveHasAuthorityStringByBusinessRole(BusinessUserRoleEnum.ADMIN);
        nonAdminAnyAuthority = "!" + adminAnyAuthority;
        botAuthority = retrieveHasAuthorityStringByBusinessRole(BusinessUserRoleEnum.BOT);
    }

    private String retrieveHasAuthorityStringByBusinessRole(BusinessUserRoleEnum businessUserRole) {
        List<UserRole> roles = userRoleService.getRealUserRoleByBusinessRoleList(businessUserRole);
        return roles
                .stream()
                .map(e -> "'" + e.name() + "'")
                .collect(Collectors.joining(",", "hasAnyAuthority(", ")"));
    }

    @RequestMapping(value = {"/2a8fy7b07dxe44", "/2a8fy7b07dxe44/users"})
    public ModelAndView admin() {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL);
        model.addObject("currencyPairList", currencyPairList);
        model.addObject("post_url", "/2a8fy7b07dxe44/set2fa");
        model.setViewName("admin/admin");
        return model;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/administrators", method = GET)
    public String administrators() {
        return "admin/administrators";
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/referral", method = GET)
    public ModelAndView referral() {
        ModelAndView model = new ModelAndView();
        model.addObject("referralLevels", referralService.findAllReferralLevels());
        model.addObject("commonRefRoot", userService.getCommonReferralRoot());
        model.addObject("admins", userSecureService.getUsersByRoles(singletonList(ADMINISTRATOR)));
        model.setViewName("admin/referral");
        return model;
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/removeOrder", method = GET)
    public ModelAndView orderDeletion() {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL);
        model.addObject("currencyPairList", currencyPairList);
        model.addObject("operationTypes", Arrays.asList(OperationType.SELL, OperationType.BUY));
        model.addObject("statusList", Arrays.asList(OrderStatus.values()));
        model.addObject("roleList", Arrays.asList(UserRole.values()));
        model.setViewName("admin/order_delete");
        return model;
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/removeStopOrder", method = GET)
    public ModelAndView stopOrderDeletion() {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL);
        model.addObject("currencyPairList", currencyPairList);
        model.addObject("operationTypes", Arrays.asList(OperationType.SELL, OperationType.BUY));
        model.addObject("statusList", Arrays.asList(OrderStatus.OPENED, OrderStatus.CLOSED, OrderStatus.CANCELLED, OrderStatus.INPROCESS));
        model.setViewName("admin/stop_order_delete");
        return model;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCmnRefRoot", method = POST)
    @ResponseBody
    public ResponseEntity<Void> editCommonReferralRoot(final @RequestParam("id") int id) {
        userService.updateCommonReferralRoot(id);
        return new ResponseEntity<>(OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editLevel", method = POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> editReferralLevel(final @RequestParam("level") int level, final @RequestParam("oldLevelId") int oldLevelId, final @RequestParam("percent") BigDecimal percent, final Locale locale) {
        final int result;
        try {
            result = referralService.updateReferralLevel(level, oldLevelId, percent);
            return new ResponseEntity<>(singletonMap("id", String.valueOf(result)), OK);
        } catch (final IllegalStateException e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error", messageSource.getMessage("admin.refPercentExceedMaximum", null, locale)), BAD_REQUEST);
        } catch (final Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error", messageSource.getMessage("admin.failureRefLevelEdit", null, locale)), BAD_REQUEST);
        }
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/users/deleteUserFile", method = POST)
    public ResponseEntity<Map<String, String>> deleteUserDoc(final @RequestParam("fileId") int fileId,
                                                             final @RequestParam("userId") int userId,
                                                             final @RequestParam("path") String path,
                                                             final Locale locale) {
        try {
            final String filename = path.substring(path.lastIndexOf('/') + 1);
            userFilesService.deleteUserFile(filename, userId);
        } catch (IOException e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error",
                    messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
        }
        userService.deleteUserFile(fileId);
        return new ResponseEntity<>(singletonMap("success",
                messageSource.getMessage("admin.successfulDeleteUserFiles", null, locale)), OK);
    }


    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/usersList", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataTable<List<User>> getAllUsers(@RequestParam Map<String, String> params) {
        List<UserRole> userRoles = userRoleService.getRealUserRoleByGroupRoleList(USERS);
        return userSecureService.getUsersByRolesPaginated(userRoles, params);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/admins", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllAdmins() {
        List<UserRole> adminRoles = userRoleService.getRealUserRoleByGroupRoleList(ADMINS);
        List<UserRole> botRoles = userRoleService.getRealUserRoleByGroupRoleList(BOT);
        return userSecureService.getUsersByRoles(new ArrayList<>(CollectionUtils.union(adminRoles, botRoles)));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/transactions", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataTable<List<OperationViewDto>> getUserTransactions(
            AdminTransactionsFilterData filterData,
            @RequestParam Integer id,
            @RequestParam Map<String, String> params,
            Principal principal,
            HttpServletRequest request) {
        filterData.initFilterItems();
        DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
        Integer requesterAdminId = userService.getIdByEmail(principal.getName());
        return transactionService.showUserOperationHistory(
                requesterAdminId,
                id,
                filterData,
                dataTableParams,
                localeResolver.resolveLocale(request));
    }


    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<WalletFormattedDto> getUserWallets(@RequestParam int id, @RequestParam(defaultValue = "false") Boolean onlyBalances) {
        boolean getExtendedInfo = userService.getUserRoleFromDB(id).showExtendedOrderInfo();
        return getExtendedInfo && !onlyBalances ? walletService.getAllUserWalletsForAdminDetailed(id) :
                walletService.getAllWallets(id)
                        .stream()
                        .map(WalletFormattedDto::new)
                        .collect(Collectors.toList());
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/comments", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Comment> getUserComments(@RequestParam int id, Principal principal) {

        return userService.getUserComments(id, principal.getName());
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/addComment", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> addUserComment(@RequestParam String newComment, @RequestParam String email,
                                                              @RequestParam boolean sendMessage, HttpServletRequest request, final Locale locale) {

        try {
            userService.addUserComment(GENERAL, newComment, email, sendMessage);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error",
                    messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(singletonMap("success",
                messageSource.getMessage("admin.successfulDeleteUserFiles", null, locale)), OK);
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/editUserComment", method = POST)
    public ResponseEntity<Map<String, String>> editUserComment(final @RequestParam("commentId") int commentId,
                                                               @RequestParam String newComment, @RequestParam String email,
                                                               @RequestParam boolean sendMessage, Principal principal,
                                                               final Locale locale) {
        try {
            userService.editUserComment(commentId, newComment, email, sendMessage, principal.getName());
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error",
                    messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(singletonMap("success",
                messageSource.getMessage("admin.successCommentDelete", null, locale)), OK);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/deleteUserComment", method = POST)
    public ResponseEntity<Map<String, String>> deleteUserComment(final @RequestParam("commentId") int commentId,
                                                                 final Locale locale) {
        try {
            userService.deleteUserComment(commentId);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error",
                    messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(singletonMap("success",
                messageSource.getMessage("admin.successCommentDelete", null, locale)), OK);
    }


    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/orders", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderWideListDto> getUserOrders(final @RequestParam int id, final @RequestParam("tableType") String tableType,
                                                final @RequestParam("currencyPairId") int currencyPairId, final HttpServletRequest request) {

        CurrencyPair currencyPair;
        if (currencyPairId != 0) {
            currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        } else {
            currencyPair = null;
        }
        String email = userService.getUserById(id).getEmail();
        return getOrderWideListDtos(tableType, currencyPair, email, localeResolver.resolveLocale(request));
    }

    private List<OrderWideListDto> getOrderWideListDtos(@RequestParam("tableType") String tableType, CurrencyPair currencyPair, String email, Locale locale) {
        List<OrderWideListDto> result = new ArrayList<>();
        switch (tableType) {
            case "ordersBuyClosed":
                List<OrderWideListDto> ordersBuyClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.BUY, 0, -1, locale);
                result = ordersBuyClosed;
                break;
            case "ordersSellClosed":
                List<OrderWideListDto> ordersSellClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.SELL, 0, -1, locale);
                result = ordersSellClosed;
                break;
            case "ordersBuyOpened":
                List<OrderWideListDto> ordersBuyOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.BUY, 0, -1, locale);
                result = ordersBuyOpened;
                break;
            case "ordersSellOpened":
                List<OrderWideListDto> ordersSellOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.SELL, 0, -1, locale);
                result = ordersSellOpened;
                break;
            case "ordersBuyCancelled":
                List<OrderWideListDto> ordersBuyCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.BUY, 0, -1, locale);
                result = ordersBuyCancelled;
                break;
            case "ordersSellCancelled":
                List<OrderWideListDto> ordersSellCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.SELL, 0, -1, locale);
                result = ordersSellCancelled;
                break;
            case "stopOrdersCancelled":
                List<OrderWideListDto> stopOrdersCancelled = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, null, 0, -1, locale);
                result = stopOrdersCancelled;
                break;
            case "stopOrdersClosed":
                List<OrderWideListDto> stopOrdersClosed = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, null, 0, -1, locale);
                result = stopOrdersClosed;
                break;
            case "stopOrdersOpened":
                List<OrderWideListDto> stopOrdersOpened = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, null, 0, -1, locale);
                result = stopOrdersOpened;
                break;
        }
        return result;
    }

    @AdminLoggable
    @RequestMapping({"/2a8fy7b07dxe44/editUser", "/2a8fy7b07dxe44/userInfo"})
    public ModelAndView editUser(@RequestParam(required = false) Integer id, @RequestParam(required = false) String email, HttpServletRequest request, Principal principal) {
        ModelAndView model = new ModelAndView();

        model.addObject("statusList", UserStatus.values());
        List<UserRole> roleList = new ArrayList<>();
        UserRole currentUserRole = userService.getUserRoleFromSecurityContext();
        if (currentUserRole == UserRole.ADMINISTRATOR) {
            roleList = userRoleService.getRolesAvailableForChangeByAdmin();
        }
        model.addObject("roleList", roleList);

        User user;
        if (email != null) {
            email = email.replace(" ", "+");
            user = userService.findByEmail(email);
        } else {
            user = userService.getUserById(id);
        }

        model.addObject("user", user);
        model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
        model.addObject("currencies", currencyService.findAllCurrencies());
        model.addObject("currencyPairs", currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL));
        model.setViewName("admin/editUser");
        model.addObject("userFiles", userService.findUserDoc(user.getId()));
        model.addObject("transactionTypes", Arrays.asList(TransactionType.values()));
        List<Merchant> merchantList = merchantService.findAll();
        merchantList.sort(Comparator.comparing(Merchant::getName));
        model.addObject("merchants", merchantList);
        Set<String> allowedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        AuthorityOptionsForm form = new AuthorityOptionsForm();
        form.setUserId(user.getId());
        form.setOptions(userService.getAuthorityOptionsForUser(user.getId(), allowedAuthorities, localeResolver.resolveLocale(request)));
        UserOperationAuthorityOptionsForm userOperationForm = new UserOperationAuthorityOptionsForm();
        userOperationForm.setUserId(user.getId());
        userOperationForm.setOptions(userOperationService.getUserOperationAuthorityOptions(user.getId(), localeResolver.resolveLocale(request)));
        model.addObject("authorityOptionsForm", form);
        model.addObject("userOperationAuthorityOptionsForm", userOperationForm);
        model.addObject("userActiveAuthorityOptions", userService.getActiveAuthorityOptionsForUser(user.getId())
                .stream()
                .map(e -> e.getAdminAuthority().name())
                .collect(Collectors.joining(",")));
        model.addObject("userLang", userService.getPreferedLang(user.getId()).toUpperCase());
        model.addObject("usersInvoiceRefillCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), REFILL));
        model.addObject("usersInvoiceWithdrawCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), WITHDRAW));
        model.addObject("usersInvoiceTransferCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), TRANSFER_VOUCHER));
        model.addObject("manualChangeAllowed", walletService.isUserAllowedToManuallyChangeWalletBalance(principal.getName(), user.getId()));
        model.addObject("walletsExtendedInfoRequired", user.getRole().showExtendedOrderInfo());
        return model;
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/2FaOptions/contact_info")
    public String setGlobal2fa(@RequestParam int userId, @RequestParam int notificatorId) {
        Subscribable subscribable = notificatorsService.getByNotificatorId(notificatorId);
        Preconditions.checkNotNull(subscribable);
        NotificatorSubscription subscription = subscribable.getSubscription(userId);
        Preconditions.checkState(subscription.isConnected());
        String contact = Preconditions.checkNotNull(subscription.getContactStr());
        int roleId = userService.getUserRoleFromDB(userId).getRole();
        BigDecimal fee = notificatorsService.getMessagePrice(notificatorId, roleId);
        BigDecimal price = doAction(fee, subscription.getPrice(), ActionType.ADD);
        return new JSONObject() {{
            put("contact", contact);
            put("price", price);
        }}.toString();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/edituser/submit", method = RequestMethod.POST)
    public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        UserRole currentUserRole = userService.getUserRoleFromSecurityContext();
        /*todo: Temporary commented for security reasons*/
        /*
        if (!(currentUserRole == ADMINISTRATOR) && user.getRole() == ADMINISTRATOR) {
            return new ModelAndView("403");
        }*/
        /*todo remove it; Temporary set null to prevent change role from admin, for security reasons*/
        //user.setRole(null);

        /*todo: Temporary commented for security reasons*/
        /*user.setConfirmPassword(user.getPassword());*/
        if (user.getFinpassword() == null) {
            user.setFinpassword("");
        }
        /**/
        registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("admin/editUser");
            model.addObject("statusList", UserStatus.values());
            /*todo: Temporary commented for security reasons*/
            /*if (currentUserRole == ADMINISTRATOR) {
                model.addObject("roleList", userRoleService.getRolesAvailableForChangeByAdmin());
            }*/
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setEmail(user.getEmail());
            /*todo: Temporary commented for security reasons*/
            /*updateUserDto.setPassword(user.getPassword());*/
            updateUserDto.setPhone(user.getPhone());
            /*todo: Temporary commented for security reasons*/
            if (currentUserRole == ADMINISTRATOR) {
                //Add to easy change user role to USER or VIP_USER !!! Not other
                if (user.getRole() == USER || user.getRole() == VIP_USER) {
                    updateUserDto.setRole(user.getRole());
                }
            }
            updateUserDto.setStatus(user.getStatus());
            userService.updateUserByAdmin(updateUserDto);
            if (updateUserDto.getStatus() == UserStatus.DELETED) {
                userSessionService.invalidateUserSessionExceptSpecific(updateUserDto.getEmail(), null);
            } else if (updateUserDto.getStatus() == UserStatus.BANNED_IN_CHAT) {
                notificationService.notifyUser(user.getEmail(), NotificationEvent.ADMIN, "account.bannedInChat.title", "dashboard.onlinechatbanned", null);
            }

            model.setViewName("redirect:/2a8fy7b07dxe44");
        }
        /**/
        model.addObject("user", user);
        /**/
        return model;
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/orderinfo", method = RequestMethod.GET)
    public AdminOrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
        return orderService.getAdminOrderInfo(id, localeResolver.resolveLocale(request));
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/stopOrderinfo", method = RequestMethod.GET)
    public OrderInfoDto getStopOrderInfo(@RequestParam int id, HttpServletRequest request) {
        return stopOrderService.getStopOrderInfo(id, localeResolver.resolveLocale(request));
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/transferInfo", method = RequestMethod.GET)
    public UserTransferInfoDto getTransferInfo(@RequestParam int id, HttpServletRequest request) {
        return userTransferService.getTransferInfoBySourceId(id);
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/orderdelete", method = RequestMethod.POST)
    public Integer deleteOrderByAdmin(@RequestParam int id) {
        try {
            return (Integer) orderService.deleteOrderByAdmin(id);
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/order/accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> acceptOrderByAdmin(@RequestParam int id, Principal principal, Locale locale) {
        orderService.acceptOrderByAdmin(principal.getName(), id, locale);
        return Collections.singletonMap("result", messageSource.getMessage("admin.order.acceptsuccess", new Object[]{id}, locale));
    }

    @AdminLoggable
    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/stopOrderDelete", method = RequestMethod.POST)
    public boolean deleteStopOrderByAdmin(@RequestParam int id, HttpServletRequest request) {
        try {
            return (boolean) stopOrderService.deleteOrderByAdmin(id, localeResolver.resolveLocale(request));
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/searchorders", method = RequestMethod.GET)
    public DataTable<List<OrderBasicInfoDto>> searchOrderByAdmin(AdminOrderFilterData adminOrderFilterData,
                                                                 @RequestParam Map<String, String> params,
                                                                 HttpServletRequest request) {
        //
        try {
            adminOrderFilterData.initFilterItems();
            DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
            DataTable<List<OrderBasicInfoDto>> orderInfo = orderService.searchOrdersByAdmin(adminOrderFilterData, dataTableParams,
                    localeResolver.resolveLocale(request));
            return orderInfo;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            DataTable<List<OrderBasicInfoDto>> errorResult = new DataTable<>();
            errorResult.setError(ex.getMessage());
            errorResult.setData(Collections.EMPTY_LIST);
            return errorResult;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/searchStopOrders", method = RequestMethod.GET)
    public DataTable<List<OrderBasicInfoDto>> searchStopOrderByAdmin(AdminStopOrderFilterData adminOrderFilterData,
                                                                     @RequestParam Map<String, String> params,
                                                                     HttpServletRequest request) {

        try {
            adminOrderFilterData.initFilterItems();
            DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
            DataTable<List<OrderBasicInfoDto>> orderInfo = stopOrderService.searchOrdersByAdmin(adminOrderFilterData, dataTableParams,
                    localeResolver.resolveLocale(request));
            return orderInfo;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            DataTable<List<OrderBasicInfoDto>> errorResult = new DataTable<>();
            errorResult.setError(ex.getMessage());
            errorResult.setData(Collections.EMPTY_LIST);
            return errorResult;
        }

    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/walletsSummaryTable", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserWalletSummaryDto> findRequestByStatus(
            @RequestParam("viewType") String viewTypeName,
            Principal principal) {
        Integer requesterUserId = userService.getIdByEmail(principal.getName());
        List<Integer> realRoleList = userRoleService.getRealUserRoleIdByBusinessRoleList(viewTypeName);
        return walletService.getUsersWalletsSummaryForPermittedCurrencyList(requesterUserId, realRoleList);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/userStatements/{walletId}")
    public ModelAndView accountStatementPage(@PathVariable("walletId") Integer walletId) {
        return new ModelAndView("/admin/user_statement", "walletId", walletId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/getStatements", method = RequestMethod.GET)
    @ResponseBody
    public DataTable<List<AccountStatementDto>> getStatements(@RequestParam Integer walletId, @RequestParam Map<String, String> params,
                                                              HttpServletRequest request) {
        Integer offset = Integer.parseInt(params.getOrDefault("start", "0"));
        Integer limit = Integer.parseInt(params.getOrDefault("length", "-1"));
        return transactionService.getAccountStatementForAdmin(walletId, offset, limit, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinConfirmation")
    public ModelAndView bitcoinTransactions() {
        return new ModelAndView("admin/transaction_bitcoin");
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/sessionControl")
    public ModelAndView sessionControl() {
        return new ModelAndView("admin/sessionControl");
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/userSessions")
    @ResponseBody
    public List<UserSessionDto> retrieveUserSessionInfo() {
        return userSessionService.retrieveUserSessionInfo();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/expireSession", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> expireSession(@RequestParam String sessionId) {
        return userSessionService.expireSession(sessionId);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits", method = RequestMethod.GET)
    public ModelAndView currencyLimits() {
        ModelAndView modelAndView = new ModelAndView("admin/currencyLimits");
        modelAndView.addObject("roleNames", BusinessUserRoleEnum.values());
        modelAndView.addObject("operationTypes", Arrays.asList(OperationType.INPUT.name(), OperationType.OUTPUT.name(), OperationType.USER_TRANSFER.name()));
        modelAndView.addObject("orderTypes", OrderType.values());
        return modelAndView;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/retrieve", method = RequestMethod.GET)
    @ResponseBody
    public List<CurrencyLimit> retrieveCurrencyLimits(@RequestParam String roleName,
                                                      @RequestParam OperationType operationType) {
        return currencyService.retrieveCurrencyLimitsForRole(roleName, operationType);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity editCurrencyLimit(@RequestParam int currencyId,
                                            @RequestParam OperationType operationType,
                                            @RequestParam String roleName,
                                            @RequestParam(defaultValue = "0") BigDecimal minAmount,
                                            @RequestParam(defaultValue = "0") BigDecimal minAmountUSD,
                                            @RequestParam Integer maxDailyRequest,
                                            @RequestParam(required = false) Object allRolesEdit) {
        if (nonNull(allRolesEdit)) {
            currencyService.updateCurrencyLimit(currencyId, operationType, minAmount, minAmountUSD, maxDailyRequest);
        } else {
            currencyService.updateCurrencyLimit(currencyId, operationType, roleName, minAmount, minAmountUSD, maxDailyRequest);
        }
        return ResponseEntity.ok().build();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/recalculate-limit-to-usd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity setPropertyRecalculateLimitToUsd(@RequestParam int currencyId,
                                                           @RequestParam OperationType operationType,
                                                           @RequestParam String roleName,
                                                           @RequestParam Boolean recalculateToUsd) {
        return currencyService.setPropertyCalculateLimitToUsd(currencyId, operationType, roleName, recalculateToUsd)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/convert-min-sum", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BigDecimal> convertMinSum(@RequestParam BigDecimal minSum) {
        return ResponseEntity.ok(converter.convert(minSum));
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/pairs/retrieve", method = RequestMethod.GET)
    @ResponseBody
    public List<CurrencyPairLimitDto> retrieveCurrencyPairLimits(@RequestParam String roleName,
                                                                 @RequestParam OrderType orderType) {
        return currencyService.findAllCurrencyLimitsForRoleAndType(roleName, orderType);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/pairs/submit", method = RequestMethod.POST)
    @ResponseBody
    public void editCurrencyPairLimit(@RequestParam int currencyPairId,
                                      @RequestParam OrderType orderType,
                                      @RequestParam String roleName,
                                      @RequestParam BigDecimal minRate,
                                      @RequestParam BigDecimal maxRate,
                                      @RequestParam BigDecimal minAmount,
                                      @RequestParam BigDecimal maxAmount) {
        validateDecimalLimitValues(minAmount, maxAmount);
        validateDecimalLimitValues(minRate, maxRate);
        currencyService.updateCurrencyPairLimit(currencyPairId, orderType, roleName, minRate, maxRate, minAmount, maxAmount);
    }

    private void validateDecimalLimitValues(BigDecimal min, BigDecimal max) {
        if (!BigDecimalProcessing.isNonNegative(min) || !BigDecimalProcessing.isNonNegative(max) || min.compareTo(max) >= 0) {
            throw new InvalidNumberParamException("Invalid request params!");
        }
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editAuthorities/submit", method = RequestMethod.POST)
    public RedirectView editAuthorities(@ModelAttribute AuthorityOptionsForm authorityOptionsForm, Principal principal,
                                        RedirectAttributes redirectAttributes) {
        RedirectView redirectView = new RedirectView("/2a8fy7b07dxe44/userInfo?id=" + authorityOptionsForm.getUserId());
        try {
            userService.updateAdminAuthorities(authorityOptionsForm.getOptions(), authorityOptionsForm.getUserId(), principal.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorNoty", e.getMessage());
            return redirectView;
        }
        String updatedUserEmail = userService.getUserById(authorityOptionsForm.getUserId()).getEmail();
        sessionRegistry.getAllPrincipals()
                .stream()
                .filter(currentPrincipal -> ((UserDetails) currentPrincipal).getUsername().equals(updatedUserEmail))
                .findFirst()
                .ifPresent(updatedUser -> sessionRegistry.getAllSessions(updatedUser, false).forEach(SessionInformation::expireNow));
        return redirectView;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/changeActiveBalance/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> changeActiveBalance(@RequestParam Integer userId, @RequestParam("currency") Integer currencyId,
                                                    @RequestParam BigDecimal amount, Principal principal) {
        LOG.debug("userId = " + userId + ", currencyId = " + currencyId + "? amount = " + amount);
        walletService.manualBalanceChange(userId, currencyId, amount, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @RequestMapping(value = "/2a8fy7b07dxe44/commissions", method = RequestMethod.GET)
    public ModelAndView commissions() {
        ModelAndView modelAndView = new ModelAndView("admin/editCommissions");
        modelAndView.addObject("roleNames", BusinessUserRoleEnum.values());
        return modelAndView;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/getCommissionsForRole", method = RequestMethod.GET)
    @ResponseBody
    public List<CommissionShortEditDto> retrieveCommissionsForRole(@RequestParam String role, HttpServletRequest request) {
        return commissionService.getEditableCommissionsByRole(role, localeResolver.resolveLocale(request));

    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/getMerchantCommissions", method = RequestMethod.GET)
    @ResponseBody
    public List<MerchantCurrencyOptionsDto> retrieveMerchantCommissions() {
        return merchantService.findMerchantCurrencyOptions(
                Stream.of(MerchantProcessType.values())
                        .filter(p -> !p.equals(MerchantProcessType.TRANSFER))
                        .map(Enum::name)
                        .collect(Collectors.toList()));
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/merchantCommissions/recalculate-commission-limit-to-usd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity setPropertyRecalculateCommissionLimitToUsd(@RequestParam String merchantName,
                                                                     @RequestParam String currencyName,
                                                                     @RequestParam Boolean recalculateToUsd) {
        return merchantService.setPropertyRecalculateCommissionLimitToUsd(merchantName, currencyName, recalculateToUsd)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editUserOperationTypeAuthorities/submit", method = RequestMethod.POST)
    public RedirectView editUserOperationTypeAuthorities(@ModelAttribute UserOperationAuthorityOptionsForm userOperationAuthorityOptionsForm, Principal principal,
                                                         RedirectAttributes redirectAttributes) {
        RedirectView redirectView = new RedirectView("/2a8fy7b07dxe44/userInfo?id=" + userOperationAuthorityOptionsForm.getUserId());
        try {
            userOperationService.updateUserOperationAuthority(userOperationAuthorityOptionsForm.getOptions(), userOperationAuthorityOptionsForm.getUserId(), principal.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorNoty", e.getMessage());
            return redirectView;
        }
        String updatedUserEmail = userService.getUserById(userOperationAuthorityOptionsForm.getUserId()).getEmail();
        sessionRegistry.getAllPrincipals()
                .stream()
                .filter(currentPrincipal -> ((UserDetails) currentPrincipal).getUsername().equals(updatedUserEmail))
                .findFirst()
                .ifPresent(updatedUser -> sessionRegistry.getAllSessions(updatedUser, false).forEach(SessionInformation::expireNow));
        return redirectView;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/getMerchantTransferCommissions", method = RequestMethod.GET)
    @ResponseBody
    public List<MerchantCurrencyOptionsDto> retrieveMerchantTransactionCommissions() {
        return merchantService.findMerchantCurrencyOptions(Collections.singletonList(MerchantProcessType.TRANSFER.name()));

    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editCommission", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> editCommission(@RequestParam("operationType") OperationType operationType,
                                               @RequestParam("userRole") String role,
                                               @RequestParam("commissionValue") BigDecimal value) {
        LOG.debug("operationType = " + operationType + ", userRole = " + role + ", value = " + value);
        commissionService.updateCommission(operationType, role, value);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editMerchantCommission", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity editMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
        commissionService.updateMerchantCommission(editMerchantCommissionDto);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editMerchantCommission/toggleSubtractWithdraw", method = RequestMethod.POST)
    @ResponseBody
    public void toggleSubtractMerchantCommissionForWithdraw(@RequestParam String merchantName, @RequestParam String currencyName,
                                                            @RequestParam Boolean subtractMerchantCommissionForWithdraw) {
        merchantService.toggleSubtractMerchantCommissionForWithdraw(merchantName, currencyName, subtractMerchantCommissionForWithdraw);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess", method = RequestMethod.GET)
    public ModelAndView merchantAccess() {
        return new ModelAndView("admin/merchantAccess");
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/data", method = RequestMethod.GET)
    @ResponseBody
    public List<MerchantCurrencyOptionsDto> merchantAccessData(@RequestParam List<String> processTypes) {
        List<MerchantCurrencyOptionsDto> merchantCurrencyOptions = merchantService.findMerchantCurrencyOptions(processTypes);
        LOG.debug(merchantCurrencyOptions);
        return merchantCurrencyOptions;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/autoWithdrawParams", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void setAutoWithdrawParams(@RequestBody MerchantCurrencyOptionsDto merchantCurrencyOptionsDto) {
        if (merchantCurrencyOptionsDto.getWithdrawAutoEnabled() == null) {
            merchantCurrencyOptionsDto.setWithdrawAutoEnabled(false);
        }
        withdrawService.setAutoWithdrawParams(merchantCurrencyOptionsDto);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/toggleBlock", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> toggleBlock(@RequestParam Integer merchantId,
                                            @RequestParam Integer currencyId,
                                            @RequestParam OperationType operationType) {
        LOG.debug("merchantId = " + merchantId + ", currencyId = " + currencyId + ", operationType = " + operationType);
        merchantService.toggleMerchantBlock(merchantId, currencyId, operationType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/setBlockForAll", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> switchBlockStatusForAll(@RequestParam OperationType operationType,
                                                        @RequestParam boolean blockStatus) {
        merchantService.setBlockForAll(operationType, blockStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/2a8fy7b07dxe44/merchantAccess/getCurrency")
    public List<Currency> getCurrency() {
        return currencyService.findAllCurrency();
    }

    @ResponseBody
    @PostMapping(value = "/2a8fy7b07dxe44/merchantAccess/currency/visibility/update")
    public ResponseEntity<Void> updateVisibilityCurrencyById(@RequestParam("currencyId") int currencyId) {
        currencyService.updateVisibilityCurrencyById(currencyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/2a8fy7b07dxe44/merchantAccess/getCurrencyPairs")
    public List<CurrencyPair> getCurrencyPairs() {
        return currencyService.findAllCurrencyPair();
    }

    @ResponseBody
    @PostMapping(value = "/2a8fy7b07dxe44/merchantAccess/currencyPair/visibility/update")
    public ResponseEntity<Void> updateVisibilityCurrencyPairById(@RequestParam("currencyPairId") int currencyPairId) {
        currencyService.updateVisibilityCurrencyPairById(currencyPairId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/2a8fy7b07dxe44/merchantAccess/currencyPair/directLink/update")
    public ResponseEntity<Void> updateAccessToDirectLinkCurrencyPairById(@RequestParam("currencyPairId") int currencyPairId) {
        currencyService.updateAccessToDirectLinkCurrencyPairById(currencyPairId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/phrases/{topic:.+}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getPhrases(
            @PathVariable String topic,
            @RequestParam String email) {
        String lang = userService.getPreferedLangByEmail(email);
        Locale userLocale = Locale.forLanguageTag(StringUtils.isEmpty(lang) ? "EN" : lang);
        UserCommentTopicEnum userCommentTopic = UserCommentTopicEnum.convert(topic.toUpperCase());
        List<String> phrases = phraseTemplateService.getAllByTopic(userCommentTopic)
                .stream()
                .map(e -> messageSource.getMessage(e, null, userLocale))
                .collect(Collectors.toList());
        return new HashMap<String, List<String>>() {{
            put("lang", Collections.singletonList(userLocale.getLanguage()));
            put("list", phrases);
        }};
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyPermissions/submit", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void editCurrencyPermissions(
            @RequestBody List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList,
            HttpSession httpSession,
            Principal principal) {
        userService.setCurrencyPermissionsByUserId(userCurrencyOperationPermissionDtoList);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/candleTable", method = RequestMethod.GET)
    public ModelAndView candleChartTable() {
        return new ModelAndView("/admin/candleTable", "currencyPairs", currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL));
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/getCandleTableData", method = RequestMethod.GET)
    @ResponseBody
    public List<CandleChartItemDto> getCandleChartData(@RequestParam("currencyPair") Integer currencyPairId,
                                                       @RequestParam("interval") String interval,
                                                       @RequestParam("startTime") String startTimeString) {
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        BackDealInterval backDealInterval = new BackDealInterval(interval);
        LocalDateTime startTime = LocalDateTime.parse(startTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return orderService.getDataForCandleChart(currencyPair, backDealInterval, startTime);
    }

    private BitcoinService getBitcoinServiceByMerchantName(String merchantName) {
        String serviceBeanName = merchantService.findByName(merchantName).getServiceBeanName();
        IMerchantService merchantService = serviceContext.getMerchantService(serviceBeanName);
        if (merchantService == null || !(merchantService instanceof BitcoinService)) {
            throw new NoRequestedBeansFoundException(serviceBeanName);
        }
        return (BitcoinService) merchantService;
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}", method = RequestMethod.GET)
    public ModelAndView bitcoinWallet(@PathVariable String merchantName, Locale locale) {
        ModelAndView modelAndView = new ModelAndView("/admin/btcWallet");
        modelAndView.addObject("merchant", merchantName);
        CoreWalletDto coreWallet = merchantService.retrieveCoreWalletByMerchantName(merchantName, locale);
        modelAndView.addObject("currency", coreWallet.getCurrencyName());
        modelAndView.addObject("title", coreWallet.getLocalizedTitle());
        BitcoinService bitcoinService = getBitcoinServiceByMerchantName(merchantName);
        modelAndView.addObject("walletInfo", bitcoinService.getWalletInfo());
        modelAndView.addObject("rawTxEnabled", bitcoinService.isRawTxEnabled());
        return modelAndView;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/adkWallet", method = RequestMethod.GET)
    public ModelAndView adkWallet() {
        ModelAndView modelAndView = new ModelAndView("/admin/adkWallet");
        modelAndView.addObject("merchant", AdkServiceImpl.MERCHANT_NAME);
        modelAndView.addObject("currency", AdkServiceImpl.CURRENCY_NAME);
        modelAndView.addObject("title", "ADK Wallet");
        modelAndView.addObject("balance", adkService.getBalance());
        return modelAndView;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/usdtWallet", method = RequestMethod.GET)
    public ModelAndView usdtWallet() {
        ModelAndView modelAndView = new ModelAndView("/admin/usdtWallet");
        modelAndView.addObject("merchant", omniService.getMerchant().getName());
        modelAndView.addObject("currency", omniService.getCurrency().getName());
        modelAndView.addObject("title", "USDT Wallet");
        modelAndView.addObject("btcBalance", omniService.getBtcBalance());
        modelAndView.addObject("usdtBalances", omniService.getUsdtBalances());
        return modelAndView;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transactions", method = RequestMethod.GET)
    @ResponseBody
    public List<BtcTransactionHistoryDto> getBtcTransactions(@PathVariable String merchantName) {
        return getBitcoinServiceByMerchantName(merchantName).listAllTransactions();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transactions/pagination", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataTable<List<BtcTransactionHistoryDto>> getAllTransactionByCoinLikeBitcoin(@PathVariable String merchantName, @RequestParam Map<String, String> tableParams)  throws BitcoindException, CommunicationException {
        return getBitcoinServiceByMerchantName(merchantName).listTransactions(tableParams);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/pageableTransactions", method = RequestMethod.GET)
    @ResponseBody
    public List<BtcTransactionHistoryDto> getBtcTransactionByPage(@PathVariable String merchantName, @RequestParam("page") int page) {
        return getBitcoinServiceByMerchantName(merchantName).listTransactions(page);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/findTransactions", method = GET)
    @ResponseBody
    public List<BtcTransactionHistoryDto> findTransactions(@PathVariable String merchantName, @RequestParam("value") String value) throws BitcoindException, CommunicationException {
        return getBitcoinServiceByMerchantName(merchantName).findTransactions(value);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/omniWallet/getUsdtTransactions", method = RequestMethod.GET)
    @ResponseBody
    public List<OmniTxDto> getOmniTransactions() {
        return omniService.getAllTransactions();
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/omniWallet/getBlockedAddersses", method = RequestMethod.GET)
    @ResponseBody
    public List<RefillRequestAddressShortDto> getOmniBlockedAddressses() {
        return omniService.getBlockedAddressesOmni();
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/omniWallet/createTransaction", method = RequestMethod.POST)
    @ResponseBody
    public void getOmniCreateRefill(@RequestParam Map<String, String> params) {
        omniService.createRefillRequestAdmin(params);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/estimatedFee", method = RequestMethod.GET)
    @ResponseBody
    public String getEstimatedFee(@PathVariable String merchantName) {
        return getBitcoinServiceByMerchantName(merchantName).getEstimatedFeeString();
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/actualFee", method = RequestMethod.GET)
    @ResponseBody
    public BigDecimal getActualFee(@PathVariable String merchantName) {
        return getBitcoinServiceByMerchantName(merchantName).getActualFee();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/setFee", method = RequestMethod.POST)
    @ResponseBody
    public void setFee(@PathVariable String merchantName, @RequestParam BigDecimal fee) {
        getBitcoinServiceByMerchantName(merchantName).setTxFee(fee);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/unlock", method = RequestMethod.POST)
    @ResponseBody
    public void submitPassword(@PathVariable String merchantName, @RequestParam String password) {
        getBitcoinServiceByMerchantName(merchantName).submitWalletPassword(password);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/sendToMany", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BtcAdminPaymentResponseDto sendToMany(@PathVariable String merchantName,
                                                 @RequestBody List<BtcWalletPaymentItemDto> payments, HttpServletRequest request) {
        LOG.debug(payments);
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        BtcAdminPaymentResponseDto responseDto = new BtcAdminPaymentResponseDto();
        responseDto.setResults(walletService.sendToMany(payments));
        responseDto.setNewBalance(walletService.getWalletInfo().getBalance());
        return responseDto;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transaction/details", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, RefillRequestBtcInfoDto> getTransactionDetails(@PathVariable String merchantName,
                                                                      @RequestParam("currency") String currencyName,
                                                                      @RequestParam String hash,
                                                                      @RequestParam String address) {
        Optional<RefillRequestBtcInfoDto> dtoResult = refillService.findRefillRequestByAddressAndMerchantTransactionId(address, hash,
                merchantName, currencyName);
        return dtoResult.isPresent() ? Collections.singletonMap("result", dtoResult.get()) : Collections.EMPTY_MAP;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transaction/create", method = RequestMethod.POST)
    @ResponseBody
    public void createBtcRefillRequest(@PathVariable String merchantName, @RequestParam Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        LOG.debug(params);
        getBitcoinServiceByMerchantName(merchantName).processPayment(params);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/newAddress", method = RequestMethod.GET)
    @ResponseBody
    public String getNewAddress(@PathVariable String merchantName) {
        return getBitcoinServiceByMerchantName(merchantName).getNewAddressForAdmin();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/checkPayments", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void checkPayments(@PathVariable String merchantName,
                              @RequestParam(required = false) String blockhash) {
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        walletService.scanForUnprocessedTransactions(blockhash);
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/findReferral")
    @ResponseBody
    public RefsListContainer findUserReferral(@RequestParam("action") String action,
                                              @RequestParam(value = "userId", required = false) Integer userId,
                                              @RequestParam("profitUser") int profitUser,
                                              @RequestParam(value = "onPage", defaultValue = "20") int onPage,
                                              @RequestParam(value = "page", defaultValue = "1") int page,
                                              RefFilterData refFilterData) {
        LOG.error("filter data " + refFilterData);
        return referralService.getRefsContainerForReq(action, userId, profitUser, onPage, page, refFilterData);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/downloadRef")
    public void downloadUserRefferalStructure(@RequestParam("profitUser") int profitUser,
                                              RefFilterData refFilterData,
                                              HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String reportName =
                "referrals-"
                        .concat(userService.getEmailById(profitUser))
                        .concat(".csv");
        response.setHeader("Content-disposition", "attachment;filename=" + reportName);
        List<String> refsList = referralService.getRefsListForDownload(profitUser, refFilterData);
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        try {
            for (String transaction : refsList) {
                writer.write(transaction);
            }
        } catch (IOException e) {
            LOG.error("error download transactions " + e);
        } finally {
            writer.flush();
            writer.close();
        }
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading", method = GET)
    public ModelAndView autoTrading() {
        ModelAndView modelAndView = new ModelAndView("/admin/autoTrading");
        botService.retrieveBotFromDB().ifPresent(bot -> {
            modelAndView.addObject("bot", bot);
            modelAndView.addObject("botUser", userService.getUserById(bot.getUserId()));
            modelAndView.addObject("currencyPairs", currencyService.getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType.ALL));
        });
        return modelAndView;
    }


    @GetMapping(value = "/getWalletBalanceByCurrencyName")
    public ResponseEntity<Map<String, String>> getWalletBalanceByCurrencyName(@RequestParam("currency") String currencyName,
                                                                              @RequestParam("token") String token,
                                                                              @RequestParam(value = "address", required = false) String address) throws IOException {

        if (!token.equals("ZXzG8z13nApRXDzvOv7hU41kYHAJSLET")) {
            throw new RuntimeException("Some unexpected exception");
        }
        if (currencyName.equals("EDR")) {
            String balance = edcServiceNode.extractBalance(address, 0);
            Map<String, String> response = new HashMap<>();
            response.put("EDR", balance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Currency byName = currencyService.findByName(currencyName);

        List<Merchant> allByCurrency = merchantService.findAllByCurrency(byName);
        List<Merchant> collect = allByCurrency
                .stream().
                filter(merchant -> merchant.getProcessType() == MerchantProcessType.CRYPTO).collect(Collectors.toList());
        Map<String, String> collect1 = collect.
                stream().
                collect(Collectors.toMap(
                        Merchant::getName,
                        merchant -> getBitcoinServiceByMerchantName(merchant.getName()).getWalletInfo().getBalance()));


        return new ResponseEntity<>(collect1, HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/roleSettings", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<UserRoleSettings> getRoleSettings() {
        return userRoleService.retrieveSettingsForAllRoles();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/roleSettings/update", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void updateSettingsForRole(@RequestBody UserRoleSettings userRoleSettings) {
        userRoleService.updateSettingsForRole(userRoleSettings);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/create", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void createBot(@RequestParam String nickname, @RequestParam String email, @RequestParam String password) {
        botService.createBot(nickname, email, password);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/update", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void updateBot(@RequestBody @Valid BotTrader botTrader, Locale locale) {
        botService.updateBot(botTrader, locale);

    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/launchSettings", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<BotLaunchSettings> getLaunchSettings(@RequestParam Integer botId) {
        return botService.retrieveLaunchSettings(botId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/tradingSettings", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Map<String, BotTradingSettingsShortDto> getTradingSettings(@RequestParam Integer launchSettingsId) {
        return botService.retrieveTradingSettingsShort(launchSettingsId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/launchSettings/toggle", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void toggleCreationForCurrencyPair(@RequestParam Integer currencyPairId, @RequestParam Boolean status, Locale locale) {
        botService.toggleBotStatusForCurrencyPair(currencyPairId, status, locale);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/launchSettings/userOrders/toggle", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void toggleConsiderUserOrders(@RequestParam Integer launchSettingsId, @RequestParam Boolean considerUserOrders, Locale locale) {
        botService.setConsiderUserOrders(launchSettingsId, considerUserOrders);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/launchSettings/update", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void updateLaunchSettings(@Valid BotLaunchSettings launchSettings) {
        botService.updateLaunchSettings(launchSettings);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/tradingSettings/update", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public void updateTradingSettings(@RequestBody List<BotTradingSettingsShortDto> tradingSettingsList, Locale locale) {

        log.info(tradingSettingsList);
        tradingSettingsList.forEach(tradingSettings -> {
            if (tradingSettings.getMinAmount().compareTo(tradingSettings.getMaxAmount()) > 0 ||
                    tradingSettings.getMinPrice().compareTo(tradingSettings.getMaxPrice()) > 0) {
                throw new InvalidNumberParamException(messageSource.getMessage("admin.autoTrading.settings.minGreater", null, locale));
            }
            botService.updateTradingSettings(tradingSettings);
        });
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/notificatorsSettings")
    public String notificatorsSettings(Model model) {
        model.addAttribute("roles", UserRole.values());
        return "admin/notificatorsSettings";
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/getNotificatorsSettings")
    public List<Notificator> getNotificatorsSettings(@RequestParam int roleId) {
        return notificatorsService.getNotificatorSettingsByRole(roleId);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/setNotificatorsSetting", method = POST)
    public ResponseEntity<Void> setNotificatorsSetting(@RequestParam BigDecimal price,
                                                       @RequestParam int roleId,
                                                       @RequestParam int notificatorId) {
        Notificator notificator = notificatorsService.getById(notificatorId);
        Preconditions.checkArgument(!notificator.getPayTypeEnum().equals(NotificationPayTypeEnum.FREE));
        if (notificator.getPayTypeEnum().equals(NotificationPayTypeEnum.PAY_FOR_EACH)) {
            Preconditions.checkState(price.compareTo(BigDecimal.ZERO) >= 0
                    && price.compareTo(BigDecimal.valueOf(10000)) < 0);
        }
        notificatorsService.updateNotificatorPrice(price, roleId, notificatorId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/notificatorSettings/enable", method = RequestMethod.POST)
    public ResponseEntity<Void> enableNotificators(@RequestParam(name = "notificatorId") int notificatorId,
                                                   @RequestParam(name = "enable") boolean enable) {
        notificatorsService.setEnable(notificatorId, enable);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/order/acceptMany", method = POST)
    public void acceptManyOrders(@RequestParam List<Integer> orderIds, Principal principal, Locale locale) {
        log.info(orderIds);
        orderService.acceptManyOrdersByAdmin(principal.getName(), orderIds, locale);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/order/deleteMany", method = POST)
    public void deleteManyOrders(@RequestParam List<Integer> orderIds) {
        orderService.deleteManyOrdersByAdmin(orderIds);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats", method = GET)
    public ModelAndView generalStats() {
        Map<UserRole, Boolean> defaultRoleFilter = new EnumMap<>(UserRole.class);
        defaultRoleFilter.putAll(Stream.of(UserRole.values())
                .filter(value -> value != ROLE_CHANGE_PASSWORD)
                .collect(Collectors.toMap(
                        value -> value,
                        value -> false)));
        userRoleService.getRolesUsingRealMoney().forEach(role -> defaultRoleFilter.replace(role, true));
        ModelAndView modelAndView = new ModelAndView("admin/generalStats");
        modelAndView.addObject("defaultRoleFilter", defaultRoleFilter);
        modelAndView.addObject("roleGroups", Arrays.asList(ReportGroupUserRole.values()));
        modelAndView.addObject("currencyList", currencyService.findAllCurrency());


        return modelAndView;
    }

    @AdminLoggable
    @GetMapping(value = "/2a8fy7b07dxe44/alerts")
    public String alertsPage(Model model) {
        model.addAttribute("update", alertsService.getAlert(AlertType.UPDATE));
        model.addAttribute("tech", alertsService.getAlert(AlertType.TECHNICAL_WORKS));
        return "admin/alertMessages";
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/getSubtractFeeStatus", method = GET)
    public Boolean getSubtractFeeFromAmount(@PathVariable String merchantName) {
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        return walletService.getSubtractFeeFromAmount();
    }

    @AdminLoggable
    @PostMapping(value = "/2a8fy7b07dxe44/alerts/update")
    public String alertsUpdatePage(@Valid AlertDto alertDto) {
        alertsService.updateAction(alertDto);
        return "redirect:/2a8fy7b07dxe44/alerts";
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/setSubtractFee", method = POST)
    public void setSubtractFeeFromAmount(@PathVariable String merchantName,
                                         @RequestParam Boolean subtractFee) {
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        walletService.setSubtractFeeFromAmount(subtractFee);
    }


    @GetMapping(value = "/2a8fy7b07dxe44/refillAddresses")
    public String refillAddressesPage(Model model) {
        model.addAttribute("currencies", currencyService.findAllCurrenciesByProcessType(MerchantProcessType.CRYPTO));
        return "admin/refillAddresses";
    }

    @ResponseBody
    @GetMapping(value = "/2a8fy7b07dxe44/refillAddresses/table")
    public DataTable<List<RefillRequestAddressShortDto>> getRefillAddressesTable(RefillAddressFilterData filterData,
                                                                                 @RequestParam Map<String, String> params) {
        DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
        filterData.initFilterItems();
        return refillService.getAdressesShortDto(dataTableParams, filterData);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/prepareRawTx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BtcAdminPreparedTxDto prepareRawTransactions(@PathVariable String merchantName,
                                                        @RequestBody List<BtcWalletPaymentItemDto> payments,
                                                        HttpServletRequest request) {
        LOG.debug(payments);
    /*long uniqueAddressesCount = payments.stream().map(BtcWalletPaymentItemDto::getAddress).distinct().count();
    if (uniqueAddressesCount != payments.size()) {
      throw new InvalidBtcPaymentDataException("Only unique addresses allowed in single payment!");
    }*/
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        HttpSession session = request.getSession();
        List<BtcPreparedTransactionDto> preparedTransactions = (List<BtcPreparedTransactionDto>) session.getAttribute("PREPARED_RAW_TXES");
        BtcAdminPreparedTxDto result;

        if (preparedTransactions != null) {
            result = walletService.updateRawTransactions(preparedTransactions);
        } else {
            result = walletService.prepareRawTransactions(payments);
        }
        final Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            session.setAttribute("PREPARED_RAW_TXES", result.getPreparedTransactions());
        }
        return result;
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/sendRawTx", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BtcAdminPaymentResponseDto sendRawTransactions(@PathVariable String merchantName,
                                                          HttpServletRequest request) {
        HttpSession session = request.getSession();
        final Object mutex = WebUtils.getSessionMutex(session);
        Optional<List<BtcPreparedTransactionDto>> preparedTransactionsOptional = Optional.empty();
        synchronized (mutex) {
            preparedTransactionsOptional = Optional.ofNullable(((List<BtcPreparedTransactionDto>) session.getAttribute("PREPARED_RAW_TXES")));
            session.removeAttribute("PREPARED_RAW_TXES");
        }
        if (!preparedTransactionsOptional.isPresent()) {
            throw new IllegalStateException("No prepared transactions stored in session!");
        }
        List<BtcPreparedTransactionDto> preparedTransactions = preparedTransactionsOptional.get();
        BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
        BtcAdminPaymentResponseDto responseDto = new BtcAdminPaymentResponseDto();
        responseDto.setResults(walletService.sendRawTransactions(preparedTransactions));
        responseDto.setNewBalance(walletService.getWalletInfo().getBalance());
        return responseDto;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/listWallets", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<CoreWalletDto> listCoreWallets(HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        return merchantService.retrieveCoreWallets(locale);
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets", method = RequestMethod.GET)
    public ModelAndView externalWallets() {
        return new ModelAndView("admin/externalWallets");
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/retrieve", method = RequestMethod.GET)
    @ResponseBody
    public List<ExternalWalletBalancesDto> retrieveExternalWalletBalances() {
        return walletService.getExternalWalletBalances();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/retrieve/summaryUSD", method = RequestMethod.GET)
    @ResponseBody
    public BigDecimal retrieveSummaryUSD() {
        return walletService.retrieveSummaryUSD();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/retrieve/summaryBTC", method = RequestMethod.GET)
    @ResponseBody
    public BigDecimal retrieveSummaryBTC() {
        return walletService.retrieveSummaryBTC();
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/retrieve/reservedWallets/{currency_id}", method = RequestMethod.GET)
    @ResponseBody
    public List<ExternalReservedWalletAddressDto> getReservedWallets(@PathVariable("currency_id") String currencyId) {
        return walletService.getReservedWalletsByCurrencyId(currencyId);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/address/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createWalletAddress(@RequestParam int currencyId) {
        walletService.createWalletAddress(currencyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/address/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity deleteWalletAddress(@RequestParam int id,
                                              @RequestParam int currencyId,
                                              @RequestParam String walletAddress) {
        walletService.deleteWalletAddress(id, currencyId, walletAddress);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/certainty/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateSignOfCertaintyForCurrency(@RequestParam int currencyId,
                                                           @RequestParam boolean signOfCertainty) {
        walletService.updateSignOfCertaintyForCurrency(currencyId, signOfCertainty);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/address/saveAsAddress/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> submitWalletAddressAsAddress(@RequestParam int id,
                                                               @RequestParam int currencyId,
                                                               @RequestParam String walletAddress,
                                                               Locale locale) {
        final BigDecimal reservedWalletBalance = walletService.getExternalReservedWalletBalance(currencyId, walletAddress);
        if (nonNull(reservedWalletBalance)) {
            ExternalReservedWalletAddressDto externalReservedWalletAddressDto = ExternalReservedWalletAddressDto.builder()
                    .id(id)
                    .currencyId(currencyId)
                    .walletAddress(walletAddress)
                    .balance(reservedWalletBalance)
                    .build();
            walletService.updateWalletAddress(externalReservedWalletAddressDto, true);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(messageSource.getMessage("user.settings.changePassword.fail", null, locale), HttpStatus.NOT_FOUND);
        }
    }

    @AdminLoggable
    @RequestMapping(value = "/2a8fy7b07dxe44/externalWallets/address/saveAsName/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity submitWalletAddressAsName(@RequestParam int id,
                                                    @RequestParam int currencyId,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam String walletAddress,
                                                    @RequestParam BigDecimal reservedWalletBalance) {
        ExternalReservedWalletAddressDto externalReservedWalletAddressDto = ExternalReservedWalletAddressDto.builder()
                .id(id)
                .name(name)
                .currencyId(currencyId)
                .walletAddress(walletAddress)
                .balance(reservedWalletBalance)
                .build();
        walletService.updateWalletAddress(externalReservedWalletAddressDto, false);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/usersInfo", method = GET)
    public ResponseEntity<UsersInfoDto> getUsersInfo(@RequestParam("startTime") String startTimeString,
                                                     @RequestParam("endTime") String endTimeString,
                                                     @RequestParam("roles") List<UserRole> userRoles) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));

        return ResponseEntity.ok(userService.getUsersInfoFromCache(startTime, endTime, userRoles));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/bitcoin/b2x/sendToReserve", method = POST)
    public ResponseEntity sendMoneyToReserveAddress(@RequestParam("transactionCount") int transactionCount,
                                                    @RequestParam("transactionAmount") String amount) {
        b2XTransferToReserveAccount.transferToReserveAccountFromNode(transactionCount, amount);
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({NotEnoughMoneyException.class, NotEnoughUserWalletMoneyException.class, OrderCreationException.class,
            OrderAcceptionException.class, OrderCancellingException.class, NotAcceptableOrderException.class,
            NotCreatableOrderException.class})
    @ResponseBody
    public ErrorInfo orderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo methodArgumentNotValidExceptionHandler(HttpServletRequest req, MethodArgumentNotValidException ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ErrorInfo bindExceptionHandler(HttpServletRequest req, BindException ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        LOG.error(exception);
        exception.printStackTrace();
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    public static void main(String[] args) {
        System.out.println(WithdrawStatusEnum.getEndStatesSet()
                .stream()
                .map(InvoiceStatus::getCode)
                .collect(Collectors.toList()));
    }

}
