package com.agri.filter.jwtfilter;

import io.jsonwebtoken.Claims;

/**
 * JWT拦截器接口
 * 默认实现类
 * {@link LegalJwtHandler} 判断传入的token是否合法
 * {@link ExpireJwtHandler} 判断token是否过期（可以续期就传给RenewalJWTHandler处理）
 * {@link RenewalJwtHandler} token续期拦截器
 * @see IJwtHandler#check(String, Claims, JwtFilterChain, String) 实现方法来执行拦截器链
 */
public interface IJwtHandler {

    Claims check(String token, Claims claims, JwtFilterChain chain, String id);
}
