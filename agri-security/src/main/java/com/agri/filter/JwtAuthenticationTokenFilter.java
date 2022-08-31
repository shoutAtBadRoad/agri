package com.agri.filter;

import com.agri.filter.jwtfilter.JwtFilterChain;
import com.agri.model.ResultSet;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    JwtFilterChain chain;
    @Autowired
    private ISysUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // 獲取token
        String token = httpServletRequest.getHeader("Authorization");
        if(!StringUtils.hasText(token)) {
            // 放行
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        // 解析token
        String userid = null;
        Claims claims = chain.doCheck(token, null, UUID.randomUUID().toString());
        if(claims == null) {
            // 處理異常
            ResultSet<Object> resultSet = ResultSet.OK(ResultStatus.AUTHORITY, null, "用户认证失败");
            renderString(httpServletResponse, JSONObject.toJSONString(resultSet));
            log.info("用戶未登錄");
            return;
        }
        userid = claims.getSubject();
        // 如果用户的角色修改了，redis中缓存的信息会被清楚，所以需要判断redis中是否有用户信息，如果没有需要从库中重新读取并放入缓存中
        boolean b = redisUtil.hasKey(userid);
        if(!b) {
            synchronized (JwtAuthenticationTokenFilter.class) {
                boolean b1 = redisUtil.hasKey(userid);
                if(!b1) {
                    // TODO 需要考虑加载失败的情况
                    userService.loadUserInfoById(Long.valueOf(userid));
                }
            }
        }
        Object o = redisUtil.get(userid);
        LoginUser loginUser = JSON.parseObject(o.toString(), LoginUser.class);
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
