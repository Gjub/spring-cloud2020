package com.gujunbin.springcloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/26 13:38
 * @Description:
 */
@Configuration
@MapperScan(basePackages = "com.gujunbin.springcloud.mapper")
public class MybatisConfig {
}
