package com.agri.handler;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 處理異常
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.FORBIDDEN);
        map.put("code", HttpStatus.FORBIDDEN.value());
        map.put("msg", "您的權限不足");
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
