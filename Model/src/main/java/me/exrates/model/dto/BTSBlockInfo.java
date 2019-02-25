package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BTSBlockInfo {
    private int blockNum;
    private String previousHash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTSBlockInfo that = (BTSBlockInfo) o;
        return blockNum == that.blockNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockNum);
    }
}
