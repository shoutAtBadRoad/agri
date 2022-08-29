package com.agri.filter.jwtfilter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilterChain {

    @Autowired
    List<IJwtHandler> handlers = new ArrayList<>();

    IndexThreadLocal index = new IndexThreadLocal();

    public Claims doCheck(String token, Claims claims) {
        int next = index.get()+1;
        index.set(next);
        if(next == handlers.size()) {
            return claims;
        }
        IJwtHandler handler = handlers.get(next);
        return handler.check(token, claims,this);
    }

    static class  IndexThreadLocal extends ThreadLocal<Integer> {
        @Override
        protected Integer initialValue() {
            return -1;
        }
    }
}
