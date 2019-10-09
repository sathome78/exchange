package me.exrates.service.impl;

import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.adgroup.responses.AdGroupResponseDto;
import me.exrates.model.dto.merchants.adgroup.responses.HeaderResponseDto;
import me.exrates.model.dto.merchants.adgroup.responses.InvoiceDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponsePayOutDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResultResponseDto;
import me.exrates.service.AdgroupService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.http.AdGroupHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConfigAdGroupContext.class})
public class AdGroupServiceImplTest {

    @Autowired
    private AdGroupHttpClient adGroupHttpClient;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AdgroupService adgroupService;

    @Test
    public void testRefill() {
        when(merchantService.findById(anyInt())).thenReturn(getMerchant());
        when(adGroupHttpClient.createInvoice(anyString(), anyString(), anyObject())).thenReturn(getInvoice());

        Map<String, String> refill = adgroupService.refill(getRefillRequestCreateDto());

        assertEquals("GET", refill.get("$__method"));
        assertEquals(getInvoice().getResponseData().getPaymentLink(), refill.get("$__redirectionUrl"));
    }


    @Test(expected = MerchantException.class)
    public void withdraw_failed() throws Exception {
        when(adGroupHttpClient.createPayOut(anyString(), anyString(), anyObject())).thenReturn(getWithdrawBad());
        adgroupService.withdraw(getWithdrawRequest());
    }

    @Test
    public void withdraw_ok() throws Exception {
        when(adGroupHttpClient.createPayOut(anyString(), anyString(), anyObject())).thenReturn(getWithdrawOk());
        Map<String, String> withdraw = adgroupService.withdraw(getWithdrawRequest());
        assertEquals(2, withdraw.size());
    }

    private RefillRequestCreateDto getRefillRequestCreateDto() {
        RefillRequestCreateDto createDto = new RefillRequestCreateDto();
        createDto.setMerchantId(1);
        createDto.setAmount(new BigDecimal(100));
        createDto.setCurrencyName("RUB");
        createDto.setId(1);
        return createDto;
    }

    private Merchant getMerchant() {
        Merchant merchant = new Merchant();
        merchant.setId(232);
        merchant.setName("Adgroup_wallet");
        return merchant;
    }

    private AdGroupResponseDto<InvoiceDto> getInvoice() {
        HeaderResponseDto header = new HeaderResponseDto();
        ResultResponseDto result = new ResultResponseDto();
        InvoiceDto responseData = InvoiceDto.builder()
                .id("3c2edcc6-0416-4e0c-9164-6edcd97eb038")
                .message("Customerorderhasbeenregistered,kindlysharebelow details with them.")
                .paymentLink("http://localhost:8008/yandex/invoice?_id=2c9c9480-0f6a-4ed6-b6fe-46de1428dc47")
                .comment("0901182615")
                .walletAddr("410019605447448")
                .build();

        return new AdGroupResponseDto<>(header, result, responseData, null);
    }

    private AdGroupResponseDto<ResponsePayOutDto> getWithdrawBad() {

        HeaderResponseDto header = new HeaderResponseDto();
        ResultResponseDto result = new ResultResponseDto();

        ResponsePayOutDto responseData = ResponsePayOutDto.builder()
                .originalAmount(new BigDecimal(100))
                .amount(new BigDecimal(100))
                .status("PENDING")
                .id("a37eaa2a-e847-43ed-b290-0b1c3ef43dd6")
                .refId("617871424097000304")
                .extraId("821976011")
                .build();

        return new AdGroupResponseDto<>(header, result, responseData, null);
    }

    private AdGroupResponseDto<ResponsePayOutDto> getWithdrawOk() {

        HeaderResponseDto header = new HeaderResponseDto();
        ResultResponseDto result = new ResultResponseDto();

        ResponsePayOutDto responseData = ResponsePayOutDto.builder()
                .originalAmount(new BigDecimal(100))
                .amount(new BigDecimal(100))
                .status("APPROVED")
                .id("a37eaa2a-e847-43ed-b290-0b1c3ef43dd6")
                .refId("617871424097000304")
                .extraId("821976011")
                .build();

        return new AdGroupResponseDto<>(header, result, responseData, null);
    }

    private WithdrawMerchantOperationDto getWithdrawRequest() {
        return WithdrawMerchantOperationDto.builder()
                .amount("100.00")
                .currency("RUB")
                .accountTo("ewfwfwe")
                .build();

    }
}
