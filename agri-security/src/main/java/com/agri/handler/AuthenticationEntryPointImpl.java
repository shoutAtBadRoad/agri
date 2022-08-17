package com.agri.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 處理異常
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.UNAUTHORIZED);
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("msg", "用戶認證失敗，請重新登陸");
        String jsonString = JSON.toJSONString(map);
        renderString(response, jsonString);
    }

    private void renderString(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(msg);
    }
}
