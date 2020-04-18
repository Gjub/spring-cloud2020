package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.service.IProviderMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 11:12
 * @Description:
 */
@RestController
public class SendMessageController {

    @Resource
    private IProviderMessage providerMessage;

    @GetMapping("/send")
    public String send(){
        return providerMessage.send();
    }
}
