package com.gujunbin.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/22 16:37
 * @Description:
 */
@Slf4j
@RestController
public class FlowLimitController {

    @GetMapping("/testA")
    public String testA(){
        try {
            TimeUnit.MILLISECONDS.sleep(800);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return "-----testA";
    }

    @GetMapping("/testB")
    public String testB(){
        log.info(Thread.currentThread().getName()+"/t ----testB");
        return "-----testB";
    }

    @GetMapping("/testD")
    public String testD(){
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        log.info("TestD 测试RT（平均响应时间）");
        return "-----testD";
    }

    /**
     * 热点参数规则配置
     * @SentinelResource value：资源名 blockHandler：自定义消息
     * @param p1
     * @param p2
     * @return
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2){
        return "-----testHotKey\t" + p1 + "===.===" + p2;
    }

    public String deal_testHotKey(String p1, String p2, BlockException blockException){
        return "dealTestHotKey,  o(╥﹏╥)o ！！\t" + p1 + "===.===" + p2;
    }
}
