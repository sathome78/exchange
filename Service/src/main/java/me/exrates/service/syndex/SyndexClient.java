package me.exrates.service.syndex;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.model.serializer.LocalDateTimeFromTimestampDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public interface SyndexClient {

    String REQUEST_ID_FIELD = "request_id";

    OrderInfo createOrder(CreateOrderRequest orderDto);

    OrderInfo getOrderInfo(long orderId);

    String cancelOrder(long orderId);

    String confirmOrder(long orderId);

    String openDispute(long orderId, String comment);

    List<Country> getCountryList();

    List<Currency> getCurrencyList();

    List<PaymentSystemWrapper> getPaymentSystems(String countryCode);

    @Data
    class BaseResponse<T> {
        private boolean status;
        private T result;
        private Error error;

        boolean isError() {
            return !isNull(error);
        }
    }

    @Data
    class BaseError<T> {
        private boolean status;
        private T error;
    }

    @Data
    class Error {
        String message;
        int code;
    }

    @Data
    class CreateOrderRequest extends BaseRequestEntity {
        private int type = 0;
        @JsonProperty("country_code")
        private String countryCode;
        private String currency;
        private BigDecimal amount;
        @JsonProperty("payment_method")
        private String paymentMethod;

        public CreateOrderRequest(SyndexOrderDto syndexOrderDto) {
            this.countryCode = syndexOrderDto.getCountryId();
            this.currency = syndexOrderDto.getCurrency();
            this.amount = syndexOrderDto.getAmountToPay();
            this.paymentMethod = syndexOrderDto.getPaymentSystemId();
        }
    }

    @Data
    class OrderInfo {
        private long id;
        private BigDecimal amount;
        @JsonProperty("amount_btc")
        private BigDecimal amountBtc;
        private int type;
        private int status;
        private BigDecimal commission;
        @JsonProperty("temp_price_usd")
        private BigDecimal tempPriceUsd;
        @JsonProperty("payment_id")
        private String paymentId;
        private String currency;
        @JsonProperty("country_id")
        private String countryId;
        @JsonProperty("payment_details")
        private String paymentDetails;
        @JsonDeserialize(using = LocalDateTimeFromTimestampDeserializer.class)
        @JsonProperty("close_payment_time")
        private LocalDateTime endPaymentTime;
    }

    @Data
    class Currency {
        private String id;
        private String name;
    }

    @Data
    class Country {
        private String id;
        private String name;
        @JsonProperty("default_currency")
        private String defaultCurrency;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    class PaymentSystemWrapper {
        private String id;
        private String name;
        @JsonProperty("currency")
        private List<PaymentSystem> paymentSystems;
        @JsonProperty("min_amount")
        private Map<String, Double> minAmount;
    }

    @Data
    class PaymentSystem {
        private String id;
        private String iso;
    }

    @Data
    class DisputeData {
        private int id;
        private String text;
    }

    @Data
    class BaseRequestEntity {
        @JsonProperty(REQUEST_ID_FIELD)
        private String requestId = String.valueOf(TimeUnit.MILLISECONDS.toMicros(System.currentTimeMillis()));
    }

    @Getter@Setter
    class OrderRequest extends BaseRequestEntity {
        @JsonProperty("order_id")
        private String orderId;

        public OrderRequest(long orderId) {
            super();
            this.orderId = String.valueOf(orderId);
        }
    }

    @AllArgsConstructor
    @Getter@Setter
    class GetPaymentSystemRequest extends BaseRequestEntity {
        @JsonProperty("country_code")
        private String countryCode;
    }

    @AllArgsConstructor
    @Getter@Setter
    class OpenDisputeRequest extends BaseRequestEntity {
        @JsonProperty("order_id")
        private String orderId;
        private String comment;
    }

    @Getter@Setter
    class SyndexCallback{
        @JsonProperty("merchant_id")
        private String merchantId;
        @JsonProperty("txid")
        private String txid;
        @JsonProperty("transfer_id")
        private String transferId;
        private String amount;
        private String address;
        @JsonProperty("id_address")
        private String idAddress;
        @JsonProperty("time_create")
        private String timeCreate;
        private String status;
        private String currency;
        private String signature;


    }
}
