package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/24 10:45
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProviderMain9004 {

    public static void main(String[] args) {
        SpringApplication.run(ProviderMain9004.class, args);
    }
}
