package me.exrates.model.dto.freecoins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveResultDto {

    private int id;
    @JsonProperty("giveaway_id")
    private int giveawayId;
    private boolean received;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("last_received")
    private LocalDateTime lastReceived;
    @JsonIgnore
    private String receiverEmail;
}