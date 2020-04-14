package com.gujunbin.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/13 14:31
 * @Description:
 */
@Service
public class PaymentService {

    /**
     * 正常访问
     *
     * @param id
     * @return java.lang.String
     * @author GuJunBin
     */
    public String paymentInfo_OK(Integer id) {
        return "线程池：" + Thread.currentThread().getName() +
                "  paymentInfo_OK,id:  " + id + "\t" + "O(∩_∩)O哈哈~";
    }

    /**
     * 超时访问
     *
     * @param id
     * @return java.lang.String
     * @author GuJunBin
     */
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String paymentInfo_TimeOut(Integer id) {

        /**
         * 模拟线程异常 也会跳转至 paymentInfo_TimeOutHandler兜底方法
         * int age= 1/0;
         */

        Integer Time = 1;
        //模拟线程耗时
        try {
            TimeUnit.SECONDS.sleep(Time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() +
                " id: " + id + "\t" + "(*^▽^*)  耗时（s）：" + Time;
    }


    public String paymentInfo_TimeOutHandler(Integer id) {
        return "程序运行繁忙或报错,请稍后再试*****" + "当前线程: " +
                Thread.currentThread().getName() + id + "\t " + "(꒦_꒦) )";
    }

    // 服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),//是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),// 请求总数阈值
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//快照时间窗
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")//错误百分比阈值
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        if (id < 0) {
            throw new RuntimeException();
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t " + "调用成功,流水号: " + serialNumber;
    }

    public String paymentCircuitBreakerFallback(@PathVariable("id") Integer id) {
        return "id不能为负数,请稍后再试~ id: " + id;
    }
}
