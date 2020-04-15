package com.gujunbin.springcloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/15 10:40
 * @Description:
 */
@Configuration
public class GetawayConfig {
    
    /***
     * 配置了 id 为 path_route_baidu 的路由规则
     * 当访问 localhost：9527/guonei 时 会转发至下面配置的 URI
     * 
     * @param routeLocatorBuilder 
     * @return org.springframework.cloud.gateway.route.RouteLocator
     * @author GuJunBin
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();

        routes.route("path_route_baidu",
                r -> r.path("/guonei")
                        .uri("http://news.baidu.com/guonei")).build();
        return routes.build();
    }
}
