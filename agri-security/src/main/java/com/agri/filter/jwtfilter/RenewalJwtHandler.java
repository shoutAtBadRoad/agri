package com.agri.filter.jwtfilter;

import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 进行token续期
 * @see Order 用来在拦截器链中排序
 * @author jyp
 * @since 2022-9-1
 */
@Component
@Order(2)
@Slf4j
public class RenewalJwtHandler implements IJwtHandler{

    public static final Long DEFAULT_EXPIRE_TIME = JwtUtil.DEFAULT_EXPIRE_TIME * 2;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Claims check(String token, Claims claims, JwtFilterChain chain, String id) {
        String subject = (String) claims.get("subject");
        String newToken = JwtUtil.createJWT(subject);
        redisUtil.set(token, newToken, DEFAULT_EXPIRE_TIME);
        log.info(token + "已经刷新");
        return chain.doCheck(token, claims, id);
    }
}
