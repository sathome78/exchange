package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.exrates.model.Commission;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.UserStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

public abstract class AngularApiCommonTest {
    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    ObjectMapper objectMapper = new ObjectMapper();

    protected RequestBuilder getApiRequestBuilder(URI uri, HttpMethod method, HttpHeaders httpHeaders, String content, String contentType) {
        HttpHeaders headers = createHeaders();
        if (httpHeaders != null) {
            headers.putAll(httpHeaders);
        }
        if (method.equals(HttpMethod.GET)) {
            System.out.println(MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType));
            return MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.POST)) {
            return MockMvcRequestBuilders.post(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.PUT)) {
            return MockMvcRequestBuilders.put(uri).headers(headers).content(content).contentType(contentType);
        }
        throw new UnsupportedOperationException(String.format("Method: %s not supported", method.toString()));
    }

    protected User getMockUser() {
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setUserStatus(UserStatus.REGISTERED);
        user.setPassword("TEST_PASSWORD");
        return user;
    }

    protected CurrencyPair getMockCurrencyPair() {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(100);
        currencyPair.setName("TEST_NAME");
        currencyPair.setCurrency1(getMockCurrency("TEST_NAME"));
        currencyPair.setCurrency2(getMockCurrency("TEST_NAME"));
        currencyPair.setMarket("TEST_MARKET");
        currencyPair.setMarketName("TEST_MARKET_NAME");
        currencyPair.setPairType(CurrencyPairType.ALL);
        currencyPair.setHidden(Boolean.TRUE);
        currencyPair.setPermittedLink(Boolean.TRUE);
        return currencyPair;
    }

    protected Currency getMockCurrency(String name) {
        Currency currency = new Currency();
        currency.setId(100);
        currency.setName(name);
        currency.setDescription("TEST_DESCRIPTION");
        currency.setHidden(Boolean.TRUE);
        return currency;
    }

    protected MerchantCurrencyScaleDto getMockMerchantCurrencyScaleDto() {
        MerchantCurrencyScaleDto dto = new MerchantCurrencyScaleDto();
        dto.setMerchantId(100);
        dto.setCurrencyId(200);
        dto.setScaleForRefill(300);
        dto.setScaleForWithdraw(400);
        dto.setScaleForTransfer(500);

        return dto;
    }

    protected MerchantCurrency getMockMerchantCurrency() {
        MerchantCurrency dto = new MerchantCurrency();
        dto.setMerchantId(100);
        dto.setCurrencyId(200);
        dto.setName("TEST_NAME");
        dto.setDescription("TEST_DESCRIPTION");
        dto.setMinSum(BigDecimal.valueOf(50));
        dto.setInputCommission(BigDecimal.valueOf(7));
        dto.setOutputCommission(BigDecimal.valueOf(10));
        dto.setFixedMinCommission(BigDecimal.valueOf(5));
        dto.setListMerchantImage(Collections.emptyList());
        dto.setProcessType("TEST_PROCESS_TYPE");
        dto.setMainAddress("TEST_MAIN_ADDRESS");
        dto.setAddress("TEST_ADDRESS");
        dto.setAdditionalTagForWithdrawAddressIsUsed(Boolean.TRUE);
        dto.setAdditionalTagForRefillIsUsed(Boolean.TRUE);
        dto.setAdditionalFieldName("TEST_ADDITIONAL_FIELD_NAME");
        dto.setGenerateAdditionalRefillAddressAvailable(Boolean.TRUE);
        dto.setRecipientUserIsNeeded(Boolean.TRUE);
        dto.setComissionDependsOnDestinationTag(Boolean.TRUE);
        dto.setSpecMerchantComission(Boolean.TRUE);
        dto.setAvailableForRefill(Boolean.TRUE);
        dto.setNeedVerification(Boolean.TRUE);

        return dto;
    }

    protected Optional<CreditsOperation> getMockCreditsOperation() {
        CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .initialAmount(getMockCommissionDataDto().getAmount())
                .amount(getMockCommissionDataDto().getResultAmount())
                .commissionAmount(getMockCommissionDataDto().getCompanyCommissionAmount())
                .commission(getMockCommissionDataDto().getCompanyCommission())
                .operationType(OperationType.BUY)
                .user(getMockUser())
                .currency(getMockCurrency("TEST_CURRENCY"))
                .wallet(getMockWallet())
                .merchant(getMockMerchant())
                .merchantCommissionAmount(getMockCommissionDataDto().getMerchantCommissionAmount())
                .destination("TEST_DESTINATION")
                .destinationTag("TEST_DESTINATION_TAG")
                .transactionSourceType(TransactionSourceType.ORDER)
                .recipient(getMockUser())
                .recipientWallet(getMockWallet())
                .build();

        return Optional.of(creditsOperation);
    }

    protected Wallet getMockWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(100);
        wallet.setCurrencyId(200);
        wallet.setUser(getMockUser());
        wallet.setActiveBalance(BigDecimal.TEN);
        wallet.setReservedBalance(BigDecimal.ONE);
        wallet.setName("TEST_NAME");

        return wallet;
    }

    protected NotificationResultDto getMockNotificationResultDto() {
        String[] arguments = {"ONE", "TWO"};
        return new NotificationResultDto("TEST_MESSAGE_SOURCE", arguments);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HEADER_SECURITY_TOKEN, ImmutableList.of("Test-Token"));
        return headers;
    }

    private Merchant getMockMerchant() {
        Merchant merchant = new Merchant();
        merchant.setId(100);
        merchant.setName("TEST_NAME");
        merchant.setDescription("TEST_DESCRIPTION");
        merchant.setServiceBeanName("TEST_SERVER_BEAN_NAME");
        merchant.setProcessType(MerchantProcessType.CRYPTO);
        merchant.setRefillOperationCountLimitForUserPerDay(10);
        merchant.setAdditionalTagForWithdrawAddressIsUsed(Boolean.TRUE);
        merchant.setTokensParrentId(200);
        merchant.setNeedVerification(Boolean.TRUE);

        return merchant;
    }

    private CommissionDataDto getMockCommissionDataDto() {
        return new CommissionDataDto(
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(70),
                "TEST_MERCHant_COMMISSION_UNIT",
                BigDecimal.valueOf(80),
                Commission.zeroComission(),
                BigDecimal.valueOf(90),
                "TEST_COMPANY_COMMISSION_AMOUNT",
                BigDecimal.valueOf(95),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(110),
                Boolean.TRUE
        );
    }
}