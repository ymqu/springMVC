package com.enjoy.qu.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnjoyRequestMapping {
    String value() default "";
}
