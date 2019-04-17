package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.exrates.model.enums.WsSourceTypeEnum;

@Data
@AllArgsConstructor
public class WsMessageObject {

    private WsSourceTypeEnum typeEnum;
    private Object message;


}
