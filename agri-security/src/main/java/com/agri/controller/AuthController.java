package com.agri.controller;

import com.agri.model.RedisConstant;
import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.security.model.LoginUser;
import com.agri.service.PermsRolesService;
import com.agri.utils.RedisUtil;
import com.agri.utils.annotation.SaveAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author jyp
 * @since 2022-9-1
 */
@RestController
@RequestMapping(value = "/auth")
@Api(tags = "权限校验控制器")
public class AuthController {

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public PermsRolesService permsRolesService;

    /**
     * 权限校验接口
     * @return
     */
    @PostMapping("/apiAuth")
    @ApiOperation(value = "权限校验接口", response = Boolean.class)
    @SaveAuth
    public CommonResult<Boolean> authenticationApi(@ApiParam("进行校验的uri") @RequestParam("uri") String uri) {
        // 拿到用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser user = (LoginUser)authentication.getPrincipal();
        //TODO 拿到请求接口与权限信息
        List<String> permissions = user.getPermissions();
        Boolean checked = permsRolesService.checkPerms(uri, permissions);
        if(checked) {
            return CommonResult.OK(true);
        }
        return CommonResult.create(ResultStatus.FORBIDDEN, false);
    }

}
