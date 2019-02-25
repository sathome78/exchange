package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.BitsharesTransactionInfo;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsInfo {
    private List<BitsharesTransactionInfo> listOfTransactionInfo = new LinkedList<>();
}
