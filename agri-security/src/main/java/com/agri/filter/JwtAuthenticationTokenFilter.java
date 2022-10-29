package com.agri.filter;

import com.agri.filter.jwtfilter.JwtFilterChain;
import com.agri.model.CommonResult;
import com.agri.model.ResultStatus;
import com.agri.security.model.LoginUser;
import com.agri.service.ISysUserService;
import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT token验证类
 * @see #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
 * {@link JwtFilterChain}
 * @author jyp
 * @since 2022-9-1
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    JwtFilterChain chain;
    @Resource
    private ISysUserService userService;

    @Override                                                                                                                                                       
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 獲取token
        String token = httpServletRequest.getHeader("Authorization");
        if(Objects.isNull(token) || !StringUtils.hasText(token)) {
            // 放行
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        // 解析token
        String userid = null;
        Claims claims = chain.doCheck(token, null, UUID.randomUUID().toString());
        if(claims == null) {
            // 處理異常
            CommonResult<Object> result = CommonResult.create(ResultStatus.UNAUTHORIZED,null);
            renderString(httpServletResponse, JSONObject.toJSONString(result));
            log.info("非法认证-token:" + token);
            return;
        }

        userid = claims.getSubject();
        // 如果用户的角色修改了，redis中缓存的信息会被清楚，所以需要判断redis中是否有用户信息，如果没有需要从库中重新读取并放入缓存中
        boolean b = redisUtil.hasKey(userid);
        LoginUser loginUser;
        if(!b)
            loginUser = userService.loadUserInfoById(Long.valueOf(userid), userid);
        else {
            loginUser = (LoginUser) redisUtil.get(userid);
//            loginUser = JSONObject.parseObject(o.toString(), LoginUser.class);
        }
        // 检查用户是否禁用
        if(loginUser.getUser().getStatus() == 1) {
            redisUtil.del(loginUser.getUser().getUserid().toString());
            CommonResult<String> commonResult = CommonResult.create(ResultStatus.ACCOUNT_LOCKED, "账户已被禁用，请联系管理员");
            renderString(httpServletResponse, JSONObject.toJSONString(commonResult));
            return;
        }
        // 存入SecurityContextHolder
        // 獲取權限信息封裝到Authentication
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(true);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // 放行
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    private void renderString(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(msg);
    }
}
