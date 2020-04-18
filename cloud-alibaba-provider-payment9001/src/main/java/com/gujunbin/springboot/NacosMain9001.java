package com.gujunbin.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 20:54
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosMain9001 {

    public static void main(String[] args) {
        SpringApplication.run(NacosMain9001.class, args);
    }
}
