package com.gujunbin.springcloud.service;

import org.springframework.stereotype.Service;

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
    public String paymentInfo_TimeOut(Integer id) {

        Integer Time = 3;
        //模拟线程耗时
        try {
            TimeUnit.SECONDS.sleep(Time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() +
               " paymentInfo_TimeOut,id: " + id + "\t" +
               "┭┮﹏┭┮  耗时（s）：" + Time;
    }
}
