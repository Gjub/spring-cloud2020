package com.gujunbin.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 22:09
 * @Description:
 */
@RestController
public class OrderNacosController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-service}")
    private String serverUrl;

    @GetMapping("/consumer/nacos/payment/{id}")
    public String getPaymentInfo(@PathVariable("id") Integer id){
        return restTemplate.getForObject(serverUrl + "/nacos/payment/" + id, String.class);
    }
}