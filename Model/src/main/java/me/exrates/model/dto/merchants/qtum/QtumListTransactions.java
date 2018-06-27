package me.exrates.model.dto.merchants.qtum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import me.exrates.model.dto.merchants.neo.JsonRpcResponseError;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class QtumListTransactions {

    private List<QtumTransaction> transactions;
    private String lastblock;

}
