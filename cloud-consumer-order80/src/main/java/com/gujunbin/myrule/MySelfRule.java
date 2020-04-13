package com.gujunbin.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/12 11:38
 * @Description:
 */
@Configuration
public class MySelfRule {

    /**
     * 配置类完成后要在主启动类添加 @RibbonClient 使配置生效
     * @return
     */
    @Bean
    public IRule myRule(){
        return new RandomRule(); //定义为随机
    }
}
