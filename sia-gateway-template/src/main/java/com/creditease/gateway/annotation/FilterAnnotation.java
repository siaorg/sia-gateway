package com.creditease.gateway.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 自定义组件注解
 * 
 * @author peihua
 * 
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FilterAnnotation {

    /**
     * FilterAnnotation的描述信息
     * 
     * @return
     */

    String type() default "";

    int order() default 0;

    boolean isenabled() default false;

    String compname() default "";

    String compdesc() default "";
}