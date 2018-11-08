package me.exrates.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ControlPhrase {
    private Long id;
    private String phrase;
    private Long userId;

    public ControlPhrase(String phrase) {
        this.phrase = phrase;
    }
}