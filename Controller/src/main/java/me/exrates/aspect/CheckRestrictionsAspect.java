package me.exrates.aspect;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPairRestrictionsEnum;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.RestrictedCountry;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.GeoLocation;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.model.enums.UserRole;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.ngService.GeoLocationService;
import me.exrates.ngService.RestrictedCountryService;
import me.exrates.security.service.CheckUserAuthority;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Component
@Aspect
public class CheckRestrictionsAspect {

    private final CurrencyService currencyService;
    private final GeoLocationService geoLocationService;
    private final RestrictedCountryService restrictedCountryService;
    private final UserService userService;

    @Autowired
    public CheckRestrictionsAspect(CurrencyService currencyService,
                                   GeoLocationService geoLocationService,
                                   RestrictedCountryService restrictedCountryService,
                                   UserService userService) {
        this.currencyService = currencyService;
        this.geoLocationService = geoLocationService;
        this.restrictedCountryService = restrictedCountryService;
        this.userService = userService;
    }

    @Before("@annotation(checkRestrictions)")
    public void checkIp(JoinPoint jp, CheckRestrictions checkRestrictions) {

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();

        if (new HashSet<>(Arrays.asList(checkRestrictions.restrictions())).contains(RestrictedOperation.TRADE)) {
            final Optional<InputCreateOrderDto> orderDto = getOrderCreatedDto(jp);
            final InputCreateOrderDto inputCreateOrderDto = orderDto
                    .orElseThrow(() -> new RuntimeException("Failed to obtain inputCreateOrderDto"));

            final int currencyPairId = inputCreateOrderDto.getCurrencyPairId();
            final CurrencyPairWithRestriction currencyPair = currencyService.findCurrencyPairByIdWithRestrictions(currencyPairId);

            if (!currencyPair.hasTradeRestriction()) {
                return;
            }

            if (currencyPair.getTradeRestriction().stream().anyMatch(r -> r == CurrencyPairRestrictionsEnum.ESCAPE_USA)) {

                final String userEmail = userService.getUserEmailFromSecurityContext();
                final User user = userService.findByEmail(userEmail);

                if (user.hasTradePrivileges()) {
                    return;
                }

                if (user.getVerificationRequired()) {
                    String message = "Current user needs to fix verification issues to trade with " + currencyPair.getName();
                    throw new NgResponseException(ErrorApiTitles.KYC_VERIFICATION_REQUIRED, message);
                }

                processRestrictedCountriesCheck(checkRestrictions);

            }
        }
    }

    private Optional<InputCreateOrderDto> getOrderCreatedDto(JoinPoint jp) {
        Object[] args = jp.getArgs();
        return Arrays.stream(args)
                .filter(arg -> arg instanceof InputCreateOrderDto)
                .map(r -> (InputCreateOrderDto) r)
                .findAny();
    }

    private void processRestrictedCountriesCheck(CheckRestrictions checkRestrictions) {
        Set<RestrictedCountry> restrictedCountries = new HashSet<>();
        Arrays.stream(checkRestrictions.restrictions())
                .forEach(res -> restrictedCountries.addAll(restrictedCountryService.findAllByOperation(res)));

        HttpServletRequest request = IpUtils.getCurrentRequest();
        String ipAddress = IpUtils.getIpForDbLog(request);

        final GeoLocation geoLocation = geoLocationService.findById(ipAddress);
        String country = Objects.isNull(geoLocation)
                ? ""
                : geoLocation.getCountry();

        if (StringUtils.isNoneEmpty(country)
                && restrictedCountries.stream().anyMatch(c -> c.getCountryCode().equalsIgnoreCase(country)
                || c.getCountryName().equalsIgnoreCase(country))) {
            String message = "Trading restrictions are set for country: " + country;
            throw new NgResponseException(ErrorApiTitles.RESTRICTIONS_COUNTRY_ISSUE, message);

        }
    }

}
