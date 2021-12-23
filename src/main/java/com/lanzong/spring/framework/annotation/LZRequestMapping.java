package com.lanzong.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求URL
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZRequestMapping {
    String value() default "";
}
