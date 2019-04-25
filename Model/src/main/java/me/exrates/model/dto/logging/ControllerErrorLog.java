package me.exrates.model.dto.logging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ControllerErrorLog {

    private int status;
    private String body;
}
