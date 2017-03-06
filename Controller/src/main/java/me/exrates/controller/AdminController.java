package me.exrates.controller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.form.AuthorityOptionsForm;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.*;
import me.exrates.service.exception.NoPermissionForOperationException;
import me.exrates.service.exception.OrderDeletingException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.UserCommentTopicEnum.GENERAL;
import static me.exrates.model.enums.UserRole.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AdminController {

  private static final Logger LOG = LogManager.getLogger(AdminController.class);
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private UserSecureServiceImpl userSecureService;
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
  private InvoiceService invoiceService;
  @Autowired
  private BitcoinService bitcoinService;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private PhraseTemplateService phraseTemplateService;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  @Qualifier("ExratesSessionRegistry")
  private SessionRegistry sessionRegistry;

  @RequestMapping(value = {"/2a8fy7b07dxe44", "/2a8fy7b07dxe44/users"})
  public ModelAndView admin(Principal principal, HttpSession httpSession) {

    final Object mutex = WebUtils.getSessionMutex(httpSession);
    synchronized (mutex) {
      httpSession.setAttribute("currentRole", ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority());
    }

    ModelAndView model = new ModelAndView();
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    model.addObject("currencyPairList", currencyPairList);
    model.setViewName("admin/admin");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/administrators", method = GET)
  public String administrators() {
    return "admin/administrators";
  }

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
    model.setViewName("admin/order_delete");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCmnRefRoot", method = POST)
  @ResponseBody
  public ResponseEntity<Void> editCommonReferralRoot(final @RequestParam("id") int id) {
    userService.updateCommonReferralRoot(id);
    return new ResponseEntity<>(OK);
  }

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
    params.forEach((key, value) -> LOG.debug(key + " :: " + value));
    List<UserRole> userRoles = asList(USER, EXCHANGE, VIP_USER);
    return userSecureService.getUsersByRolesPaginated(userRoles, params);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/admins", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<User> getAllAdmins() {
    List<UserRole> adminRoles = new ArrayList<>();
    adminRoles.add(UserRole.ADMINISTRATOR);
    adminRoles.add(UserRole.ACCOUNTANT);
    adminRoles.add(UserRole.ADMIN_USER);
    return userSecureService.getUsersByRoles(adminRoles);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/transactions", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataTable<List<OperationViewDto>> getUserTransactions(final @RequestParam(required = false) int id,
                                                               final @RequestParam(required = false) Integer status,
                                                               final @RequestParam(required = false) String[] type,
                                                               final @RequestParam(required = false) Integer[] merchant,
                                                               final @RequestParam(required = false) String startDate,
                                                               final @RequestParam(required = false) String endDate,
                                                               final @RequestParam(required = false) BigDecimal amountFrom,
                                                               final @RequestParam(required = false) BigDecimal amountTo,
                                                               final @RequestParam(required = false) BigDecimal commissionAmountFrom,
                                                               final @RequestParam(required = false) BigDecimal commissionAmountTo,
                                                               final @RequestParam Map<String, String> params,
                                                               final HttpServletRequest request) {

    Integer transactionStatus = status == null || status == -1 ? null : status;
    List<TransactionType> types = type == null ? null :
        Arrays.stream(type).map(TransactionType::valueOf).collect(Collectors.toList());
    List<Integer> merchantIds = merchant == null ? null : Arrays.asList(merchant);
    return transactionService.showUserOperationHistory(id, transactionStatus, types, merchantIds, startDate, endDate,
        amountFrom, amountTo, commissionAmountFrom, commissionAmountTo, localeResolver.resolveLocale(request), params);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<Wallet> getUserWallets(@RequestParam int id, HttpServletRequest request) {
    return walletService.getAllWallets(id);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/comments", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<Comment> getUserComments(@RequestParam int id, HttpServletRequest request) {

    return userService.getUserComments(id);
  }

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
    Map<String, List<OrderWideListDto>> resultMap = new HashMap<>();

    List<OrderWideListDto> result = new ArrayList<>();
    switch (tableType) {
      case "ordersBuyClosed":
        List<OrderWideListDto> ordersBuyClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyClosed;
        break;
      case "ordersSellClosed":
        List<OrderWideListDto> ordersSellClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellClosed;
        break;
      case "ordersBuyOpened":
        List<OrderWideListDto> ordersBuyOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyOpened;
        break;
      case "ordersSellOpened":
        List<OrderWideListDto> ordersSellOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellOpened;
        break;
      case "ordersBuyCancelled":
        List<OrderWideListDto> ordersBuyCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyCancelled;
        break;
      case "ordersSellCancelled":
        List<OrderWideListDto> ordersSellCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellCancelled;
        break;
    }

    return result;
  }

  @RequestMapping("/2a8fy7b07dxe44/addUser")
  public ModelAndView addUser(HttpSession httpSession) {

    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (!currentRole.equals(UserRole.ADMINISTRATOR.name())) {
      return new ModelAndView("403");
    }
    ModelAndView model = new ModelAndView();

    model.addObject("roleList", userService.getAllRoles());
    User user = new User();
    model.addObject("user", user);
    model.setViewName("admin/addUser");

    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/adduser/submit", method = RequestMethod.POST)
  public ModelAndView submitcreate(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request,
                                   HttpSession httpSession) {

    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (!currentRole.equals(UserRole.ADMINISTRATOR.name())) {
      return new ModelAndView("403");
    }

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

  @RequestMapping({"/2a8fy7b07dxe44/editUser", "/2a8fy7b07dxe44/userInfo"})
  public ModelAndView editUser(@RequestParam int id, HttpSession httpSession, HttpServletRequest request) {

    ModelAndView model = new ModelAndView();

    model.addObject("statusList", UserStatus.values());
    List<UserRole> roleList = new ArrayList<>();
    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (currentRole.equals(UserRole.ADMINISTRATOR.name())) {
      roleList = userService.getAllRoles();
    }
    model.addObject("roleList", roleList);
    User user = userService.getUserById(id);
    user.setId(id);
    model.addObject("user", user);
    model.addObject("currencies", currencyService.findAllCurrencies().stream()
        .filter(currency -> !"LTC".equals(currency.getName())).collect(Collectors.toList()));
    model.addObject("currencyPairs", currencyService.getAllCurrencyPairs());
    model.setViewName("admin/editUser");
    model.addObject("userFiles", userService.findUserDoc(id));
    model.addObject("transactionTypes", Arrays.asList(TransactionType.values()));
    List<Merchant> merchantList = merchantService.findAll();
    model.addObject("merchants", merchantList);
    model.addObject("maxAmount", transactionService.maxAmount());
    model.addObject("maxCommissionAmount", transactionService.maxCommissionAmount());
    Set<String> allowedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    AuthorityOptionsForm form = new AuthorityOptionsForm();
    form.setUserId(id);
    form.setOptions(userService.getAuthorityOptionsForUser(id, allowedAuthorities, localeResolver.resolveLocale(request)));
    model.addObject("authorityOptionsForm", form);
    model.addObject("userLang", userService.getPreferedLang(id).toUpperCase());
    model.addObject("usersInvoiceRefillCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), REFILL));
    model.addObject("usersInvoiceWithdrawCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), WITHDRAW));

    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/edituser/submit", method = RequestMethod.POST)
  public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, HttpServletResponse response,
                                 HttpSession httpSession) {
    LOG.debug(user.getRole());
    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && user.getRole() == ADMINISTRATOR) {
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
      if (currentRole.equals(ADMINISTRATOR.name())) {
        model.addObject("roleList", userService.getAllRoles());
      }
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setEmail(user.getEmail());
      updateUserDto.setPassword(user.getPassword());
      updateUserDto.setPhone(user.getPhone());
      if (currentRole.equals(UserRole.ADMINISTRATOR.name())) {
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
    LOG.debug(sessionRegistry.getAllPrincipals().size());
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

  @RequestMapping(value = "/2a8fy7b07dxe44/withdrawal")
  public ModelAndView withdrawalRequests(Principal principal) {
    final List<UserRole> admins = new ArrayList<>();
    admins.addAll(asList(UserRole.ADMINISTRATOR, UserRole.ACCOUNTANT));
    final Map<String, Object> params = new HashMap<>();
    params.put("admins", admins);
    List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.findPermittedCurrenciesForWithdraw(principal.getName())
            .stream().filter(dto -> dto.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE).collect(Collectors.toList());
    params.put("currencies", permittedCurrencies);
    List<Merchant> merchants = merchantService.findAllByCurrencies(permittedCurrencies.stream()
            .map(UserCurrencyOperationPermissionDto::getCurrencyId).collect(Collectors.toList()), OperationType.OUTPUT).stream()
            .map(item -> new Merchant(item.getMerchantId(), item.getName(), item.getDescription(), null))
            .distinct().collect(Collectors.toList());
    params.put("merchants", merchants);
    return new ModelAndView("withdrawalRequests", params);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/withdrawRequests", method = GET)
  @ResponseBody
  public DataTable<List<WithdrawRequest>> findRequestByStatus(@RequestParam("status") Integer requestStatus, WithdrawFilterData withdrawFilterData,
                                                              @RequestParam Map<String, String> params, Principal principal) {
    DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
    withdrawFilterData.initFilterItems();
    return merchantService.findWithdrawRequestsByStatus(requestStatus, dataTableParams, withdrawFilterData, principal.getName());
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/orderinfo", method = RequestMethod.GET)
  public OrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
    return orderService.getOrderInfo(id, localeResolver.resolveLocale(request));
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/orderdelete", method = RequestMethod.POST)
  public Integer deleteOrderByAdmin(@RequestParam int id) {
    try {
      return orderService.deleteOrderByAdmin(id);
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

  @RequestMapping("/2a8fy7b07dxe44/userswallets")
  public ModelAndView showUsersWalletsSummary() {

    Map<String, List<UserWalletSummaryDto>> mapUsersWalletsSummaryList = new LinkedHashMap<>();
    mapUsersWalletsSummaryList.put("ALL", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.getRealUserRoleIdList("ALL")));
    mapUsersWalletsSummaryList.put("ADMIN", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.ADMIN.getRealUserRoleIdList()));
    mapUsersWalletsSummaryList.put("USER", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.USER.getRealUserRoleIdList()));
    mapUsersWalletsSummaryList.put("EXCHANGE", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.EXCHANGE.getRealUserRoleIdList()));
    mapUsersWalletsSummaryList.put("VIP_USER", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.VIP_USER.getRealUserRoleIdList()));
    mapUsersWalletsSummaryList.put("TRADER", walletService.getUsersWalletsSummary(BusinessUserRoleEnum.TRADER.getRealUserRoleIdList()));

    ModelAndView model = new ModelAndView();
    model.setViewName("UsersWallets");
    model.addObject("mapUsersWalletsSummaryList", mapUsersWalletsSummaryList);

    return model;
  }


  @RequestMapping(value = "/2a8fy7b07dxe44/downloadUsersWalletsSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
  @ResponseBody
  public String getUsersWalletsSummeryTxt(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String role) {
    return
        UserSummaryDto.getTitle() +
            userService.getUsersSummaryList(startDate, endDate, BusinessUserRoleEnum.getRealUserRoleIdList(role))
                .stream()
                .map(e -> e.toString())
                .collect(Collectors.joining());
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/downloadUsersWalletsSummaryInOut", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
  @ResponseBody
  public String getUsersWalletsSummeryInOut(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String role) {
    String value = UserSummaryInOutDto.getTitle() +
        userService.getUsersSummaryInOutList(startDate, endDate, BusinessUserRoleEnum.getRealUserRoleIdList(role))
            .stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());

    return value;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/downloadUserSummaryOrders", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
  @ResponseBody
  public String getUserSummaryOrders(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String role) {

    List<UserSummaryOrdersDto> list = userService.getUserSummaryOrdersList(startDate, endDate, BusinessUserRoleEnum.getRealUserRoleIdList(role));
    BigDecimal sumAmountBuy = new BigDecimal(0.00);
    BigDecimal sumAmountBuyFee = new BigDecimal(0.00);
    BigDecimal sumAmountSell = new BigDecimal(0.00);
    BigDecimal sumAmountSellFee = new BigDecimal(0.00);

    String value = "Orders from " + startDate.substring(0, 10) + " till " + endDate.substring(0, 10) + ": \n \n" + UserSummaryOrdersDto.getTitle() +
        list.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());

    for (UserSummaryOrdersDto userSummaryOrdersDto : list) {
      if (userSummaryOrdersDto.getAmountBuy() != null) {
        sumAmountBuy = sumAmountBuy.add(userSummaryOrdersDto.getAmountBuy());
      }
      if (userSummaryOrdersDto.getAmountBuyFee() != null) {
        sumAmountBuyFee = sumAmountBuyFee.add(userSummaryOrdersDto.getAmountBuyFee());
      }
      if (userSummaryOrdersDto.getAmountSell() != null) {
        sumAmountSell = sumAmountSell.add(userSummaryOrdersDto.getAmountSell());
      }
      if (userSummaryOrdersDto.getAmountSellFee() != null) {
        sumAmountSellFee = sumAmountSellFee.add(userSummaryOrdersDto.getAmountSellFee());
      }
    }
    value += "\n sumBuy: " + sumAmountBuy.toString() + "\n sumBuyFee: " + sumAmountBuyFee.toString();
    value += "\n sumSell: " + sumAmountSell.toString() + "\n sumSellFee: " + sumAmountSellFee.toString();

    return value;
  }

    @RequestMapping(value = "/2a8fy7b07dxe44/downloadUserSummaryOrdersByCurrencyPairs", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUserSummaryOrdersByCurrencyPairs(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String role) {

        List<UserSummaryOrdersByCurrencyPairsDto> list = userService.getUserSummaryOrdersByCurrencyPairList(startDate, endDate, BusinessUserRoleEnum.getRealUserRoleIdList(role));

        String value = "Orders by currency pairs from" + startDate.substring(0,10) + " till " + endDate.substring(0,10) + ": \n \n" + UserSummaryOrdersByCurrencyPairsDto.getTitle() +
                list.stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());

        return value;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/downloadUsersWalletsSummaryTotalInOut", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUsersWalletsSummeryTotalInOut(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String role) {
        String value = UserSummaryTotalInOutDto.getTitle() +
                userService.getUsersSummaryTotalInOutList(startDate, endDate, BusinessUserRoleEnum.getRealUserRoleIdList(role))
                        .stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());

    return value;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/userStatements/{walletId}")
  public ModelAndView accountStatementPage(@PathVariable("walletId") Integer walletId) {
    return new ModelAndView("/admin/user_statement", "walletId", walletId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getStatements", method = RequestMethod.GET)
  @ResponseBody
  public DataTable<List<AccountStatementDto>> getStatements(@RequestParam Integer walletId, @RequestParam Map<String, String> params,
                                                            HttpServletRequest request) {
    Integer offset = Integer.parseInt(params.getOrDefault("start", "0"));
    Integer limit = Integer.parseInt(params.getOrDefault("length", "-1"));
    return transactionService.getAccountStatementForAdmin(walletId, offset, limit, localeResolver.resolveLocale(request));
  }


  @RequestMapping(value = "/2a8fy7b07dxe44/invoiceConfirmation")
  public ModelAndView invoiceTransactions(HttpSession httpSession) {
    return new ModelAndView("admin/transaction_invoice", "invoiceRequests", invoiceService.findAllInvoiceRequests());
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/invoiceRequests")
  @ResponseBody
  public List<InvoiceRequest> invoiceRequests(
      @RequestParam(required = false) List<String> availableActionSet) {
    if (availableActionSet == null || availableActionSet.isEmpty()) {
      return invoiceService.findAllInvoiceRequests();
    } else {
      List<InvoiceActionTypeEnum> invoiceActionTypeEnumList = InvoiceActionTypeEnum.convert(availableActionSet);
      List<InvoiceStatus> invoiceRequestStatusList = InvoiceRequestStatusEnum.getAvailableForActionStatusesList(invoiceActionTypeEnumList);
      List<Integer> invoiceRequestStatusIdList = invoiceRequestStatusList.stream()
          .map(InvoiceStatus::getCode)
          .collect(Collectors.toList());
      return invoiceService.findAllByStatus(invoiceRequestStatusIdList);
    }
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/invoiceRequests/{status}")
  @ResponseBody
  public List<InvoiceRequest> invoiceRequestsByStatus(@PathVariable String status) {
    return invoiceService.findAllByStatus(Collections.singletonList(InvoiceRequestStatusEnum.convert(status).getCode()));

  }



  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinConfirmation")
  public ModelAndView bitcoinTransactions() {
    return new ModelAndView("admin/transaction_bitcoin");
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinRequests")
  @ResponseBody
  public List<PendingPaymentFlatDto> getBitcoinRequests() {
    return bitcoinService.getBitcoinTransactions();
  }
  
  
  

  @RequestMapping(value = "/2a8fy7b07dxe44/sessionControl")
  public ModelAndView sessionControl() {
    return new ModelAndView("admin/sessionControl");
  }


  @RequestMapping(value = "/2a8fy7b07dxe44/userSessions")
  @ResponseBody
  public List<UserSessionDto> retrieveUserSessionInfo() {
    Map<String, String> usersSessions = sessionRegistry.getAllPrincipals().stream()
        .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
        .collect(Collectors.toMap(SessionInformation::getSessionId, sessionInformation -> {
          UserDetails user = (UserDetails) sessionInformation.getPrincipal();
          return user.getUsername();
        }));
    Map<String, UserSessionInfoDto> userSessionInfo = userService.getUserSessionInfo(usersSessions.values().stream().collect(Collectors.toSet()))
        .stream().collect(Collectors.toMap(UserSessionInfoDto::getUserEmail, userSessionInfoDto -> userSessionInfoDto));
    List<UserSessionDto> result = usersSessions.entrySet().stream()
        .map(entry -> {
          UserSessionDto dto = new UserSessionDto(userSessionInfo.get(entry.getValue()), entry.getKey());
          return dto;
        }).collect(Collectors.toList());
    return result;
  }

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
    return modelAndView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/retrieve", method = RequestMethod.GET)
  @ResponseBody
  public List<CurrencyLimit> retrieveCurrencyLimits(@RequestParam String roleName,
                                                    @RequestParam OperationType operationType) {
    return currencyService.retrieveCurrencyLimitsForRole(roleName, operationType);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editCurrencyLimit(@RequestParam int currencyId,
                                                @RequestParam OperationType operationType,
                                                @RequestParam String roleName,
                                                @RequestParam BigDecimal minAmount) {

    currencyService.updateCurrencyLimit(currencyId, operationType, roleName, minAmount);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editAuthorities/submit", method = RequestMethod.POST)
  public RedirectView editAuthorities(@ModelAttribute AuthorityOptionsForm authorityOptionsForm, Principal principal,
                                      RedirectAttributes redirectAttributes) {
    LOG.debug(authorityOptionsForm.getOptions());
    LOG.debug(authorityOptionsForm.getUserId());
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

  @RequestMapping(value = "/2a8fy7b07dxe44/changeActiveBalance/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> changeActiveBalance(@RequestParam Integer userId, @RequestParam("currency") Integer currencyId,
                                                  @RequestParam BigDecimal amount) {
    LOG.debug("userId = " + userId + ", currencyId = " + currencyId + "? amount = " + amount);
    walletService.manualBalanceChange(userId, currencyId, amount);
    return new ResponseEntity<>(HttpStatus.OK);

  }


  @RequestMapping(value = "/2a8fy7b07dxe44/commissions", method = RequestMethod.GET)
  public ModelAndView commissions() {
    ModelAndView modelAndView = new ModelAndView("admin/editCommissions");
    modelAndView.addObject("roleNames", BusinessUserRoleEnum.values());
    return modelAndView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getCommissionsForRole", method = RequestMethod.GET)
  @ResponseBody
  public List<CommissionShortEditDto> retrieveCommissionsForRole(@RequestParam String role, HttpServletRequest request) {
    return commissionService.getEditableCommissionsByRole(role, localeResolver.resolveLocale(request));

  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getMerchantCommissions", method = RequestMethod.GET)
  @ResponseBody
  public List<MerchantCurrencyOptionsDto> retrieveMerchantCommissions() {
    return merchantService.findMerchantCurrencyOptions();

  }

  @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editCommission", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editCommission(@RequestParam("operationType") OperationType operationType,
                                             @RequestParam("userRole") String role,
                                             @RequestParam("commissionValue") BigDecimal value) {
    LOG.debug("operationType = " + operationType + ", userRole = " + role + ", value = " + value);
    commissionService.updateCommission(operationType, role, value);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editMerchantCommission", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
    commissionService.updateMerchantCommission(editMerchantCommissionDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess", method = RequestMethod.GET)
  public ModelAndView merchantAccess() {
    List<MerchantCurrencyOptionsDto> merchantCurrencyOptions = merchantService.findMerchantCurrencyOptions();
    LOG.debug(merchantCurrencyOptions);
    return new ModelAndView("admin/merchantAccess", "merchantCurrencies", merchantCurrencyOptions);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/toggleBlock", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> toggleBlock(@RequestParam Integer merchantId,
                                          @RequestParam Integer currencyId,
                                          @RequestParam OperationType operationType) {
    LOG.debug("merchantId = " + merchantId + ", currencyId = " + currencyId + ", operationType = " + operationType);
    merchantService.toggleMerchantBlock(merchantId, currencyId, operationType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

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
      put("lang", Arrays.asList(userLocale.getISO3Language()));
      put("list", phrases);
    }};
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyPermissions/submit", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public void editCurrencyPermissions(
      @RequestBody List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList,
      HttpSession httpSession,
      Principal principal) {
    String currentRole = (String) httpSession.getAttribute("currentRole");
    if (!currentRole.equals(UserRole.ADMINISTRATOR.name())) {
      throw new NoPermissionForOperationException();
  }
    userService.setCurrencyPermissionsByUserId(userCurrencyOperationPermissionDtoList);
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

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    LOG.error(exception);
    exception.printStackTrace();
    return new ErrorInfo(req.getRequestURL(), exception);
  }


}