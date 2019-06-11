package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.*;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.model.vo.WalletOperationMsDto;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.RefillRequestRevokeException;
import me.exrates.service.impl.RefillServiceImpl;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;

@Service
@Conditional(MicroserviceConditional.class)
@PropertySource(value = {"classpath:/job.properties"})
public class RefillServiceMsImpl extends RefillServiceImpl {

    private static final String GET_ADDRESS_BY_MERCHANT_ID_AND_CURRENCY_ID_AND_USER_ID = "/api/getAddressByMerchantIdAndCurrencyIdAndUserId";
    private static final String CHECK_INPUT_REQUESTS_LIMIT = "/api/checkInputRequestsLimit";
    private static final String CREATE_REFILL_REQUEST = "/api/createRefillRequest";
    public static final String API_MERCHANT_GET_ADDITIONAL_REFILL_FIELD_NAME = "/api/merchant/getAdditionalRefillFieldName/";
    public static final String API_MERCHANT_GET_MIN_CONFIRMATIONS_REFILL = "/api/merchant/getMinConfirmationsRefill/";
    public static final String API_MERCHANT_RETRIEVE_ADDRESS_AND_ADDITIONAL_PARAMS_FOR_REFILL_FOR_MERCHANT_CURRENCIES = "/api/merchant/retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies";
    private static final String API_MERCHANT_CALL_REFILL_IREFILLABLE = "/api/merchant/callRefillIRefillable";
    private final InOutProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final RequestUtil requestUtil;
    private final WalletService walletService;
    private final CompanyWalletService companyWalletService;
    private final RefillRequestDao refillRequestDao;
    private final UserService userService;

    @Value("${invoice.blockNotifyUsers}")
    private Boolean BLOCK_NOTIFYING;

    private static final Logger log = LogManager.getLogger("refill");

    public RefillServiceMsImpl(InOutProperties properties, ObjectMapper objectMapper, @Qualifier("inoutRestTemplate") RestTemplate template, RequestUtil requestUtil, WalletService walletService, CompanyWalletService companyWalletService, RefillRequestDao refillRequestDao, UserService userService) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.template = template;
        this.requestUtil = requestUtil;
        this.walletService = walletService;
        this.companyWalletService = companyWalletService;
        this.refillRequestDao = refillRequestDao;
        this.userService = userService;
    }

    @Override
    public Map<String, Object> createRefillRequest(RefillRequestCreateDto requestCreateDto) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + CREATE_REFILL_REQUEST);
            HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestCreateDto), requestUtil.prepareHeaders(requestCreateDto.getUserId()));
            ResponseEntity<Map<String, Object>> response = template.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<Map<String, Object>>() {
                    });

            return response.getBody();
        }catch (Exception ex){
            log.error(ex);
            throw new RuntimeException("Inout error");
        }
    }

    @Override
    public Optional<String> getAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + GET_ADDRESS_BY_MERCHANT_ID_AND_CURRENCY_ID_AND_USER_ID)
                .queryParam("currency_id", currencyId)
                .queryParam("merchant_id", merchantId);

        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(userId));
        ResponseEntity<Optional<String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, new ParameterizedTypeReference<Optional<String>>() {});
        return response.getBody();
    }

    @Override
    public boolean checkInputRequestsLimit(int currencyId, String email) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + CHECK_INPUT_REQUESTS_LIMIT)
                .queryParam("currency_id", currencyId);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(email));
        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, Boolean.class);

        return response.getBody();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRefillRequest(WalletOperationMsDto dto) {
        WalletOperationData walletOperationData = dto.getWalletOperationData();
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (walletTransferStatus != SUCCESS) {
            throw new RefillRequestRevokeException(walletTransferStatus.name());
        }
        CompanyWallet companyWallet = companyWalletService.findByCurrency(new Currency(dto.getCurrencyId()));
        companyWalletService.deposit(
                companyWallet,
                walletOperationData.getAmount(),
                walletOperationData.getCommissionAmount()
        );
    }

    @Override
    public Map<String, String> callRefillIRefillable(RefillRequestCreateDto request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_CALL_REFILL_IREFILLABLE);

        HttpEntity<?> entity = null;
        try {
            entity = new HttpEntity<>(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new RuntimeException(String.format("Object mapper error. " +
                    "RefillRequestCreateDto: %s", request));
        }
        ResponseEntity<Map<String, String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Map<String, String>>() {});

        return response.getBody();
    }

    @Override
    public List<MerchantCurrency> retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies, String userEmail) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_RETRIEVE_ADDRESS_AND_ADDITIONAL_PARAMS_FOR_REFILL_FOR_MERCHANT_CURRENCIES)
                .queryParam("userEmail", userEmail);

        HttpEntity<?> entity = null;
        try {
            entity = new HttpEntity<>(objectMapper.writeValueAsString(merchantCurrencies));
        } catch (JsonProcessingException e) {
            log.error(e);
            throw new RuntimeException(String.format("Object mapper error. " +
                    "User email: %s | List<MerchantCurrency>: %s", userEmail, merchantCurrencies));
        }

        ResponseEntity<List<MerchantCurrency>> response = template.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<List<MerchantCurrency>>() {});

            setElements(merchantCurrencies, response);
            return response.getBody();
    }

    private void setElements(List<MerchantCurrency> merchantCurrencies, ResponseEntity<List<MerchantCurrency>> response) {
        merchantCurrencies.clear();
        merchantCurrencies.addAll(response.getBody());
    }

    @Override
    public DataTable<List<RefillRequestAddressShortDto>> getAdressesShortDto(DataTableParams dataTableParams, RefillAddressFilterData filterData) {
        PagingData<List<RefillRequestAddressShortDto>> data = refillRequestDao.getAddresses(dataTableParams, filterData);
        try {
            fillAdressesDtos(data.getData());
        } catch (Exception e) {
            log.error(e);
        }
        DataTable<List<RefillRequestAddressShortDto>> output = new DataTable<>();
        output.setData(data.getData());
        output.setRecordsTotal(data.getTotal());
        output.setRecordsFiltered(data.getFiltered());
        return output;
    }

    private void fillAdressesDtos(List<RefillRequestAddressShortDto> dtos) {
        dtos.forEach(p -> setAddressFieldName(p));
    }

    private void setAddressFieldName(RefillRequestAddressShortDto p) {
        String additionalRefillFieldName = template.getForObject(properties.getUrl() + API_MERCHANT_GET_ADDITIONAL_REFILL_FIELD_NAME + p.getMerchantId(), String.class);
        p.setAddressFieldName(additionalRefillFieldName);
    }

    @Override
    public List<RefillOnConfirmationDto> getOnConfirmationRefills(String email, int currencyId) {
        Integer userId = userService.getIdByEmail(email);
        if (userId == 0) {
            return Collections.emptyList();
        }
        List<RefillOnConfirmationDto> dtos = refillRequestDao.getOnConfirmationDtos(userId, currencyId);
        dtos.forEach(p -> {
            setNeededConfirmations(p);
        });
        return dtos;
    }

    private void setNeededConfirmations(RefillOnConfirmationDto p) {
        Integer neededConfirmations = template.getForObject(properties.getUrl() + API_MERCHANT_GET_MIN_CONFIRMATIONS_REFILL + p.getMerchantId(), Integer.class);
        p.setNeededConfirmations(neededConfirmations);
    }
}






























