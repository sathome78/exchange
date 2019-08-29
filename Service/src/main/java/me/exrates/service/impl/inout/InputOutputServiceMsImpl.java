package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.UserService;
import me.exrates.service.exception.InoutMicroserviceInternalServerException;
import me.exrates.service.impl.InputOutputServiceImpl;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Optional;

import static me.exrates.service.impl.inout.RefillServiceMsImpl.API_MERCHANT_GET_MIN_CONFIRMATIONS_REFILL;

@Log4j2
@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class InputOutputServiceMsImpl extends InputOutputServiceImpl {

    private static final String API_PREPARE_CREDITS_OPERATION = "/api/prepareCreditsOperation";
    private final RequestUtil requestUtil;
    private final InOutProperties properties;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MessageSource messageSource;

    @Override
    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail, Locale locale) {
        setUserRecipient(locale, payment);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_PREPARE_CREDITS_OPERATION);
        HttpEntity<String> entity;
        try {
            entity = new HttpEntity<>(objectMapper.writeValueAsString(payment), requestUtil.prepareHeaders(userEmail));
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new RuntimeException(String.format("Object mapper error. " +
                    "User email: %s | Locale: %s | Payment: %s", userEmail, locale, payment));
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<CreditsOperation> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<CreditsOperation>() {
                    });

            return Optional.ofNullable(response.getBody());
        }catch (Exception ex){
            ex.getStackTrace();
            System.out.println(ex.getMessage());
            log.error(ex);
            throw new InoutMicroserviceInternalServerException(ex.getMessage());
        }
    }

    @Override
    public Integer getMinConfirmationsRefillByMerchantId(int merchantId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(properties.getUrl() + API_MERCHANT_GET_MIN_CONFIRMATIONS_REFILL + merchantId, Integer.class);
    }

    private void setUserRecipient(Locale locale, Payment payment) {
        User userRecipient;
        try {
            if (!StringUtils.isEmpty(payment.getRecipient())) {
                userRecipient = userService.getIdByNickname(payment.getRecipient()) > 0 ?
                        userService.findByNickname(payment.getRecipient()) : userService.findByEmail(payment.getRecipient());
                payment.setUserRecipient(userRecipient);
            }
        } catch (RuntimeException e) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.nonExistentUser", new Object[]{payment.getRecipient()}, locale));
        }
    }

}
