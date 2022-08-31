package com.agri.handler;

import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
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
        ResultSet<Object> resultSet = ResultSet.OK(ResultStatus.AUTHORITY, null, "用户认证失败");
        renderString(response, JSONObject.toJSONString(resultSet));
    }

    private void renderString(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(msg);
    }
}
