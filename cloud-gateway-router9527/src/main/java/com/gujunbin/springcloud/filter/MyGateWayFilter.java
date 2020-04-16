package com.gujunbin.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/16 10:01
 * @Description:
 */
@Component
@Slf4j
@Order(0)
public class MyGateWayFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("********Welcome to MyGateWayFilter :" + new Date());
        String username = exchange.getRequest().getQueryParams().getFirst("username");
        if( null == username ){
            log.warn("****警告：用户名为 NULL 非法用户 [○･｀Д´･ ○]");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
