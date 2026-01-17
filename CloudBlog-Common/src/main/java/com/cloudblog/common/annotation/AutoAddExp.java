package com.cloudblog.common.annotation;

import com.cloudblog.common.enums.ExpSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoAddExp {

    /**
     * 积分类型
     * @return
     */
    ExpSource value();

    /**
     * 积分数（手动指定）
     */
    int exp() default -1;
}
