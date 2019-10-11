package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.service.syndex.SyndexCallException;
import me.exrates.service.syndex.SyndexClient;
import me.exrates.service.syndex.SyndexFrontDataService;
import me.exrates.service.syndex.SyndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


@Log4j2(topic = "syndex")
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SyndexController {

    private final SyndexFrontDataService dataService;
    private final SyndexService syndexService;

    @Autowired
    public SyndexController(SyndexFrontDataService dataService,
                            SyndexService syndexService) {
        this.dataService = dataService;
        this.syndexService = syndexService;
    }

    @GetMapping("/public/v2/syndex/country")
    public List<SyndexClient.Country> getCountries() {
        return dataService.getCountryList();
    }

    @GetMapping("/public/v2/syndex/currency")
    public Set<SyndexClient.Currency> getCurrencies() {
        return dataService.getCurrencyList();
    }

    @GetMapping("/public/v2/syndex/payment-system")
    public Set<SyndexClient.PaymentSystem> getPaymentSystems(@RequestParam("country") String country) {
        return dataService.getPaymentSystemList(country);
    }

    @PostMapping(value = "/private/v2/syndex/order/dipsute", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity openDispute(@RequestBody SyndexClient.DisputeData data) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        syndexService.openDispute(data, email);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/private/v2/syndex/order")
    public SyndexOrderDto getOrderInfo(@RequestParam @NotNull Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return syndexService.getOrderInfo(id, email);
    }

    @PostMapping("/private/v2/syndex/order/{id}")
    public ResponseEntity confirmOrder(@PathVariable Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        syndexService.confirmOrder(id, email);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/private/v2/syndex/order")
    public ResponseEntity cancelOrder(@RequestParam @NotNull Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        syndexService.cancelOrder(id, email);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/public/v2/syndex/result_callback")
    public ResponseEntity callbackHandler(@RequestParam @NotNull SyndexClient.OrderInfo order) {
        log.debug("syndex callback {}", order);
        syndexService.onCallbackEvent(order);
        return ResponseEntity.ok()
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EmptyResultDataAccessException.class, SyndexCallException.class, NullPointerException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
