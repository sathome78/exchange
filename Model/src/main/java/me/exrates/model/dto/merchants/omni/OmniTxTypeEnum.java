package me.exrates.model.dto.merchants.omni;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

@JsonFormat
public enum OmniTxTypeEnum {

    Simple_Send(0), Freeze_Transaction(185), Unknown(-1);


    private int type_int;

    OmniTxTypeEnum(int id) {
        this.type_int = id;
    }

    @JsonValue
    public int getId() {
        return type_int;
    }

    @JsonCreator
    public static OmniTxTypeEnum fromId(int id) {
        return Arrays.stream(OmniTxTypeEnum.class.getEnumConstants())
                .filter(e -> e.getId() == id)
                .findAny()
                .orElse(Unknown);
    }
}
