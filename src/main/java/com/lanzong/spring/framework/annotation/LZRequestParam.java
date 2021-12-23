package com.lanzong.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求参数映射
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZRequestParam {
    String value() default "";
}
