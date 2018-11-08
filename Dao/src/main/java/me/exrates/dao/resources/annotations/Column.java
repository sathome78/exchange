package me.exrates.dao.resources.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    Type[] type() default {};
    boolean unique() default false;
    boolean primaryKey() default false;
    boolean autoIncrement() default false;
    ForeignKey[] foreignKey() default {};
    boolean notNull() default false;
}
