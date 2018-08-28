package me.exrates.model.kyc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorldCheck {
    private int id;
    private int userId;
    private String admin;
    private WorldCheckStatus status;
}
