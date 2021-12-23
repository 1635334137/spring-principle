package com.lanzong.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 自动注入
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZAutowired {
    String value() default "";
}
