package com.agri.filter.jwtfilter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtFilterChain {

    @Autowired
    List<IJwtHandler> handlers = new ArrayList<>();

    public IndexThreadLocal index = new IndexThreadLocal();

    public Claims doCheck(String token, Claims claims, String id) {
//        ConcurrentHashMap<String, Integer> map = index.get();
//        int next = map.getOrDefault(id,-1);
//        next++;

//        map.put(id, next);
//        index.set(map);
        int next = 1 + index.get();
        System.out.println(Thread.currentThread().getName() + ":" + "正在执行 - " + next);
        index.set(next);
        if(next == handlers.size()) {
            index.remove();
            return claims;
        }
        IJwtHandler handler = handlers.get(next);
        return handler.check(token, claims,this, id);
    }

    static class  IndexThreadLocal extends ThreadLocal<Integer> {
        @Override
        protected Integer initialValue() {
            return -1;
        }
    }

//    static class  IndexThreadLocal extends ThreadLocal<ConcurrentHashMap<String, Integer>> {
//        @Override
//        protected ConcurrentHashMap<String, Integer> initialValue() {
//            return new ConcurrentHashMap<>();
//        }
//    }
}
