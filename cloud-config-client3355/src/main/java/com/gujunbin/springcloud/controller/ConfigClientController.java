package com.gujunbin.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/16 12:21
 * @Description:
 */
@RestController
public class ConfigClientController {

    @Value("${server.port}")
    private String serverPort;  //要访问的3344上的信息

    @GetMapping("/serverPort")	//请求地址
    public String getServerPort(){
        return serverPort;
    }
}
