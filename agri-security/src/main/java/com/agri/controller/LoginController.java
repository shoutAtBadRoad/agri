package com.agri.controller;



import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.agri.model.User;
import com.agri.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class LoginController {

    @Autowired
    @Qualifier(value = "loginServiceImpl")
    private LoginService loginService;

    @PostMapping("/user/login")
    public ResultSet<Map<String, String>> login(@RequestBody User user) {
        String token = loginService.loginReturnToken(user.getUsername(), user.getPassword());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return ResultSet.OK(ResultStatus.OK, map, null);
    }

    @PostMapping("/user/logout")
    public String logout() {
        return loginService.logtout();
    }
}
