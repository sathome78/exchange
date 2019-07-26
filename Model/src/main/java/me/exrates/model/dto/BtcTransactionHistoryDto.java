package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 24.03.2017.
 */
@Log4j2
@Getter @Setter
@ToString
@NoArgsConstructor
public class BtcTransactionHistoryDto {
    private String txId;
    private String address;
    private String category;
    private String amount;
    private String blockhash;
    private String fee;
    private Integer confirmations;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime time;

    public BtcTransactionHistoryDto(String txId, String address, String category, String amount, Integer confirmations, LocalDateTime time) {
        this.txId = txId;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.confirmations = confirmations;
        this.time = time;
    }

    public BtcTransactionHistoryDto(String txId) {
        this.txId = txId;
    }

    public Object getAttributeValueByName(String attributeName) {
        Field fieldOfTransaction = null;
        try {
            fieldOfTransaction =  this.getClass().getDeclaredField(attributeName);
            fieldOfTransaction.setAccessible(true);
            return fieldOfTransaction.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e);
        }
        return null;
    }

}