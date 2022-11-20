package com.agri.aop;

import com.agri.controller.LoginController;
import com.agri.exception.UnAuthorizedException;
import com.agri.security.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 检查用户角色是否可登陆相关应用
 */
@Aspect
@Component
@Slf4j
public class CheckAfterLogin {

    @Pointcut("execution(* com.agri.service.impl.LoginServiceImpl.loginReturnToken(..))")
    public void checkPoint() {}

    @Pointcut("execution(* com.agri.controller.LoginController.login*(..))")
    public void registerAppType(){}

    @Resource
    private HttpServletRequest request;

    /**
     * 记录和消除用户登陆的应用类型
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "registerAppType()")
    public Object registerAppType(ProceedingJoinPoint joinPoint) throws Throwable {
        String appType = request.getHeader("appType");
        log.info("用户登陆的应用：" + appType);
        LoginController.AppType.set(appType);
        Object o = joinPoint.proceed();
        LoginController.AppType.remove();
        return o;
    }

    /**
     * 检查用户的登陆应用是否有权限，无权限则抛出异常
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "checkPoint()")
    public Object checkAppType(ProceedingJoinPoint joinPoint) throws Throwable {
        String type = LoginController.AppType.get();
        Object res = joinPoint.proceed();
        if(!StringUtils.isEmpty(type) && type.equals("Manager")) {
            LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> permissions = principal.getPermissions();
            if (!permissions.contains("admin") && !permissions.contains("coder")) {
                throw new UnAuthorizedException("该账号无权限登陆此应用");
            }
        }
        return res;
    }

}
