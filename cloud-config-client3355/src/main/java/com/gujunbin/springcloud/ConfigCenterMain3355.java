package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/16 12:19
 * @Description:
 */
@SpringBootApplication
@EnableEurekaClient
public class ConfigCenterMain3355 {

    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterMain3355.class, args);
    }
}
