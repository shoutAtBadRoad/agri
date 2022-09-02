package com.agri.controller;



import com.agri.exception.BeyondLoginTimeException;
import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.agri.model.User;
import com.agri.service.LoginService;
import com.agri.utils.annotation.SaveAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    @Qualifier(value = "loginServiceImpl")
    private LoginService loginService;

    @PostMapping("/login")
    @SaveAuth
    public ResultSet login(@RequestBody User user) {
        try {
            String token = loginService.loginReturnToken(user.getUsername(), user.getPassword());
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            return ResultSet.OK(map);
        }catch (BeyondLoginTimeException blt) {
            return ResultSet.error(blt.getResult());
        }
        // 这里捕获的是AES加密中的异常，视情况处理即可
        catch (Exception e) {
            return ResultSet.error("密文加解密错误");
        }
    }

    @PostMapping("/logout")
    @SaveAuth
    public String logout(HttpServletRequest request) {
        return loginService.logtout(request);
    }
}
