package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BTSBlockInfo {
    private int blockNum;
    private String previousHash;
}
