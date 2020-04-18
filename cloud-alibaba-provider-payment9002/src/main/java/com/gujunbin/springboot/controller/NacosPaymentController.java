package com.gujunbin.springboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/nacos/payment")
    public String getPaymentById(@RequestParam("id") Integer id){
        return "nacos registry center, serverPort: " + serverPort + "\t  id: " + id;
    }
}