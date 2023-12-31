package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 10.04.2017.
 */
@Getter @Setter
@ToString
public class OrderValidationDto {
  private Map<String, Object> errors = new HashMap<>();
  private Map<String, Object[]> errorParams = new HashMap<>();
}
