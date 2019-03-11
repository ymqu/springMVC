package com.enjoy.qu.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnjoyAutowired {
    String value() default "";
}
