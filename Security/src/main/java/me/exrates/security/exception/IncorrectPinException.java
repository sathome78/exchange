package me.exrates.security.exception;

import me.exrates.model.dto.PinDto;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by maks on 02.07.2017.
 */
public class IncorrectPinException extends AuthenticationException {

    private static final String ERR_CODE = "message.pin_code.incorrect";

    private PinDto dto;

    public IncorrectPinException(PinDto dto) {
        super(dto.getMessage());
        this.dto = dto;
    }

    public IncorrectPinException(String message) {
        super(message);
    }

    public IncorrectPinException() {
        super(ERR_CODE);
    }

    public PinDto getDto() {
        return dto;
    }

}
