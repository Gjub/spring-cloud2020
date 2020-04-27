package com.gujunbin.springcloud.service;

import com.gujunbin.springcloud.entity.Order;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 13:29
 * @Description:
 */
public interface OrderService {

    /**
     * 创建订单
     * @param order
     */
    void create(Order order);
}

