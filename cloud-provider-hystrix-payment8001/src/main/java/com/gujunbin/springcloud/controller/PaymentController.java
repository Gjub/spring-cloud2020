package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/13 14:46
 * @Description:
 */

@Slf4j
@RestController
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;
    
    @GetMapping("/payment/hystrix/ok")
    public String paymentInfo_OK(@RequestParam("id") Integer id) {
        String result = paymentService.paymentInfo_OK(id);
        log.info("**********Result: " + result);
        return result;
    }

    @GetMapping("/payment/hystrix/timeOut/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
        String result = paymentService.paymentInfo_TimeOut(id);
        log.info("**********Result: " + result);
        return result;
    }
}
