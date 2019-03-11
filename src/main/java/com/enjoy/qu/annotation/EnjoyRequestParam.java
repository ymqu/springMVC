package com.enjoy.qu.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnjoyRequestParam {
    String value() default "";
}
