package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.exrates.model.enums.WsMessageTypeEnum;

@Data
@AllArgsConstructor
public class WsMessageObject {

    private WsMessageTypeEnum typeEnum;
    private Object message;


}
