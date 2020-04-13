package com.gujunbin.springcloud.controller;

import com.gujunbin.springcloud.lb.ILoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.gujunbin.springcloud.entity.CommonResult;
import com.gujunbin.springcloud.entity.Payment;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

/**
 * @Author: GuJunBin
 * @Date: 2020/4/8 19:36
 * @Description:
 */

@RestController
@Slf4j
public class OrderController {

//    public static final String PAYMENT_URL= "http://localhost:8001";
    /**
     * 更改多个端口后 要在 ApplicationContextConfig 配置中
     * 使用 @LoadBalanced 赋予 RestTemplate 负载均衡的能力
     */

    @Resource
    private ILoadBalancer loadBalancer;

    @Resource
    private DiscoveryClient discoveryClient;

    public static final String PAYMENT_URL= "http://CLOUD-PROVIDER-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        log.info("*******消费者启动创建订单*******");
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
    }

    /**
     * restTemplate.getForObject
     *      返回对象为响应体中数据转化成的对象  基本上可以理解为 Json
     *
     * restTemplate.getForEntity
     *      返回对象为ResponseEntity对象，包含了响应中的一些重要信息，如：响应头、响应状态码、响应体等
     */
    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }


    @GetMapping("/consumer/payment/getForEntity/{id}")
    public CommonResult<Payment> getPayment2(@PathVariable("id") Long id){
        ResponseEntity<CommonResult> forEntity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        log.info(forEntity.getStatusCode()+"\t"+forEntity.getHeaders());
        if (forEntity.getStatusCode().is2xxSuccessful()){
            return forEntity.getBody();
        }else {
            return new CommonResult<>(444,"调用失败");
        }
    }

    @GetMapping("/consumer/payment/lb")
    public String getPaymentLB(){
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PROVIDER-SERVICE");
        if (instances == null && instances.size() < 0){
            return "未发现服务";
        }
        ServiceInstance instances1 = loadBalancer.instances(instances);
        URI uri = instances1.getUri();
        return restTemplate.getForObject(uri+"/payment/lb",String.class);
    }

}