package com.agri.filter.jwtfilter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link JwtFilterChain#handlers} 注入拦截器链
 * {@link JwtFilterChain#index} ThreadLocal当前线程的拦截器执行定位
 * {@link JwtFilterChain#doCheck(String, Claims, String)} 传入token、用户主体属性、id可以不要
 * @author jyp
 * @since 2022-9-1
 */
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
