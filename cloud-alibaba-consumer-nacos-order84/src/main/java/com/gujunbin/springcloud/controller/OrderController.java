package com.gujunbin.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;
import com.gujunbin.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/24 11:12
 * @Description:
 */
@RestController
@Slf4j
public class OrderController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private PaymentService paymentService;

    public static final String SERVICE_URL = "http://nacos-cloud-payment-provider";

    @Value("${service-url.nacos-user-service}")
    private String serverUrl;

    @GetMapping("/consumer/payment/nacos/{id}")
    public String getPaymentInfo(@PathVariable("id") Integer id) {
        return restTemplate.getForObject(SERVICE_URL + "/paymentSQL/" + id, String.class);
    }


    /**
     * fallback: 负责的是java的业务异常
     * blockHandler: 这是sentinel控制台的限流方式，在1s一次的时候还是会报对应的异常，但是超过阈值 则会报 sentinel的异常
     *   当两个都配置的时候，被限流降级而抛出BlockException会进入blockHandler处理逻辑
     * exceptionsToIgnore：忽略异常降级，返回默认Error页面
     */
    @GetMapping("/consumer/fallback/{id}")
    //@SentinelResource("fallback") //没有配置
    //@SentinelResource(value = "fallback", fallback = "handlerFallback") //fallback只负责java业务异常
    //@SentinelResource(value = "fallback", blockHandler = "blockHandler") //blockHandler只负责Sentinel控制台配置违规
    @SentinelResource(value = "fallback", blockHandler = "blockHandler", fallback = "handlerFallback",
            exceptionsToIgnore = {IllegalArgumentException.class})
    public CommonResult<Payment> fallback(@PathVariable("id") Long id) {
        CommonResult<Payment> result = restTemplate.getForObject(SERVICE_URL + "/paymentSQL/" + id, CommonResult.class, id);
        if (id == 4) {
            throw new IllegalArgumentException("非法参数异常...");
        } else if (result.getData() == null) {
            throw new NullPointerException("没有对应的id--空指针异常...");
        }
        return result;
    }

    public CommonResult handlerFallback(Long id, Throwable e) {
        Payment payment = new Payment(id, null);
        return new CommonResult(444, "handlerFallback-兜底异常处理\t" + e.getMessage(), payment);
    }

    public CommonResult blockHandler(Long id, BlockException b) {
        Payment payment = new Payment(id, null);
        return new CommonResult(445, "blockHandler-无此流水，这是sentinel控制台限流\t" + b.getMessage(), payment);
    }

    @GetMapping("/consumer/paymentSQL/{id}")
    CommonResult<Payment> paymentSQL(@PathVariable("id") Long id) {
        return paymentService.paymentSQL(id);
    }
}
