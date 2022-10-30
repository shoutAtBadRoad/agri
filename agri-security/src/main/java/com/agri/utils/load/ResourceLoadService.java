package com.agri.utils.load;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * load逻辑是
 *   数据库加载资源
 *   缓存到redis
 *   返回资源
 */
public interface ResourceLoadService {

    public Object load(String key, String lock, ProceedingJoinPoint joinPoint) throws Throwable;
}
