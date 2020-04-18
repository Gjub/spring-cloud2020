package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 21:34
 * @Description:
 */

@SpringBootApplication
@EnableDiscoveryClient
public class NacosMain9002 {

    public static void main(String[] args) {
        SpringApplication.run(NacosMain9002.class, args);
    }
}
