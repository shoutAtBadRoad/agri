package com.agri.service;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {
    String loginReturnToken(String username, String password);

    String logtout(HttpServletRequest request);
}
