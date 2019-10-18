package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.EnfinsMerchantService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestNotFoundException;
import me.exrates.service.exception.RefillRequestRevokeException;
import me.exrates.service.util.JsonUtils;
import me.exrates.service.util.RestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2(topic = "enfins_log")
@RestController
@RequestMapping("/merchants/enfins")
@PropertySource({"classpath:/merchants/enfins.properties"})
public class EnfinsMerchantController {

    private final String serverIPs;
    private final EnfinsMerchantService enfinsMerchantService;

    @Autowired
    public EnfinsMerchantController(@Value("${server_ip}") String serverIP,
                                    EnfinsMerchantService enfinsMerchantService) {
        this.serverIPs = serverIP;
        this.enfinsMerchantService = enfinsMerchantService;
    }

    @RequestMapping(value = "/payment/status", method = RequestMethod.POST)
    public ResponseEntity<?> statusPayment(HttpServletRequest servletRequest, @RequestParam Map<String, String> inputParams) throws RefillRequestAppropriateNotFoundException {
        String clientIpAddress = RestUtil.getClientIpAddress(servletRequest);
        log.info("Response from status callback: {}, IP {}", JsonUtils.toJson(inputParams), clientIpAddress);
        String[] allowedIP = serverIPs.split(",");
        if (Stream.of(allowedIP).noneMatch(ip -> ip.equalsIgnoreCase(clientIpAddress))) {
            log.error("Callback from not authorized server {}", clientIpAddress);
            return new ResponseEntity<>(BAD_REQUEST);
        }
        try {
            enfinsMerchantService.processPayment(inputParams);
            log.info("Processed successfully");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (RefillRequestAlreadyAcceptedException e) {
            log.info("Processed successfully, but request already accepted");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (RefillRequestRevokeException e) {
            log.info("Processed failed, but will return success response");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (RefillRequestNotFoundException e) {
            log.info("Processed failed, but will return success response, request not found");
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            log.info("Processed failed, error occurred", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/payment/success", method = RequestMethod.GET)
    public ResponseEntity<?> statusSuccess() throws RefillRequestAppropriateNotFoundException {
        log.info("Response from success callback");
        return ResponseEntity.ok().build(); //need to redirect to front
    }

    @RequestMapping(value = "/payment/fail", method = RequestMethod.GET)
    public ResponseEntity<?> statusFail() throws RefillRequestAppropriateNotFoundException {
        log.info("Response from fail callback");
        return ResponseEntity.ok().build(); //need to redirect to front
    }
}
