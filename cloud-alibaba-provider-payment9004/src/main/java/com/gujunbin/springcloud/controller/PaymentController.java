package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/24 10:46
 * @Description:
 */
@RestController
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    public static HashMap<Long, Payment> hashMap = new HashMap<>();

    static {
        hashMap.put(1L, new Payment(1L, "dsadsa4d4sa8484dsa6d4"));
        hashMap.put(2L, new Payment(2L, "fdgfdgfdgfdgf54848gfg"));
        hashMap.put(3L, new Payment(3L, "ythh54861fds4g8fg46ew"));
    }

    @GetMapping("/paymentSQL/{id}")
    public CommonResult<Payment> paymentSQL(@PathVariable("id") Long id) {
        Payment payment = hashMap.get(id);
        CommonResult<Payment> result = new CommonResult(200, "from mysql, serverPort: " + serverPort, payment);
        return result;
    }
}
