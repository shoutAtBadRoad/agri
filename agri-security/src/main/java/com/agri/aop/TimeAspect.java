package com.agri.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

//
@Component
@Aspect
public class TimeAspect {

    @Pointcut("execution(* com.agri.controller.AuthController.*(..))")
    public void authPointcut() {}

    @Around(value = "authPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object o = joinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println(end-start + "ms");
        return o;
    }
}
