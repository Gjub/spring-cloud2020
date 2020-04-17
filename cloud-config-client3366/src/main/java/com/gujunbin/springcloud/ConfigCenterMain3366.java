package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/17 12:14
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
public class ConfigCenterMain3366 {

    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterMain3366.class, args);
    }
}
