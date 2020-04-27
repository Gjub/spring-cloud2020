package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Order;
import com.gujunbin.springcloud.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 15:47
 * @Description:
 */
@RestController
public class SeataOrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/order/create")
    public CommonResult create(Order order){
        orderService.create(order);
        return new CommonResult(200, "订单创建成功");
    }
}

