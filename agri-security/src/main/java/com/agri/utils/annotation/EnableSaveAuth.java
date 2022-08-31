package com.agri.utils.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要加载权限到数据库的时候，开启这个注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SaveAuthorityScanner.class)
public @interface EnableSaveAuth {
}
