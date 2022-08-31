package com.agri.filter.jwtfilter;

import io.jsonwebtoken.Claims;

public interface IJwtHandler {

    Claims check(String token, Claims claims, JwtFilterChain chain, String id);
}
