package com.agri.filter;

import com.agri.security.model.LoginUser;
import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
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

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

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
        try{
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        }catch (Exception e){
            e.printStackTrace();
        }
        // 從redis獲取用戶信息
        String redisKey = userid;
        Object o = redisUtil.get(redisKey);
        if(Objects.isNull(o)) {
            throw new RuntimeException("用戶未登錄");
        }
        LoginUser loginUser = JSON.parseObject(o.toString(), LoginUser.class);
        // 存入SecurityContextHolder
        //TODO 獲取權限信息封裝到Authentication
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(true);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // 放行
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
