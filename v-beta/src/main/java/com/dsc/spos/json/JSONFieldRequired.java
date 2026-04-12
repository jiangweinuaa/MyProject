package com.dsc.spos.json;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
//        ElementType.METHOD,
        ElementType.FIELD
//        ElementType.PARAMETER
})
public @interface JSONFieldRequired {

    boolean required() default true;

    String display() default "";

}
