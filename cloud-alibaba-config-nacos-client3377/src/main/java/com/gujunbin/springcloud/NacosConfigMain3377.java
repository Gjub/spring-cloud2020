package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/19 10:34
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosConfigMain3377 {

    public static void main(String[] args) {
        SpringApplication.run(NacosConfigMain3377.class, args);
    }
}
