package me.exrates.controller;

import me.exrates.model.OpenApiToken;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.service.OpenApiTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/token")
public class TokenSettingsController {

    @Autowired
    private OpenApiTokenService openApiTokenService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;


    @ResponseBody
    @RequestMapping(value = "/findAll", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<OpenApiTokenPublicDto> getUserTokens(Principal principal) {
        return openApiTokenService.getUserTokens(principal.getName());
    }

    @RequestMapping(value = "/created", method = RequestMethod.GET)
    public ModelAndView tokenCreated(HttpServletRequest request) {
        Map<String, ?> flashAttributes = RequestContextUtils.getInputFlashMap(request);
        if (flashAttributes == null || !(flashAttributes.containsKey("publicKey") && flashAttributes.containsKey("privateKey"))) {
            return new ModelAndView("redirect:/settings");
        }
        return new ModelAndView("globalPages/tokenKey");
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView tokenCreate(@RequestParam String alias, RedirectAttributes redirectAttributes, HttpServletRequest request,
                                    Principal principal) {
        RedirectView redirectView = new RedirectView();
        try {
            OpenApiToken token = openApiTokenService.generateToken(principal.getName(), alias);
            redirectAttributes.addFlashAttribute("publicKey", token.getPublicKey());
            redirectAttributes.addFlashAttribute("privateKey", token.getPrivateKey());
            redirectView.setUrl("/settings/token/created");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("message.api.alias.error", null, localeResolver.resolveLocale(request)));
            redirectView.setUrl("/settings");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorNoty",  "Error " + e.getMessage());
            redirectView.setUrl("/settings");
        } finally {
            redirectAttributes.addFlashAttribute("activeTabId", "api-options-wrapper");
        }
        return redirectView;
    }

    @ResponseBody
    @RequestMapping(value = "/allowTrade", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void allowTrade(@RequestParam Long tokenId, @RequestParam Boolean allowTrade, Principal principal) {
        openApiTokenService.updateToken(tokenId, allowTrade, principal.getName());
    }

    @ResponseBody
    @RequestMapping(value = "/delete", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void deleteToken(@RequestParam Long tokenId, Principal principal) {
        openApiTokenService.deleteToken(tokenId, principal.getName());
    }


}
