package me.exrates.controller;


import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.*;
import me.exrates.service.geetest.GeetestLib;
import me.exrates.service.session.UserSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

@Controller
@PropertySource("classpath:/captcha.properties")
public class DashboardController {
  private static final Logger LOG = LogManager.getLogger(DashboardController.class);


  @Autowired
  OrderService orderService;

  @Autowired
  CurrencyService currencyService;

  @Autowired
  DashboardService dashboardService;

  @Autowired
  UserService userService;

  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  CommissionService commissionService;

  @Autowired
  WalletService walletService;

  @Autowired
  RegisterFormValidation registerFormValidation;

  @Autowired
  MessageSource messageSource;

  @Autowired
  LocaleResolver localeResolver;

  @Autowired
  private UserSessionService userSessionService;

  @Autowired
  VerifyReCaptchaSec verifyReCaptcha;

  @Value("${captcha.type}")
  String CAPTCHA_TYPE;

  @Autowired
  private GeetestLib geetest;

  @RequestMapping(value = {"/dashboard/locale"})
  @ResponseBody
  public void localeSwitcherCommand(
      Principal principal,
      HttpServletRequest request,
      HttpServletResponse response) {
    Locale locale = localeResolver.resolveLocale(request);
    localeResolver.setLocale(request, response, locale);
    if (principal != null) {
      userService.setPreferedLang(userService.getIdByEmail(principal.getName()), localeResolver.resolveLocale(request));
    }
    request.getSession();
  }

  public static String convertLanguageNameToMenuFormat(String lang) {
    final Map<String, String> convertMap = new HashMap<String, String>() {{
      put("in", "id");
    }};
    String convertedLangName = convertMap.get(lang);
    return convertedLangName == null ? lang : convertedLangName;
  }

  @RequestMapping(value = "/forgotPassword")
  public String forgotPassword(Model model) {
    if (!model.containsAttribute("user")) {
      model.addAttribute("user", new User());
    }
    model.addAttribute("captchaType", CAPTCHA_TYPE);
    return "forgotPassword";
  }

