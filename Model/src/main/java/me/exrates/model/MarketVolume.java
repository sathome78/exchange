package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class MarketVolume {
    private String name;
    private BigDecimal marketVolume;
}
