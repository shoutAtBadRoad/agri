package com.agri.controller;

import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.agri.security.model.LoginUser;
import com.agri.service.PermsRolesService;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.SaveAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public PermsRolesService permsRolesService;

    Map<String, LoginUser> map = new HashMap<>(){{
        put("1", new LoginUser());
    }};

    /**
     * 权限校验接口
     * @return
     */
    @PostMapping("/apiAuth")
    @SaveAuth
    public ResultSet<Boolean> authenticationApi(HttpServletRequest request, @RequestParam("uri")String uri) {
        // 拿到用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser user = (LoginUser)authentication.getPrincipal();
        //TODO 拿到请求接口与权限信息
        List<String> permissions = user.getPermissions();
        Boolean checked = permsRolesService.checkPerms(uri, permissions);
        if(checked) {
            return ResultSet.OK(ResultStatus.OK,Boolean.TRUE,null);
        }
        return ResultSet.OK(ResultStatus.OK, Boolean.FALSE, null);
    }

}
