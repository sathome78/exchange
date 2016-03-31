package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    UserSecureServiceImpl userSecureService;

    @Autowired
    UserService userService;

    @Autowired
    RegisterFormValidation registerFormValidation;

    @Autowired
    LocaleResolver localeResolver;

    private String currentRole;

    @RequestMapping("/admin")
    public ModelAndView admin(Principal principal) {

        currentRole = ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority();

        ModelAndView model = new ModelAndView();
        List<UserRole> adminRoles = new ArrayList<>();
        adminRoles.add(UserRole.ADMINISTRATOR);
        adminRoles.add(UserRole.ACCOUNTANT);
        adminRoles.add(UserRole.ADMIN_USER);
        List<User> adminUsers = userSecureService.getUsersByRoles(adminRoles);
        model.addObject("adminUsers", adminUsers);


        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        List<User> userUsers = userSecureService.getUsersByRoles(userRoles);
        model.addObject("userUsers", userUsers);
        model.setViewName("admin/admin");

        return model;
    }

    @RequestMapping("/admin/addUser")
    public ModelAndView addUser() {

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

    @RequestMapping(value = "/admin/adduser/submit", method = RequestMethod.POST)
    public ModelAndView submitcreate(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

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
            model.setViewName("redirect:/admin");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping("/admin/editUser")
    public ModelAndView editUser(@RequestParam int id) {

        ModelAndView model = new ModelAndView();

        model.addObject("statusList", UserStatus.values());
        List<UserRole> roleList = new ArrayList<>();
        if (currentRole.equals(UserRole.ADMIN_USER.name()) || currentRole.equals(UserRole.ACCOUNTANT.name())) {
            roleList.add(UserRole.USER);
        } else {
            roleList = userService.getAllRoles();
        }
        model.addObject("roleList", roleList);
        User user = userService.getUserById(id);
        if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && !user.getRole().name().equals(UserRole.USER.name())) {
            return new ModelAndView("403");
        }
        user.setId(id);
        model.addObject("user", user);
        model.setViewName("admin/editUser");

        return model;
    }

    @RequestMapping(value = "/admin/edituser/submit", method = RequestMethod.POST)
    public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

        if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && !user.getRole().name().equals(UserRole.USER.name())) {
            return new ModelAndView("403");
        }
        user.setConfirmPassword(user.getPassword());
        if (user.getFinpassword() == null) {
            user.setFinpassword("");
        }
        registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("statusList", UserStatus.values());
            model.addObject("roleList", userService.getAllRoles());
            model.setViewName("admin/editUser");
        } else {
            userService.updateUserByAdmin(user);
            model.setViewName("redirect:/admin");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping("/settings")
    public ModelAndView settings(Principal principal) {

        ModelAndView model = new ModelAndView();

        User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        model.addObject("user", user);
        model.setViewName("settings");

        return model;
    }

    @RequestMapping(value = "settings/changePassword/submit", method = RequestMethod.POST)
    public ModelAndView submitsettingsPassword(@Valid @ModelAttribute User user, BindingResult result,
                                               ModelAndView model, HttpServletRequest request) {

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
        } else {
            userService.update(user, true, false, false, localeResolver.resolveLocale(request));
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "settings/changeFinPassword/submit", method = RequestMethod.POST)
    public ModelAndView submitsettingsFinPassword(@Valid @ModelAttribute User user, BindingResult result,
                                                  ModelAndView model, HttpServletRequest request) {

        registerFormValidation.validateResetFinPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
        } else {
            userService.update(user, false, true, false, localeResolver.resolveLocale(request));
            model.setViewName("redirect:/mywallets");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "/changePasswordConfirm")
    public ModelAndView verifyEmail(WebRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            userService.verifyUserEmail(token);
            model.setViewName("RegistrationConfirmed");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }


}
