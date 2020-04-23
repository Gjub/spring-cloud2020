package com.gujunbin.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;
import com.gujunbin.springcloud.handler.CustomerBlockHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/23 12:43
 * @Description:
 */
@RestController
public class RateLimitController {

    @GetMapping("/byResource")
    @SentinelResource(value = "byResource", blockHandler = "handleException")
    public CommonResult byResource() {
        return new CommonResult(200, "按资源名称限流测试OK！"
                , new Payment(2020L, "sentinel"));
    }

    public CommonResult handleException(BlockException blockException) {
        return new CommonResult(411, blockException.getClass().getCanonicalName() + "\t 服务不可用~");
    }

    @GetMapping("/byURL")
    @SentinelResource(value = "byURL")
    public CommonResult byURL() {
        return new CommonResult(200, "按 URL 限流测试OK！",
                new Payment(2020L, "sentinel"));
    }

    @GetMapping("/customerBlockHandler")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException2")
    public CommonResult customerBlockHandler() {
        return new CommonResult(200, "客户自定义~"
                , new Payment(2020L, "sentinel"));
    }

}
