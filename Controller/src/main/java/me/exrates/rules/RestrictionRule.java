package me.exrates.rules;

import me.exrates.model.ngExceptions.NgResponseException;

public interface RestrictionRule {

    boolean matches();

    NgResponseException fail();
}
