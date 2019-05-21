package me.exrates.model.loggingTxContext;

import lombok.Getter;
import lombok.Setter;


@Getter@Setter
public class Context {

    private Integer querriesCount = null;

    public Context(Integer querriesCount) {
        this.querriesCount = querriesCount;
    }

    public Integer incrementAndGet() {
        return ++querriesCount;
    }
}
