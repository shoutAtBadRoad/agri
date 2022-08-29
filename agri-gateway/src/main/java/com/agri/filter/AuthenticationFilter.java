package com.agri.filter;

import com.agri.filter.unfilter.WhiteList;
import com.agri.model.ResultSet;
import com.agri.model.ResultStatus;
import com.agri.service.AuthService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Log4j
@Component
@Order(0)
public class AuthenticationFilter implements GlobalFilter{

    @Autowired
    private WhiteList ignoreList;
    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        Set<URI> uris = exchange.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
//        String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
        String originalUri = exchange.getRequest().getURI().getPath();
        URI routeUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
//        log.info("Incoming request " + originalUri + " is routed to id: " + route.getId()
//                + ", uri:" + routeUri);
        //TODO url白名单校验
        boolean ignored = ignoreList.match(originalUri);
        if(ignored) {
            return chain.filter(exchange);
        }
        //TODO 校验用户的认证token和权限
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(StringUtils.isEmpty(token)) {
            log.info("无用户凭证");
            return Mono.empty();
        }
        String authenticated = authService.verifyAuthentication(token, routeUri.getPath());
        //TODO 如果未认证通过
        ResultSet resultSet = JSONObject.parseObject(authenticated, ResultSet.class);
        int code = resultSet.getCode();
        if(code == ResultStatus.AUTHORITY.getCode()) {
            log.info("用户无效凭证");
            return Mono.empty();
        }
        Boolean b = (Boolean)resultSet.getBody();
        if(!b) {
            log.info("用户无权限");
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

//    @Override
//    public int getOrder() {
//        return 0;
//    }

}
