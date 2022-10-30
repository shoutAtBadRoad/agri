package com.agri.aop;

import com.agri.utils.annotation.lock.Locked;
import com.agri.utils.annotation.lock.RKey;
import com.agri.utils.load.RedisLoader;
import com.agri.utils.load.ResourceLoadService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author jyp
 * @since 2022-9-10
 *
 * <p>
 *     {@link Locked}对Locked注解下的方法的执行，加上分布式锁
 *     如果是当前对象调用自己的方法，加这个注解也没用的，因为它调用的不是代理对象
 * </p>
 * {@link RKey} 资源的key
 * {@link com.agri.utils.annotation.lock.Param} 获取资源所需参数
 * Locked注解下的方法，需要有一个名为rKey，类型为String的参数，需要业务中传入，作为资源在redis中的key
 */
@Aspect
@Component
@Slf4j
public class LoadFromRedisWithLock {

    private static final Set<Class<?>> returnTypes = new HashSet(){{
        add(Map.class);
        add(List.class);
        add(Set.class);
        add(String.class);
    }};

    private ResourceLoadService service;

    @Autowired
    private RedisLoader redisLoader;

    @Pointcut("@annotation(com.agri.utils.annotation.lock.Locked) && execution(* com.agri.service.impl.*.*(..))")
    public void LockPointcut() {}

    @Around(value = "LockPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Method[] methods = clazz.getMethods();
        Method method = null;
        for (Method method1 : methods) {
            if(method1.getName().equals(methodName)){
                method = method1;
                break;
            }
        }
        if(method != null && !method.isAnnotationPresent(Locked.class)) {
            return joinPoint.proceed();
        }
        log.info("使用锁进行资源加载");
        assert method != null;
        Parameter[] parameters = method.getParameters();
        String rKey = null;
        int index = -1;
        for (Parameter parameter : parameters) {
            index++;
            if (parameter.isAnnotationPresent(RKey.class)) {
                break;
            }
        }
        if(index == -1) {
            throw new RuntimeException("没有找到rKey");
        }
        // 资源在redis中的key
        Object[] args = joinPoint.getArgs();
        rKey = (String) args[index];
        // 加载资源时用到的锁名
        String lockKey = clazz.getName() + methodName + Arrays.toString(args);
        ResourceLoadService service = RedisLoader.services.getOrDefault(lockKey, (key, lock, point) -> {
            try {
                return point.proceed();
            }catch (Exception e) {
                throw new RuntimeException("切入点执行失败");
            }
        });

        if(this.service == null) {
            this.service = service;
        }
        Object o;
        Class<?> type = method.getReturnType();
        // TODO
        type = returnTypes.contains(type) ? type : type;
        o = redisLoader.loadWithLock(service, lockKey, rKey, type, joinPoint);
        if(o.getClass() == type) {
            return o;
        }else {
            return JSONObject.parseObject(o.toString(), type);
        }
    }

}
