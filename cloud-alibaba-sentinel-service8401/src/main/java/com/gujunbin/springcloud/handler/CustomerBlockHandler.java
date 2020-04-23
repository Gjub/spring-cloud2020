package com.gujunbin.springcloud.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.gujunbin.springcloud.entity.CommonResult;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/23 13:11
 * @Description:
 */

public class CustomerBlockHandler {

    public static CommonResult handlerException(BlockException exception){
        return new CommonResult(444, "客户自定义 ~ Global HandlerException---1");
    }

    public static CommonResult handlerException2(BlockException exception){
        return new CommonResult(444, "客户自定义 ~ Global HandlerException---2");
    }
}
