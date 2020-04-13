package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.service.PaymentHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/13 15:56
 * @Description:
 */

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
public class OrderHystrixController {

    @Resource
    private PaymentHystrixService paymentHystrixService;
    
    @GetMapping(value = "/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id) {
        String result = paymentHystrixService.paymentInfo_OK(id);
        log.info("**********Result:" + result);
        return result;
    }

    @GetMapping("/consumer/payment/hystrix/timeOut/{id}")
   /* @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })*/
    // @HystrixCommand 没有用上面特别指明 就默认全局通用的降级处理
    @HystrixCommand
    public String paymentTimeout(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentInfo_TimeOut(id);
    }

    public String paymentTimeoutHandler(@PathVariable("id") Integer id) {
        return "我是消费端的80接口，对方的支付系统繁忙，请稍后再试...┭┮﹏┭┮...";
    }

    /**
     * 全局通用降级处理
     * 该类加上  @DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
     * 默认全局 方法上加 @HystrixCommand
     *
     * @return
     */
    public String paymentGlobalTimeoutHandler() {
        return "Global全局降级处理，请稍后再试.../(⊙︿⊙)/";
    }

}
