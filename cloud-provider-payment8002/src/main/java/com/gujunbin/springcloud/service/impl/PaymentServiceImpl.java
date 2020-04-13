package com.gujunbin.springcloud.service.impl;

import com.gujunbin.springcloud.entity.Payment;
import com.gujunbin.springcloud.mapper.PaymentMapper;
import com.gujunbin.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (Payment)表服务实现类
 *
 * @author GuJunBin
 * @since 2020-04-08 17:43:24
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentMapper paymentMapper;


    @Override
    public int create(Payment payment) {
        return  paymentMapper.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentMapper.getPaymentById(id);
    }
}