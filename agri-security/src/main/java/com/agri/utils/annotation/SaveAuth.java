package com.agri.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用在接口的方法上，根据接口名与 {@link #roles()}角色名匹配
 * {@link SaveAuthorityScanner}将数据写入数据库 {@link EnableSaveAuth}开启注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveAuth {

    /**
     *
     * @return 匹配的角色名数组
     */
    String[] roles() default {"guest"};

}
