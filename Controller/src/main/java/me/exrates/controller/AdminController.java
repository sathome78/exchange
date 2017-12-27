package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.config.WebAppConfig;
import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.exception.*;
import me.exrates.controller.exception.NoRequestedBeansFoundException;
import me.exrates.controller.exception.NotAcceptableOrderException;
import me.exrates.controller.exception.NotEnoughMoneyException;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.filterData.RefillAddressfilterData;
import me.exrates.model.dto.merchants.btc.BtcAdminPaymentResponseDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.form.AuthorityOptionsForm;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.Subscribable;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.waves.WavesRestClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.GroupUserRoleEnum.ADMINS;
import static me.exrates.model.enums.GroupUserRoleEnum.USERS;
import static me.exrates.model.enums.UserCommentTopicEnum.GENERAL;
import static me.exrates.model.enums.UserRole.ADMINISTRATOR;
import static me.exrates.model.enums.UserRole.ROLE_CHANGE_PASSWORD;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.TRANSFER_VOUCHER;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
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
  private NotificationsSettingsService notificationsSettingsService;

  @Autowired
  @Qualifier("ExratesSessionRegistry")
  private SessionRegistry sessionRegistry;

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
    return roles.stream().map(e -> "'" + e.name() + "'").collect(Collectors.joining(",", "hasAnyAuthority(", ")"));
  }

  @RequestMapping(value = {"/2a8fy7b07dxe44", "/2a8fy7b07dxe44/users"})
  public ModelAndView admin() {
    ModelAndView model = new ModelAndView();
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    model.addObject("currencyPairList", currencyPairList);
    model.addObject("enable_2fa", userService.isGlobal2FaActive());
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
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
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
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
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
    return userSecureService.getUsersByRoles(adminRoles);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/transactions", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Future<DataTable<List<OperationViewDto>>> getUserTransactions(
      AdminTransactionsFilterData filterData,
      @RequestParam Integer id,
      @RequestParam Map<String, String> params,
      Principal principal,
      HttpServletRequest request) {
    filterData.initFilterItems();
    DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    return CompletableFuture.supplyAsync(() -> transactionService.showUserOperationHistory(
            requesterAdminId,
            id,
            filterData,
            dataTableParams,
            localeResolver.resolveLocale(request)));
  }


  @AdminLoggable
  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<WalletFormattedDto> getUserWallets(@RequestParam int id) {
    return walletService.getAllWallets(id).stream().map(WalletFormattedDto::new).collect(Collectors.toList());
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
      userService.editUserComment(commentId, newComment,email, sendMessage, principal.getName());
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
  public Future<List<OrderWideListDto>> getUserOrders(final @RequestParam int id, final @RequestParam("tableType") String tableType,
                                              final @RequestParam("currencyPairId") int currencyPairId, final HttpServletRequest request) {

    CurrencyPair currencyPair;
    if (currencyPairId != 0) {
      currencyPair = currencyService.findCurrencyPairById(currencyPairId);
    } else {
      currencyPair = null;
    }
    String email = userService.getUserById(id).getEmail();
    Map<String, List<OrderWideListDto>> resultMap = new HashMap<>();

      return CompletableFuture.supplyAsync(() -> getOrderWideListDtos(tableType, currencyPair, email, localeResolver.resolveLocale(request)));
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
            result = stopOrdersCancelled ;
            break;
          case "stopOrdersClosed":
            List<OrderWideListDto> stopOrdersClosed = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, null, 0, -1, locale);
            result = stopOrdersClosed ;
            break;
          case "stopOrdersOpened":
            List<OrderWideListDto> stopOrdersOpened = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, null,0, -1, locale);
            result = stopOrdersOpened;
            break;
        }
        return result;
    }

  @RequestMapping("/2a8fy7b07dxe44/addUser")
  public ModelAndView addUser(HttpSession httpSession) {
    ModelAndView model = new ModelAndView();

    model.addObject("roleList", userService.getAllRoles());
    User user = new User();
    model.addObject("user", user);
    model.setViewName("admin/addUser");

    return model;
  }

  @AdminLoggable
  @RequestMapping(value = "/2a8fy7b07dxe44/adduser/submit", method = RequestMethod.POST)
  public ModelAndView submitcreate(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

    user.setConfirmPassword(user.getPassword());
    user.setStatus(UserStatus.ACTIVE);
    registerFormValidation.validate(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      model.addObject("roleList", userService.getAllRoles());
      model.setViewName("admin/addUser");
    } else {
      userService.createUserByAdmin(user);
      model.setViewName("redirect:/2a8fy7b07dxe44");
    }

    model.addObject("user", user);

    return model;
  }

  @AdminLoggable
  @RequestMapping({"/2a8fy7b07dxe44/editUser", "/2a8fy7b07dxe44/userInfo"})
  public ModelAndView editUser(@RequestParam int id, HttpServletRequest request, Principal principal) {

    ModelAndView model = new ModelAndView();

    model.addObject("statusList", UserStatus.values());
    List<UserRole> roleList = new ArrayList<>();
    UserRole currentUserRole = userService.getUserRoleFromSecurityContext();
    if (currentUserRole == UserRole.ADMINISTRATOR) {
      roleList = userRoleService.getRolesAvailableForChangeByAdmin();
    }
    model.addObject("roleList", roleList);
    User user = userService.getUserById(id);
    user.setId(id);
    model.addObject("user", user);
    model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
    model.addObject("currencies", currencyService.findAllCurrencies());
    model.addObject("currencyPairs", currencyService.getAllCurrencyPairs());
    model.setViewName("admin/editUser");
    model.addObject("userFiles", userService.findUserDoc(id));
    model.addObject("transactionTypes", Arrays.asList(TransactionType.values()));
    List<Merchant> merchantList = merchantService.findAll();
    model.addObject("merchants", merchantList);
    Set<String> allowedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    AuthorityOptionsForm form = new AuthorityOptionsForm();
    form.setUserId(id);
    form.setOptions(userService.getAuthorityOptionsForUser(id, allowedAuthorities, localeResolver.resolveLocale(request)));
    model.addObject("authorityOptionsForm", form);
    model.addObject("userActiveAuthorityOptions", userService.getActiveAuthorityOptionsForUser(id).stream().map(e -> e.getAdminAuthority().name()).collect(Collectors.joining(",")));
    model.addObject("userLang", userService.getPreferedLang(id).toUpperCase());
    model.addObject("usersInvoiceRefillCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), REFILL));
    model.addObject("usersInvoiceWithdrawCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), WITHDRAW));
    model.addObject("usersInvoiceTransferCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), TRANSFER_VOUCHER));
    model.addObject("user2faOptions", notificationsSettingsService.get2faOptionsForUser(user.getId()));
    model.addObject("manualChangeAllowed", walletService.isUserAllowedToManuallyChangeWalletBalance(principal.getName(), id));
    return model;
  }

  @AdminLoggable
  @RequestMapping("/2a8fy7b07dxe44/editUser/submit2faOptions")
  public RedirectView submitNotificationOptions(@RequestParam int userId, RedirectAttributes redirectAttributes,
                                                HttpServletRequest request) {
    RedirectView redirectView = new RedirectView("/2a8fy7b07dxe44/userInfo?id=".concat(String.valueOf(userId)));
    try {
      Map<Integer, NotificationsUserSetting> settingsMap = notificationsSettingsService.getSettingsMap(userId);
      settingsMap.forEach((k,v) -> {
        Integer notificatorId = Integer.parseInt(request.getParameter(k.toString()));
        if (notificatorId.equals(0)) {
          notificatorId = null;
        }
        if (v == null) {
          NotificationsUserSetting setting = NotificationsUserSetting.builder()
                  .userId(userId)
                  .notificatorId(notificatorId)
                  .notificationMessageEventEnum(NotificationMessageEventEnum.convert(k))
                  .build();
          notificationsSettingsService.createOrUpdate(setting);
        } else if (v.getNotificatorId() == null || !v.getNotificatorId().equals(notificatorId)) {
          v.setNotificatorId(notificatorId);
          notificationsSettingsService.createOrUpdate(v);
        }
      });
      redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("message.settings_successfully_saved", null,
              localeResolver.resolveLocale(request)));
    } catch (Exception e) {
      log.error(e);
      redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("message.error_saving_settings", null,
              localeResolver.resolveLocale(request)));
      throw e;
    }
    return redirectView;
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
    return new JSONObject(){{put("contact", contact);
      put("price", price);}}.toString();
  }

  @AdminLoggable
  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/set2fa", method = POST)
  public String setGlobal2fa(HttpServletRequest request, HttpServletResponse response) {
        boolean use2fa = String.valueOf(request.getParameter("enable_2fa")).equals("on");
        try {
            userService.setGlobal2FaActive(use2fa);
        } catch (Exception e) {
            log.error(e);
            response.setStatus(400);
            return "error";
        }
        return "ok";
  }

  @AdminLoggable
  @RequestMapping(value = "/2a8fy7b07dxe44/edituser/submit", method = RequestMethod.POST)
  public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
    UserRole currentUserRole = userService.getUserRoleFromSecurityContext();

    if (!(currentUserRole == ADMINISTRATOR) && user.getRole() == ADMINISTRATOR) {
      return new ModelAndView("403");
    }
    user.setConfirmPassword(user.getPassword());
    if (user.getFinpassword() == null) {
      user.setFinpassword("");
    }
        /**/
    registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      model.setViewName("admin/editUser");
      model.addObject("statusList", UserStatus.values());
      if (currentUserRole == ADMINISTRATOR) {
        model.addObject("roleList", userRoleService.getRolesAvailableForChangeByAdmin());
      }
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setEmail(user.getEmail());
      updateUserDto.setPassword(user.getPassword());
      updateUserDto.setPhone(user.getPhone());
      if (currentUserRole == ADMINISTRATOR) {
        updateUserDto.setRole(user.getRole());
      }
      updateUserDto.setStatus(user.getUserStatus());
      userService.updateUserByAdmin(updateUserDto);
      if (updateUserDto.getStatus() == UserStatus.DELETED) {
        invalidateUserSession(updateUserDto.getEmail());
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

  private void invalidateUserSession(String userEmail) {
    Optional<Object> updatedUser = sessionRegistry.getAllPrincipals().stream()
        .filter(principalObj -> {
          UserDetails principal = (UserDetails) principalObj;
          return userEmail.equals(principal.getUsername());
        })
        .findFirst();
    if (updatedUser.isPresent()) {
      sessionRegistry.getAllSessions(updatedUser.get(), false).forEach(SessionInformation::expireNow);
    }
  }


  @RequestMapping(value = "/settings/uploadFile", method = POST)
  public ModelAndView uploadUserDocs(final @RequestParam("file") MultipartFile[] multipartFiles,
                                     final Principal principal,
                                     final Locale locale) {
    final ModelAndView mav = new ModelAndView("globalPages/settings");
    final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
    final List<MultipartFile> uploaded = userFilesService.reduceInvalidFiles(multipartFiles);
    mav.addObject("user", user);
    if (uploaded.isEmpty()) {
      mav.addObject("userFiles", userService.findUserDoc(user.getId()));
      mav.addObject("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
      return mav;
    }
    try {
      userFilesService.createUserFiles(user.getId(), uploaded);
    } catch (final IOException e) {
      LOG.error(e);
      mav.addObject("errorNoty", messageSource.getMessage("admin.internalError", null, locale));
      return mav;
    }
    mav.addObject("successNoty", messageSource.getMessage("admin.successUploadFiles", null, locale));
    mav.addObject("userFiles", userService.findUserDoc(user.getId()));
    return mav;
  }

  @RequestMapping(value = "settings/changePassword/submit", method = POST)
  public ModelAndView submitsettingsPassword(@Valid @ModelAttribute User user, BindingResult result,
                                             ModelAndView model, HttpServletRequest request) {
    user.setStatus(user.getUserStatus());
    registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      model.setViewName("globalPages/settings");
      model.addObject("sectionid", "passwords-changing");
      model.addObject("tabIdx", 0);
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setPassword(user.getPassword());
      updateUserDto.setEmail(user.getEmail()); //need for send the email
      userService.update(updateUserDto, localeResolver.resolveLocale(request));
      new SecurityContextLogoutHandler().logout(request, null, null);
      model.setViewName("redirect:/dashboard");
    }

    model.addObject("user", user);

    return model;
  }

  @RequestMapping(value = "settings/changeFinPassword/submit", method = POST)
  public ModelAndView submitsettingsFinPassword(@Valid @ModelAttribute User user, BindingResult result,
                                                ModelAndView model, HttpServletRequest request, RedirectAttributes redir) {
    user.setStatus(user.getUserStatus());
    registerFormValidation.validateResetFinPassword(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      model.setViewName("globalPages/settings");
      model.addObject("sectionid", "passwords-changing");
      model.addObject("tabIdx", 1);
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setFinpassword(user.getFinpassword());
      updateUserDto.setEmail(user.getEmail()); //need for send the email
      userService.update(updateUserDto, localeResolver.resolveLocale(request));

      final String message = messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request));
      redir.addFlashAttribute("msg", message);
      model.setViewName("redirect:/settings");
    }

    return model;
  }

  @RequestMapping(value = "/changePasswordConfirm")
  public ModelAndView verifyEmail(@RequestParam("token") String token, HttpServletRequest request) {
    try {
      request.setCharacterEncoding("utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    ModelAndView model = new ModelAndView();
    try {
      if (userService.verifyUserEmail(token) != 0) {
        model.addObject("successNoty", messageSource.getMessage("admin.passwordproved", null, localeResolver.resolveLocale(request)));
      } else {
        model.addObject("errorNoty", messageSource.getMessage("admin.passwordnotproved", null, localeResolver.resolveLocale(request)));
      }
      model.setViewName("redirect:/dashboard");
    } catch (Exception e) {
      model.setViewName("DBError");
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/changeFinPasswordConfirm")
  public ModelAndView verifyEmailForFinPassword(HttpServletRequest request, @RequestParam("token") String token) {
    try {
      request.setCharacterEncoding("utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    ModelAndView model = new ModelAndView();
    try {
      if (userService.verifyUserEmail(token) != 0) {
        model.addObject("successNoty", messageSource.getMessage("admin.finpasswordproved", null, localeResolver.resolveLocale(request)));
      } else {
        model.addObject("errorNoty", messageSource.getMessage("admin.finpasswordnotproved", null, localeResolver.resolveLocale(request)));
      }
      model.setViewName("redirect:/dashboard");
    } catch (Exception e) {
      model.setViewName("DBError");
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/newIpConfirm")
  public ModelAndView verifyEmailForNewIp(@RequestParam("token") String token, HttpServletRequest req) {
    ModelAndView model = new ModelAndView();
    try {
      if (userService.verifyUserEmail(token) != 0) {
        req.getSession().setAttribute("successNoty", messageSource.getMessage("admin.newipproved", null, localeResolver.resolveLocale(req)));
      } else {
        req.getSession().setAttribute("errorNoty", messageSource.getMessage("admin.newipnotproved", null, localeResolver.resolveLocale(req)));
      }
      model.setViewName("redirect:/login");
    } catch (Exception e) {
      model.setViewName("DBError");
      e.printStackTrace();
    }
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

  @RequestMapping("/2a8fy7b07dxe44/userswallets")
  public ModelAndView showUsersWalletsSummary(Principal principal) {
    Integer requesterUserId = userService.getIdByEmail(principal.getName());
    Map<String, List<UserWalletSummaryDto>> mapUsersWalletsSummaryList = new LinkedHashMap<>();
    List<UserWalletSummaryDto> fullResult = walletService.getUsersWalletsSummaryForPermittedCurrencyList(requesterUserId);
    /**/
    List<UserWalletSummaryDto> allFiltered = getSublistForRole(fullResult, "ALL");
    List<UserWalletSummaryDto> adminFiltered = getSublistForRole(fullResult, "ADMIN");
    List<UserWalletSummaryDto> userFiltered = getSublistForRole(fullResult, "USER");
    List<UserWalletSummaryDto> exchangeFiltered = getSublistForRole(fullResult, "EXCHANGE");
    List<UserWalletSummaryDto> vipUserFiltered = getSublistForRole(fullResult, "VIP_USER");
    List<UserWalletSummaryDto> traderFiltered = getSublistForRole(fullResult, "TRADER");
    /**/
    mapUsersWalletsSummaryList.put("ALL", allFiltered);
    mapUsersWalletsSummaryList.put("ADMIN", adminFiltered);
    mapUsersWalletsSummaryList.put("USER", userFiltered);
    mapUsersWalletsSummaryList.put("EXCHANGE", exchangeFiltered);
    mapUsersWalletsSummaryList.put("VIP_USER", vipUserFiltered);
    mapUsersWalletsSummaryList.put("TRADER", traderFiltered);
    /**/
    ModelAndView model = new ModelAndView();
    model.setViewName("UsersWallets");
    model.addObject("mapUsersWalletsSummaryList", mapUsersWalletsSummaryList);
    Set<String> usersCurrencyPermittedList = new LinkedHashSet<String>() {{
      add("ALL");
    }};
    usersCurrencyPermittedList.addAll(currencyService.getCurrencyPermittedNameList(requesterUserId));
    model.addObject("usersCurrencyPermittedList", usersCurrencyPermittedList);
    List<String> operationDirectionList = Arrays.asList("ANY", InvoiceOperationDirection.REFILL.name(), InvoiceOperationDirection.WITHDRAW.name());
    model.addObject("operationDirectionList", operationDirectionList);

    return model;
  }

  private List<UserWalletSummaryDto> getSublistForRole(List<UserWalletSummaryDto> fullResult, String role) {
    List<Integer> realRoleList = userRoleService.getRealUserRoleIdByBusinessRoleList(role);
    List<UserWalletSummaryDto> roleFiltered = fullResult.stream()
        .filter(e -> realRoleList.isEmpty() || realRoleList.contains(e.getUserRoleId()))
        .collect(Collectors.toList());
    List<UserWalletSummaryDto> result = new ArrayList<>();
    for (UserWalletSummaryDto item : roleFiltered) {
      if (!result.contains(item)) {
        result.add(new UserWalletSummaryDto(item));
      } else {
        UserWalletSummaryDto storedItem = result.stream().filter(e -> e.equals(item)).findAny().get();
        storedItem.increment(item);
      }
    }
    result.forEach(UserWalletSummaryDto::calculate);
    return result;
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


 /* @RequestMapping(value = "/2a8fy7b07dxe44/invoiceConfirmation")
  public ModelAndView invoiceTransactions(Principal principal) {
    Integer requesterUserId = userService.getIdByEmail(principal.getName());
    return new ModelAndView("admin/transaction_invoice");
  }
*/

  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinConfirmation")
  public ModelAndView bitcoinTransactions() {
    return new ModelAndView("admin/transaction_bitcoin");
  }


  private BitcoinService findAnyBitcoinServiceBean() {
    return bitcoinLikeServices.entrySet().stream().findAny().orElseThrow(NoRequestedBeansFoundException::new).getValue();
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/sessionControl")
  public ModelAndView sessionControl() {
    return new ModelAndView("admin/sessionControl");
  }


  @RequestMapping(value = "/2a8fy7b07dxe44/userSessions")
  @ResponseBody
  public List<UserSessionDto> retrieveUserSessionInfo() {
    List<UserSessionDto> result = null;
    try {
      Map<String, String> usersSessions = sessionRegistry.getAllPrincipals().stream()
          .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
          .collect(Collectors.toMap(SessionInformation::getSessionId, sessionInformation -> {
            UserDetails user = (UserDetails) sessionInformation.getPrincipal();
            return user.getUsername();
          }));
      log.debug("USsize ", + usersSessions.size());
      Map<String, UserSessionInfoDto> userSessionInfo = userService.getUserSessionInfo(usersSessions.values().stream().collect(Collectors.toSet()))
          .stream().collect(Collectors.toMap(UserSessionInfoDto::getUserEmail, userSessionInfoDto -> userSessionInfoDto));
      log.debug("USinfosize ", + userSessionInfo.size());
      result = usersSessions.entrySet().stream()
          .map(entry -> {
            UserSessionDto dto = new UserSessionDto(userSessionInfo.get(entry.getValue()), entry.getKey());
            return dto;
          }).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("session_error {}", e);
    }
    return result;
  }

  @AdminLoggable
  @RequestMapping(value = "/2a8fy7b07dxe44/expireSession", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> expireSession(@RequestParam String sessionId) {
    SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
    if (sessionInfo == null) {
      return new ResponseEntity<>("Sesion not found", HttpStatus.NOT_FOUND);
    }
    sessionInfo.expireNow();
    return new ResponseEntity<>("Session " + sessionId + " expired", HttpStatus.OK);
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
  public ResponseEntity<Void> editCurrencyLimit(@RequestParam int currencyId,
                                                @RequestParam OperationType operationType,
                                                @RequestParam String roleName,
                                                @RequestParam BigDecimal minAmount,
                                                @RequestParam Integer maxDailyRequest) {

    currencyService.updateCurrencyLimit(currencyId, operationType, roleName, minAmount, maxDailyRequest);
    return new ResponseEntity<>(HttpStatus.OK);
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
    sessionRegistry.getAllPrincipals().stream()
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
    return merchantService.findMerchantCurrencyOptions(Collections.emptyList());

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
  public ResponseEntity<Void> editMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
    commissionService.updateMerchantCommission(editMerchantCommissionDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editMerchantCommission/toggleSubtractWithdraw", method = RequestMethod.POST)
  @ResponseBody
  public void toggleSubtractMerchantCommissionForWithdraw(@RequestParam Integer merchantId, @RequestParam Integer currencyId,
                                                                          @RequestParam Boolean subtractMerchantCommissionForWithdraw) {
    merchantService.toggleSubtractMerchantCommissionForWithdraw(merchantId, currencyId, subtractMerchantCommissionForWithdraw);
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
  @RequestMapping(value = "/2a8fy7b07dxe44/phrases/{topic:.+}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<String>> getPhrases(
      @PathVariable String topic,
      @RequestParam String email) {
    Locale userLocale = Locale.forLanguageTag(userService.getPreferedLangByEmail(email));
    UserCommentTopicEnum userCommentTopic = UserCommentTopicEnum.convert(topic.toUpperCase());
    List<String> phrases = phraseTemplateService.getAllByTopic(userCommentTopic).stream()
        .map(e -> messageSource.getMessage(e, null, userLocale))
        .collect(Collectors.toList());
    return new HashMap<String, List<String>>() {{
      put("lang", Arrays.asList(userLocale.getLanguage()));
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
    return new ModelAndView("/admin/candleTable", "currencyPairs", currencyService.getAllCurrencyPairs());
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
    String currency = merchantService.retrieveCoreWalletCurrencyNameByMerchant(merchantName);
    modelAndView.addObject("currency", currency);
    modelAndView.addObject("title", messageSource.getMessage(currency.toLowerCase() + "Wallet.title", null, locale));
    modelAndView.addObject("walletInfo", getBitcoinServiceByMerchantName(merchantName).getWalletInfo());
    return modelAndView;
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transactions", method = RequestMethod.GET)
  @ResponseBody
  public List<BtcTransactionHistoryDto> getBtcTransactions(@PathVariable String merchantName) {
    return getBitcoinServiceByMerchantName(merchantName).listAllTransactions();
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/estimatedFee", method = RequestMethod.GET)
  @ResponseBody
  public BigDecimal getEstimatedFee(@PathVariable String merchantName) {
    return getBitcoinServiceByMerchantName(merchantName).estimateFee();
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
      modelAndView.addObject("currencyPairs", currencyService.getAllCurrencyPairs());
    });
    return modelAndView;
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
    defaultRoleFilter.putAll(Stream.of(UserRole.values()).filter(value -> value != ROLE_CHANGE_PASSWORD)
            .collect(Collectors.toMap(value -> value, value -> false)));
    userRoleService.getRolesUsingRealMoney().forEach(role -> defaultRoleFilter.replace(role, true));

    return new ModelAndView("admin/generalStats", "defaultRoleFilter", defaultRoleFilter);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/newUsers", method = GET)
  public Integer getNewUsersNumber(@RequestParam("startTime") String startTimeString,
                                   @RequestParam("endTime") String endTimeString) {
    String dateTimePattern = "yyyy-MM-dd_HH:mm";
    LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
    LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));
    return userService.getNewRegisteredUserNumber(startTime, endTime);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/getSubtractFeeStatus", method = GET)
  public Boolean getSubtractFeeFromAmount(@PathVariable String merchantName) {
    BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
    return walletService.getSubtractFeeFromAmount();
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
    model.addAttribute("currencies", currencyService.findAllCurrenciesWithHidden());
    return "admin/refill_addresses";
  }

  @ResponseBody
  @GetMapping(value = "/2a8fy7b07dxe44/refillAddresses/table")
  public PagingData<List<RefillRequestAddressShortDto>> getRefillAddressesTable(DataTableParams dataTableParams,
                                                                                RefillAddressfilterData filterData) {
    return refillService.getAdressesShortDto(dataTableParams, filterData);
  }



  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(OrderDeletingException.class)
  @ResponseBody
  public ErrorInfo OrderDeletingExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(NoPermissionForOperationException.class)
  @ResponseBody
  public ErrorInfo userNotEnabledExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
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
    ApplicationContext context = new AnnotationConfigApplicationContext(WebAppConfig.class);

    WavesRestClient restClient = (WavesRestClient) context.getBean("wavesRestClientImpl");
    System.out.println(restClient.generateNewAddress());


  }
  


}