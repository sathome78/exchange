package me.exrates.service.usdx.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.service.usdx.model.enums.UsdxTransactionStatus;
import me.exrates.service.usdx.model.enums.UsdxTransactionType;
import me.exrates.service.usdx.model.enums.UsdxWalletAsset;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UsdxTransaction {
    private String transferId;
    private String accountName;
    private BigDecimal amount;
    private UsdxWalletAsset currency;
    private UsdxTransactionType type;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    private UsdxTransactionStatus status;
    private String memo;
    private String customData;

    private String errorCode;
    private String failReason;
}
