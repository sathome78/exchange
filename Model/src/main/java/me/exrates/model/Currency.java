package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Currency {

    private int id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    private boolean hidden;

    public Currency(int id) {
        this.id = id;
    }
}
