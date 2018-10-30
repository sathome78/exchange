package me.exrates.service.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.exrates.service.exception.WalletsApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@PropertySource(value = {"classpath:/external-apis.properties"})
@Component
public class WalletsApi {

    private final String url;

    private final RestTemplate restTemplate;

    @Autowired
    public WalletsApi(@Value("${api.wallets.url}") String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, BigDecimal> getBalances() {
        ResponseEntity<WalletsData[]> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url, WalletsData[].class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
        WalletsData[] body = responseEntity.getBody();
        return nonNull(body) && body.length != 0
                ? Arrays.stream(body)
                .collect(toMap(
                        wallet -> wallet.name,
                        wallet -> new BigDecimal(wallet.currentAmount.replace(" ", ""))
                ))
                : Collections.emptyMap();
    }

    public BigDecimal getBalanceByCurrencyAndWallet(String currencySymbol, String walletAddress) {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("wallet", walletAddress);

        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s/balance", url, currencySymbol))
                .queryParams(requestParameters)
                .build();

        ResponseEntity<WalletBalanceData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), WalletBalanceData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            return null;
        }
        WalletBalanceData body = responseEntity.getBody();

        return nonNull(body) ? BigDecimal.valueOf(body.balance) : null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WalletsData {

        String name;
        String currentAmount;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WalletBalanceData {

        double balance;
    }
}
