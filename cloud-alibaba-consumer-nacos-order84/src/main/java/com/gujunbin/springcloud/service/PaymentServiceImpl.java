package com.gujunbin.springcloud.service;

import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;
import org.springframework.stereotype.Component;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/24 11:14
 * @Description:
 */
@Component
public class PaymentServiceImpl implements PaymentService {

    @Override
    public CommonResult<Payment> paymentSQL(Long id) {
        return new CommonResult(414, "服务降级返回，---paymentServiceImpl",
                new Payment(id, "error"));
    }
}
