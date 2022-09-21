package com.agri.filter.jwtfilter;

import com.agri.utils.JwtUtil;
import com.agri.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 判断JWT token是否过期
 * @author jyp
 * @since 2020-9-1
 */
@Component
@Order(1)
@Slf4j
public class ExpireJwtHandler implements IJwtHandler{

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Claims check(String token, Claims claims, JwtFilterChain chain, String id) {
        String val = (String) redisUtil.get(token);
        if(StringUtils.isEmpty(val)) {
            // 长时间没有操作，token 已经失效
            log.info("用户未登陆或已经自动离线");
            chain.index.remove();
            return null;
        }
        try {
            claims = JwtUtil.parseJWT(val);
        }catch (ExpiredJwtException e) {
            claims = e.getClaims();
            log.info("token需要续期");
            return chain.doCheck(token, claims, id);
        }
        chain.index.remove();
        return claims;
    }
}
