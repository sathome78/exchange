package me.exrates.model.dto.merchants.adgroup.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdGroupResponseDto {

    private HeaderResponseDto header;
    private ResultResponseDto result;
    private ResponseData responseData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Errors {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ResponseData {
        private String message;
        @JsonProperty("_id")
        private String id;
        private String walletAddr;
        private String comment;
        private String paymentLink;
    }
}
