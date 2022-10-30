package com.agri.filter.jwtfilter;

import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 判断token格式是否合法
 * @author jyp
 * @since 2020-9-1
 */
@Component
@Order(0)
@Slf4j
public class LegalJwtHandler implements IJwtHandler{

    @Override
    public Claims check(String token, Claims claims, JwtFilterChain chain, String id) {
        try {
            claims = JwtUtil.parseJWT(token);
        }catch (ExpiredJwtException e) {
            claims = e.getClaims();
            log.info("token过期，但是合法");
        }catch (Exception e) {
            log.info("token非法");
            chain.index.remove();
            return null;
        }
        log.info("token合法");
        return chain.doCheck(token, claims, id);
    }
}
