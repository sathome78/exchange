package me.exrates.model.dto.merchants.btc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BtcTransactionDto {
  
  private BigDecimal amount;
  private BigDecimal fee;
  private Integer confirmations;
  private String txId;
  private String blockhash;
  private List<String> walletConflicts;
  private Long time;
  private Long timeReceived;
  private String comment;
  private String to;
  private List<BtcTxPaymentDto> details;
  
}
