package com.agri.model;

public class RedisConstant {

    /**
     * 角色权限map的key
     */
    public final static String RESOURCE_ROLES_MAP = "AUTH:RESOURCE_ROLES_MAP";

    /**
     * 加载角色权限的分布式锁名
     */
    public final static String LOCK_RESOURCE_ROLES_MAP = "LOCK:RESOURCE_ROLES_MAP";

    /**
     * 账号锁定key前缀
     */
    public final static String ACCOUNT_LOCK_PREFIX = "LOCK:ACCOUNT";

    /**
     * 账号锁定时间
     */
    public final static Long ACCOUNT_LOCK_TIME = 600 * 1000L;

    /**
     * 输入密码错误可尝试的次数
     */
    public final static Integer ACCOUNT_RETRY_COUNTS = 5;
}