  @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, RedirectAttributes attr) {
      String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
      String validate = request.getParameter(GeetestLib.fn_geetest_validate);
      String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);

      int gt_server_status_code = (Integer) request.getSession().getAttribute(geetest.gtServerStatusSessionKey);
      String userid = (String)request.getSession().getAttribute("userid");

      HashMap<String, String> param = new HashMap<>();
      param.put("user_id", userid);

      int gtResult = 0;
      if (gt_server_status_code == 1) {
          gtResult = geetest.enhencedValidateRequest(challenge, validate, seccode, param);
          LOG.info(gtResult);
      } else {
          LOG.error("failback:use your own server captcha validate");
          gtResult = geetest.failbackValidateRequest(challenge, validate, seccode);
          LOG.error(gtResult);
      }

      if (gtResult == 1) {
          registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
          if (result.hasErrors()) {
              //TODO
              throw new RuntimeException(result.toString());
          }
          String email = user.getEmail();
          user = userService.findByEmail(email);
          UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
          updateUserDto.setEmail(email);
          userService.update(updateUserDto, true, localeResolver.resolveLocale(request));

          Map<String, Object> body = new HashMap<>();
          body.put("result",messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request)));
          body.put("email", email);
          return ResponseEntity.ok(body);
      }
      else {
          //TODO
          throw new RuntimeException("Geetest error");
      }
  }

  @RequestMapping(value = "/passwordRecovery", method = RequestMethod.GET)
  public ModelAndView recoveryPassword(@ModelAttribute("user") User user) {
      ModelAndView model = new ModelAndView("fragments/recoverPassword");
      model.addObject("user", user);
    return model;
  }

    @RequestMapping(value = "/newDeviceConfirm")
    public ModelAndView newDeviceConfirm(@RequestParam("token") String token,
                                         @RequestParam("device") String device,
                                         @RequestParam("var") String var,
                                         RedirectAttributes attr, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        LocalDateTime currentDateTime = LocalDateTime.now();
        long currentMinutesOfDay = currentDateTime.getLong(ChronoField.MINUTE_OF_DAY);
        try {
            int userId = userService.verifyUserEmail(token);
            byte[] decodedUserDeviceBytes = Base64.getDecoder().decode(device);
            byte[] decodedDateTime = Base64.getDecoder().decode(var);
            String userDevice = new String(decodedUserDeviceBytes);
            long dateTimeInMinutes = Long.parseLong(new String(decodedDateTime));

            long difference = currentMinutesOfDay - dateTimeInMinutes;

            if (userId != 0){
                User user = userService.getUserById(userId);

                if (difference < 0) {
                    if((difference + 1440) < 30) {
                        userService.setNewOperSystem(user.getEmail(), userDevice);
                        attr.addFlashAttribute("successConfirm", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
                        attr.addFlashAttribute("user", user);
                    } else {
                        attr.addFlashAttribute("errorNoty", messageSource.getMessage("register.unsuccessfullyproved",null, localeResolver.resolveLocale(request)));
                    }
                } else if (difference < 30){
                    userService.setNewOperSystem(user.getEmail(), userDevice);
                    attr.addFlashAttribute("successConfirm", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("user", user);
                } else if(difference == 0){
                    userService.setNewOperSystem(user.getEmail(), userDevice);
                    attr.addFlashAttribute("successConfirm", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("user", user);
                } else {
                    attr.addFlashAttribute("errorNoty", messageSource.getMessage("register.unsuccessfullyproved",null, localeResolver.resolveLocale(request)));
                }
            } else {
                attr.addFlashAttribute("errorNoty", messageSource.getMessage("register.unsuccessfullyproved",null, localeResolver.resolveLocale(request)));
            }
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }

        model.setViewName("redirect:/dashboard");
        return model;
    }

  @RequestMapping(value = "/resetPasswordConfirm")
  public ModelAndView resetPasswordConfirm(@RequestParam("token") String token, @RequestParam("email") String email, RedirectAttributes attr, HttpServletRequest request) {
    ModelAndView model = new ModelAndView();
    try {
      int userId = userService.verifyUserEmail(token);
      if (userId != 0){
          User user = userService.getUserById(userId);

          attr.addFlashAttribute("recoveryConfirm", messageSource.getMessage("register.successfullyproved",
                  null, localeResolver.resolveLocale(request)));
          attr.addFlashAttribute("user", user);

          model.setViewName("redirect:/passwordRecovery");
      } else {
          if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser") || request.isUserInRole(UserRole.ROLE_CHANGE_PASSWORD.name())) {
              attr.addFlashAttribute("userEmail", email);
              attr.addFlashAttribute("recoveryError", messageSource.getMessage("dashboard.resetPasswordDoubleClick",null, localeResolver.resolveLocale(request)));
          } else {
              attr.addFlashAttribute("errorNoty", messageSource.getMessage("dashboard.resetPasswordDoubleClick",null, localeResolver.resolveLocale(request)));
          }
          return new ModelAndView(new RedirectView("/dashboard"));
      }
    } catch (Exception e) {
      model.setViewName("DBError");
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/dashboard/updatePassword", method = RequestMethod.POST)
  public ModelAndView updatePassword(@ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, RedirectAttributes attr, Locale locale) {
        /**/
    registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
      modelAndView.addObject("captchaType", CAPTCHA_TYPE);
      return modelAndView;
    } else {
      User userUpdate = userService.findByEmail(user.getEmail());
      ModelAndView model = new ModelAndView();
      UpdateUserDto updateUserDto = new UpdateUserDto(userUpdate.getId());
      updateUserDto.setPassword(user.getPassword());

      userService.updateUserByAdmin(updateUserDto);

      Collection<GrantedAuthority> authList = new ArrayList<>(userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
      org.springframework.security.core.userdetails.User userSpring =
              new org.springframework.security.core.userdetails.User(
                      user.getEmail(),
                      updateUserDto.getPassword(),
                      false,
                      false,
                      false,
                      false,
                      authList
              );
      Authentication auth = new UsernamePasswordAuthenticationToken(userSpring, null, authList);
      SecurityContextHolder.getContext().setAuthentication(auth);

      userSessionService.invalidateUserSessionExceptSpecific(user.getEmail(), RequestContextHolder.currentRequestAttributes().getSessionId());

      attr.addFlashAttribute("successNoty", messageSource.getMessage("login.passwordUpdateSuccess", null, locale));
      model.setViewName("redirect:/dashboard");
      return model;
    }
  }

  @RequestMapping(value = "/forgotPassword/submitUpdate", method = RequestMethod.POST)
  public ModelAndView submitUpdate(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
    registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        /**/
    if (result.hasErrors()) {
      model.addObject("user", user);
      model.addObject("captchaType", CAPTCHA_TYPE);
      model.setViewName("updatePassword");
      return model;
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setPassword(user.getPassword());
      userService.updateUserByAdmin(updateUserDto);
      model.setViewName("redirect:/dashboard");
    }
        /**/
    return model;
  }


}


