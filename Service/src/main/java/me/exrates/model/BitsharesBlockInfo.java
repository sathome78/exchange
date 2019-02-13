package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.BitsharesTransactionInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BitsharesBlockInfo {
    private List<BitsharesTransactionInfo> listOfTransactionInfo;
}
