package com.gujunbin.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 21:41
 * @Description:
 */
@RestController
public class NacosPaymentController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/nacos/payment/{id}")
    public String getPaymentById(@PathVariable("id") Integer id){
        return "nacos registry center, serverPort: " + serverPort + "\t  id: " + id;
    }
}