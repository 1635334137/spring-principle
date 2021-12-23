package com.lanzong.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LZController {
    String value() default "";
}
