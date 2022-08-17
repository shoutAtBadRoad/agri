package com.agri.service;

public interface LoginService {
    String loginReturnToken(String username, String password);

    String logtout();
}
