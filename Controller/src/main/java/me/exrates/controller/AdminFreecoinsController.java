package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.freecoins.AdminGiveawayResultDto;
import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;
import me.exrates.service.freecoins.FreecoinsService;
import me.exrates.service.freecoins.FreecoinsSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
@Controller
public class AdminFreecoinsController {

    private final FreecoinsService freecoinsService;
    private final FreecoinsSettingsService freecoinsSettingsService;

    @Autowired
    public AdminFreecoinsController(FreecoinsService freecoinsService,
                                    FreecoinsSettingsService freecoinsSettingsService) {
        this.freecoinsService = freecoinsService;
        this.freecoinsSettingsService = freecoinsSettingsService;
    }

    @GetMapping(value = "/2a8fy7b07dxe44/free-coins/distributionListPage")
    public String getFreecoinsPage() {
        return "admin/distributionList";
    }

    @GetMapping("/2a8fy7b07dxe44/free-coins/giveaway/all")
    @ResponseBody
    public List<AdminGiveawayResultDto> getAllGiveaways() {
        return freecoinsService.getAllGiveawaysForAdmin();
    }

    @PostMapping("/2a8fy7b07dxe44/free-coins/revoke")
    @ResponseBody
    public Boolean processRevokeGiveaway(@RequestParam("giveaway_id") int giveawayId,
                                         @RequestParam("revoke_to_user") boolean revokeToUser) {
        return freecoinsService.processRevokeGiveaway(giveawayId, revokeToUser);
    }

    @GetMapping(value = "/2a8fy7b07dxe44/free-coins/currencyLimitsPage")
    public String getCurrencyLimitsPage() {
        return "admin/currencyLimitsForGiveaway";
    }

    @GetMapping("/2a8fy7b07dxe44/free-coins/settings")
    @ResponseBody
    public List<FreecoinsSettingsDto> getSettings() {
        return freecoinsSettingsService.getAll();
    }

    @PostMapping("/2a8fy7b07dxe44/free-coins/settings/update")
    @ResponseBody
    public Boolean updateSettings(@RequestParam("currency_id") int currencyId,
                                  @RequestParam("min_amount") BigDecimal minAmount,
                                  @RequestParam("min_partial_amount") BigDecimal minPartialAmount) {
        return freecoinsSettingsService.set(currencyId, minAmount, minPartialAmount);
    }
}