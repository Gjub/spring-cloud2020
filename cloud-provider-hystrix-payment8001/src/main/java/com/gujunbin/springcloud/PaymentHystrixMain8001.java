package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/13 14:29
 * @Description:
 */

@SpringBootApplication
@EnableEurekaClient
// 降级激活
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {

    public static void main(String[] args) {
            SpringApplication.run(PaymentHystrixMain8001.class,args);
        }
}
