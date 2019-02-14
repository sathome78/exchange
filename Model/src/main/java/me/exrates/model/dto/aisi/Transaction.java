package me.exrates.model.dto.aisi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private String timestamp;
    private String sender;
    private String receiver;
    private String amount;
}
