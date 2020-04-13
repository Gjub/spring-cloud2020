package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/10 10:16
 * @Description:
 *  * @EnableDiscoveryClient、@EnableEurekaClient： 都是能够让注册中心能够发现，扫描到该服务。
 *  * @EnableEurekaClient 只适用于Eureka作为注册中心
 *  * @EnableDiscoveryClient  可以是其他注册中心
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain8004 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8004.class,args);
    }
}
