package com.sakura.reggieApi.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  日志注解
 * @author sakura
 * @className LogAnnotation
 * @createTime 2023/2/8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAnnotation {

    // 日志的名称
    String value();
}
