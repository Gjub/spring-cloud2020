package com.gujunbin.springcloud.service.impl;

import com.gujunbin.springcloud.service.IProviderMessage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/18 10:56
 * @Description: 消息推送管道
 */

@EnableBinding(Source.class)  //调用消息中间件的service
public class ProviderMessageImpl implements IProviderMessage {

    @Resource
    private MessageChannel output;

    @Override
    public String send() {
        String s = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(s).build());
        System.out.println("**** serial ****: " + s);
        return s;
    }
}
