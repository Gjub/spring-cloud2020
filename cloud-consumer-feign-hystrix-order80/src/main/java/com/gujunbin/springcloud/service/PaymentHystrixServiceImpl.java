package com.gujunbin.springcloud.service;

import org.springframework.stereotype.Service;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/13 19:31
 * @Description:
 */
@Service
public class PaymentHystrixServiceImpl implements PaymentHystrixService {

    @Override
    public String paymentInfo_OK(Integer id) {
        return "----PaymentHystrixServiceImpl  Fall Back-paymentInfo_OK ~~~ o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "----PaymentHystrixServiceImpl  Fall Back-paymentInfo_TimeOut ~~~ o(╥﹏╥)o";
    }
}
