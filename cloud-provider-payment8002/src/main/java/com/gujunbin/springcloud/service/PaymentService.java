package com.gujunbin.springcloud.service;

import com.gujunbin.springcloud.entity.Payment;
import org.apache.ibatis.annotations.Param;

/**
 * (Payment)表服务接口
 *
 * @author GuJunBin
 * @since 2020-04-08 17:43:23
 */
public interface PaymentService {

    public int create(Payment payment);

    public Payment getPaymentById(@Param("id") Long id);
}