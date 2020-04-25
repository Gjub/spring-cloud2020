package com.gujunbin.springcloud.service;

import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/24 11:13
 * @Description:
 */
@FeignClient(value = "nacos-cloud-payment-provider", fallback = PaymentServiceImpl.class)
public interface PaymentService {

    @GetMapping("/paymentSQL/{id}")
    CommonResult<Payment> paymentSQL(@PathVariable("id") Long id);
}
