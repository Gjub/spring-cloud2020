package com.gujunbin.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/12 18:19
 * @Description:
 */
public interface ILoadBalancer {

    ServiceInstance instances(List<ServiceInstance> serviceInstances);
}
