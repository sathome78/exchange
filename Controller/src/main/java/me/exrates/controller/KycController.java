package me.exrates.controller;

import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.dao.KycDao;
import me.exrates.controller.validator.KycValidator;
import me.exrates.model.kyc.*;
import me.exrates.service.KycService;
import me.exrates.service.UserService;
import me.exrates.service.WorldCheckService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@PropertySource("classpath:/uploadfiles.properties")
public class KycController {

    private static final Logger LOG = LogManager.getLogger(KycController.class);

    private @Value("${upload.kycFilesDir}") String kycFilesDir;

    @Autowired
    private KycDao kycDao;
    @Autowired
    private KycService kycService;
    @Autowired
    private WorldCheckService worldCheckService;
    @Autowired
    private UserService userService;
    @Autowired
    private KycValidator validator;
    @Autowired
    private MessageSource messageSource;

    @PostMapping(value = "/kyc/save")
    public ModelAndView saveKyc(@ModelAttribute KYC kyc, Principal principal, Locale locale,
                                BindingResult result, RedirectAttributes attributes) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/settings");
        attributes.addFlashAttribute("activeTabId", "kyc-options-wrapper");

        validator.validate(kyc, result, locale);
        if (result.hasErrors()) {
            attributes.addFlashAttribute("kyc", kyc);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.kyc", result);
            return mav;
        }

        int userId = userService.findByEmail(principal.getName()).getId();
        if (kycDao.inProgress(userId)) {
            kycService.update(userId, kyc);
        } else {
            kycService.save(userId, kyc);
        }
        attributes.addFlashAttribute("successNoty", "Data has been successfully saved.");
        return mav;
    }

    @PostMapping(value = "/kyc/sendForApprove")
    public ModelAndView sendForApprove(@ModelAttribute KYC kyc, BindingResult result, RedirectAttributes attributes,
                                       Principal principal, Locale locale) {
        LOG.info("Saving KYC data for approve: " + kyc.toString());

        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/settings");
        attributes.addFlashAttribute("activeTabId", "kyc-options-wrapper");

        validator.validate(kyc, result, locale);
        if (result.hasErrors()) {
            attributes.addFlashAttribute("kyc", kyc);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.kyc", result);
            return mav;
        }

        int userId = userService.findByEmail(principal.getName()).getId();
        if (kycDao.getInfo(userId) == null) {
            kycService.save(userId, kyc);
        } else {
            kycService.update(userId, kyc);
        }
        kycService.sendKycForApprove(userId);
        attributes.addFlashAttribute("successNoty", messageSource.getMessage("kyc.SendSuccess", null, locale));
        return mav;
    }

    @AdminLoggable
    @PostMapping(value = "/2a8fy7b07dxe44/kyc/saveByAdmin")
    public ModelAndView saveKycByAdmin(@ModelAttribute KYC kyc, RedirectAttributes attributes, Locale locale) {
        kycService.updateByAdmin(kyc.getUserId(), kyc);
        attributes.addAttribute("userId", kyc.getUserId());
        attributes.addFlashAttribute("successNoty", messageSource.getMessage("kyc.SavedSuccess", null, locale));
        return new ModelAndView("redirect:/kyc/getKyc");
    }

    @AdminLoggable
    @GetMapping(value = "/2a8fy7b07dxe44/kyc/getKyc")
    public ModelAndView getKyc(@RequestParam int userId) {
        ModelAndView mav = new ModelAndView("/admin/kyc");
        mav.addObject("kyc", kycService.getKyc(userId));
        return mav;
    }

    @AdminLoggable
    @PutMapping(value = "/2a8fy7b07dxe44/kyc/approveKyc")
    public ResponseEntity approveKyc(@RequestParam int userId, Principal principal) {
        kycService.approveKyc(userId, principal.getName());
        Map<String, Object> body = new HashMap<>();
        body.put("info", kycDao.getInfo(userId));
        body.put("message", "KYC has been successfully approved for user with ID: " + userId);
        return ResponseEntity.ok().body(body);
    }

    @AdminLoggable
    @PutMapping(value = "/2a8fy7b07dxe44/kyc/rejectKyc")
    public ResponseEntity rejectKyc(@RequestParam int userId, Principal principal) {
        kycService.rejectKyc(userId, principal.getName());
        Map<String, Object> body = new HashMap<>();
        body.put("info", kycDao.getInfo(userId));
        body.put("message", "KYC has been rejected for user with ID: " + userId);
        return ResponseEntity.ok().body(body);
    }

    @AdminLoggable
    @GetMapping(value = "/2a8fy7b07dxe44/kyc/getWorldCheck")
    public ModelAndView getWorldCheck(@RequestParam int userId) {
        ModelAndView mav = new ModelAndView("/admin/world_check");
        WorldCheck worldCheck = worldCheckService.getWorldCheck(userId);
        if (worldCheck != null) {
            mav.addObject("wc", worldCheck);
        } else {
            WorldCheck wc = new WorldCheck();
            wc.setStatus(WorldCheckStatus.NOT_VERIFIED);
            wc.setUserId(userId);
            mav.addObject("wc", wc);
        }
        return mav;
    }

    @AdminLoggable
    @PutMapping(value = "/2a8fy7b07dxe44/kyc/approveWorldCheck")
    public ResponseEntity approveWorldCheck(@RequestParam int userId, Principal principal) {
        worldCheckService.approveWorldCheck(userId, principal.getName());
        Map<String, Object> body = new HashMap<>();
        body.put("info", worldCheckService.getWorldCheck(userId));
        body.put("message", "World Check has been successfully approved for user with ID: " + userId);
        return ResponseEntity.ok().body(body);
    }

    @AdminLoggable
    @PutMapping(value = "/2a8fy7b07dxe44/kyc/rejectWorldCheck")
    public ResponseEntity rejectWorldCheck(@RequestParam int userId, Principal principal) {
        worldCheckService.rejectWorldCheck(userId, principal.getName());
        Map<String, Object> body = new HashMap<>();
        body.put("info", worldCheckService.getWorldCheck(userId));
        body.put("message", "World Check has been rejected for user with ID: " + userId);
        return ResponseEntity.ok().body(body);
    }

    @GetMapping(value = "/kyc/docs/{fileName:.+}")
    public @ResponseBody byte[] getFile(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(kycFilesDir + fileName);
        return IOUtils.toByteArray(Files.newInputStream(path));
    }
}
