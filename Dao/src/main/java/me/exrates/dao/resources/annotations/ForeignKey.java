package me.exrates.dao.resources.annotations;

public @interface ForeignKey {
    Class targetEntity();
    String filedName();
}
