package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/22 16:28
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SentinelMain8401 {

    public static void main(String[] args) {
        SpringApplication.run(SentinelMain8401.class, args);
    }
}
