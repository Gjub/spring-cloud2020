package com.gujunbin.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/14 20:14
 * @Description:
 */
@SpringBootApplication
//开启 HystrixDashboard 图形界面
@EnableHystrixDashboard
public class HystrixDashboardMain9001 {

    public static void main(String[] args) {
            SpringApplication.run(HystrixDashboardMain9001.class,args);
        }
}
