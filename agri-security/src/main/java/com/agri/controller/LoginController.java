package com.agri.controller;



import com.agri.exception.BeyondLoginTimeException;
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

    public static final ThreadLocal<Integer> loginMethod = new ThreadLocal<>();

    @Autowired
    @Qualifier(value = "loginServiceImpl")
    private LoginService loginService;

    @PostMapping("/login")
    @ApiOperation(value = "登录接口", response = String.class)
    @SaveAuth
    public ResultSet login(@ApiParam("用户名和密码") @RequestBody User user) {
        try {
            // 记录登陆方式
            loginMethod.set(LoginType.PASSWORD.getType());
            String token = loginService.loginReturnToken(user.getUsername(), user.getPassword());
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResultSet.OK(map);
        }catch (BeyondLoginTimeException blt) {
            // 捕获登陆失败次数
            boolean status= blt.isStatus();
            ResultSet<String> res;
            if(!status)
                res = ResultSet.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = ResultSet.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException a) {
            // 未找到匹配的用户名和密码
            return ResultSet.create(ResultStatus.PASS_WRONG, a.getMessage());
        }
        // 这里捕获的是AES加密中的异常，视情况处理即可
        catch (Exception e) {
            return ResultSet.create(ResultStatus.ERROR, e.getMessage());
        }finally {
            loginMethod.remove();
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "注销接口", response = String.class)
    @SaveAuth
    public ResultSet<Object> logout(HttpServletRequest request) {
        String logtout = loginService.logtout(request);
        return ResultSet.OK(logtout);
    }

    @PostMapping("/sms/login")
    @ApiOperation(value = "短信验证码登陆")
    @SaveAuth
    public ResultSet loginViaSms(@ApiParam("手机号") @RequestBody SysUser user, @ApiParam("验证码") String code) {
        String phoneNumber = user.getPhonenumber();
        if(StringUtils.isEmpty(phoneNumber)){
            return ResultSet.create(ResultStatus.ERROR, "手机号为空");
        }
        if(StringUtils.isEmpty(code)) {
            return ResultSet.create(ResultStatus.ERROR, "验证码为空");
        }
        try {
            loginMethod.set(LoginType.CODE_PHONE.getType());
            String token = loginService.loginReturnToken(user.getPhonenumber(), code);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResultSet.OK(map);
        }catch (BeyondLoginTimeException blt) {
            boolean status= blt.isStatus();
            ResultSet<String> res;
            if(!status)
                res = ResultSet.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = ResultSet.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException | BadCredentialsException a) {
            return ResultSet.create(ResultStatus.PASS_WRONG, a.getMessage());
        } catch (Exception e) {
            return ResultSet.create(ResultStatus.ERROR, "内部错误");
        }finally {
            loginMethod.remove();
        }
    }

    @PostMapping("/phone/login")
    @ApiOperation(value = "短信验证码登陆")
    @SaveAuth
    public ResultSet loginViaSms(@ApiParam("手机号和密码") @RequestBody SysUser user) {
        String phoneNumber = user.getPhonenumber(), password = user.getPassword();
        if(StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(password)){
            return ResultSet.create(ResultStatus.PASS_WRONG, null);
        }
        try {
            loginMethod.set(LoginType.PASS_PHONE.getType());
            String token = loginService.loginReturnToken(phoneNumber, password);
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResultSet.OK(map);
        }catch (BeyondLoginTimeException blt) {
            boolean status= blt.isStatus();
            ResultSet<String> res;
            if(!status)
                res = ResultSet.create(ResultStatus.ACCOUNT_LOCKED, blt.getResult());
            else
                res = ResultSet.create(ResultStatus.PASS_WRONG, blt.getResult());
            return res;
        }catch (AuthenticationException a) {
            return ResultSet.create(ResultStatus.PASS_WRONG, a.getMessage());
        } catch (Exception e) {
            return ResultSet.create(ResultStatus.ERROR, "内部错误");
        }finally {
            loginMethod.remove();
        }
    }
}
