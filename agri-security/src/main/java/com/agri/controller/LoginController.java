package com.agri.controller;



import com.agri.exception.AccountException;
import com.agri.exception.BeyondLoginTimeException;
import com.agri.exception.UnAuthorizedException;
import com.agri.model.*;
import com.agri.service.LoginService;
import com.agri.utils.annotation.SaveAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jyp
 * @since 2022-8-27
 */
@RestController
@RequestMapping("/user")
@Api(tags = "登陆注销控制器")
public class LoginController {

    /**
     * 记录当前线程的登陆方式
     */
    public static final ThreadLocal<Integer> loginMethod = new ThreadLocal<>();

    public static final ThreadLocal<String> AppType = new ThreadLocal<>();

    @Autowired
    @Qualifier(value = "loginServiceImpl")
    private LoginService loginService;

    @PostMapping("/login")
    @ApiOperation(value = "登录接口", response = String.class)
    public CommonResult login(@ApiParam("用户名和密码") @RequestBody User user) {
        try {
            // 记录登陆方式
            loginMethod.set(LoginType.PASSWORD.getType());
            String token = loginService.loginReturnToken(user.getUsername(), user.getPassword());
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return CommonResult.OK(map);
        }catch (AccountException ac){
            return CommonResult.create(ResultStatus.ACCOUNT_LOCKED, ac.getReason());
        } catch (BeyondLoginTimeException blt) {
            // 捕获登陆失败次数
            boolean status= blt.isStatus();
            CommonResult<String> res;
            if(!status)
                res = CommonResult.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = CommonResult.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException a) {
            // 未找到匹配的用户名和密码
            return CommonResult.create(ResultStatus.PASS_WRONG, a.getMessage());
        }catch (UnAuthorizedException unAuthorizedException) {
            return CommonResult.create(ResultStatus.UNAUTHORIZED, unAuthorizedException.getReason());
        }
        // 这里捕获的是AES加密中的异常，视情况处理即可
        catch (Exception e) {
            return CommonResult.create(ResultStatus.ERROR, e.getMessage());
        }finally {
            loginMethod.remove();
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "注销接口", response = String.class)
    @SaveAuth(roles = {"admin", "coder", "user", "farmer", "guest"})
    public CommonResult<Object> logout(HttpServletRequest request) {
        String logout = loginService.logtout(request);
        return CommonResult.OK(logout);
    }

    @PostMapping("/sms/login")
    @ApiOperation(value = "短信验证码登陆")
    public CommonResult loginViaSms(@ApiParam("手机号") @RequestBody SysUser user, @ApiParam("验证码") String code) {
        String phoneNumber = user.getPhonenumber();
        if(StringUtils.isEmpty(phoneNumber)){
            return CommonResult.create(ResultStatus.ERROR, "手机号为空");
        }
        if(StringUtils.isEmpty(code)) {
            return CommonResult.create(ResultStatus.ERROR, "验证码为空");
        }
        try {
            loginMethod.set(LoginType.CODE_PHONE.getType());
            String token = loginService.loginReturnToken(user.getPhonenumber(), code);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return CommonResult.OK(map);
        }catch (AccountException ac){
            return CommonResult.create(ResultStatus.ACCOUNT_LOCKED, ac.getReason());
        }catch (BeyondLoginTimeException blt) {
            boolean status= blt.isStatus();
            CommonResult<String> res;
            if(!status)
                res = CommonResult.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = CommonResult.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException | BadCredentialsException a) {
            return CommonResult.create(ResultStatus.PASS_WRONG, a.getMessage());
        }catch (UnAuthorizedException unAuthorizedException) {
            return CommonResult.create(ResultStatus.UNAUTHORIZED, unAuthorizedException.getReason());
        }
        catch (Exception e) {
            return CommonResult.create(ResultStatus.ERROR, "内部错误");
        }finally {
            loginMethod.remove();
        }
    }

    @PostMapping("/phone/login")
    @ApiOperation(value = "手机号密码登陆")
    public CommonResult loginViaSms(@ApiParam("手机号和密码") @RequestBody SysUser user) {
        String phoneNumber = user.getPhonenumber(), password = user.getPassword();
        if(StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(password)){
            return CommonResult.create(ResultStatus.PASS_WRONG, null);
        }
        try {
            loginMethod.set(LoginType.PASS_PHONE.getType());
            String token = loginService.loginReturnToken(phoneNumber, password);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return CommonResult.OK(map);
        }catch (AccountException ac){
            return CommonResult.create(ResultStatus.ACCOUNT_LOCKED, ac.getReason());
        }catch (BeyondLoginTimeException blt) {
            boolean status= blt.isStatus();
            CommonResult<String> res;
            if(!status)
                res = CommonResult.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = CommonResult.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException a) {
            return CommonResult.create(ResultStatus.PASS_WRONG, a.getMessage());
        }catch (UnAuthorizedException unAuthorizedException) {
            return CommonResult.create(ResultStatus.UNAUTHORIZED, unAuthorizedException.getReason());
        }
        catch (Exception e) {
            return CommonResult.create(ResultStatus.ERROR, "内部错误");
        }finally {
            loginMethod.remove();
        }
    }
}
