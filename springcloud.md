
# [项目脑图](https://www.processon.com/mindmap/5ea50552e0b34d05e1adfcfc)

# [SpringCloud中文文档](https://www.springcloud.cc/)
## maven的聚合工程(spring-cloud2020)

dependencyManagement: 提供了一种管理依赖版本号的方式。

```
 <dependencyManagement>
        <dependencies>
            <!--spring cloud Hoxton.SR1-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Hoxton.SR1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```


- 一般用在聚合工程的父工程中，用于同一版本号管理
- 子项目再次引入此依赖jar包时则无需显式的列出版本号。Maven会沿着父子层级向上寻找拥有dependencyManagement 元素的项目，然后使用它指定的版本号。
- 这个标签只依赖的声明，并不实现引入，因此子项目需要显式的声明需要用的依赖（ga）。


```
        <!--eureka server-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
```

创建好父项目后 clean之后 install 中间跳过test节约时间 统一管理子模块的版本

----------
# cloud-api-commons 公共实体类

提取公共实体类 install 在项目导入依赖即可

```
        <dependency><!-- 自定义实体类 api -->
            <groupId>com.gujunbin.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
```

---------

# SpringCloud 微服务组件    

服务注册中心 | 服务调用 | 服务降级 | 服务网关 | 服务配置 | 服务总线
---|---|---|---|---|---
× Eureka | Ribbon | × Hystrix | × Zuul | × Config | × Bus
 Zookeeper | LoadBalancer | Resilience4j | Zuul2 | ⭐  Nacos |  ⭐  Nacos 
 Consul | × Feign | ⭐   Sentinel | Getway 
⭐  Nacos |  OpenFeign  


# Eureka

Eureka是通过客户端发送心跳来维持连接的，如果过了一段时间，客户端未给出任何反应，Eureka不会 将该服务直接注销，而是会选择保留该微服务的信息。所以会给出警告，可以通过关闭自我保护机制实现消除警告（不推荐），这种机制属于微服务CAP里面的==AP==机制，保证其==高可用==。

## cloud-eureka-sercer7001 Eureka服务端
### pom.xml
```
        <!--eureka server 服务端依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
```
### application.yaml

```
# 服务端口号
server:
  port: 7001
  
# Eureka
eureka:
  instance:
    hostname: eureka7001.com
  server:
    # 关闭eureka的自我保护机制，默认开启为true
    enable-self-preservation: false
    # 过了客户端的等待时间后两秒后就关闭
    eviction-interval-timer-in-ms: 2000

  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false 表示自己就是注册中心，职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #集群指向其他 Eureka
      #defaultZone: http://eureka7002.com:7002/eureka/
      #单机指向自己
      defaultZone: http://eureka7001.com:7001/eureka/

```
hostname 映射 eureka7001.com 需在本地（C:\Windows\System32\drivers\etc） hosts 文件添加路径

> ###########SpringCloud2020#############<br>
> 127.0.0.1   eureka7001.com<br>
> 127.0.0.1   eureka7002.com

### SpringBoot启动类
- @EnableEurekaServer 开启 Eureka 的注册管理服务端

```
@SpringBootApplication
@EnableEurekaServer
public class EurekaMain7001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaMain7001.class, args);
    }
}
```
## cloud-eureka-sercer7002 Eureka服务端
与7001相互注册关联实现集群，仅需修改 yaml 文件

```
server:
  port: 7002

eureka:
  instance:
    hostname: eureka7002.com
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false 表示自己就是注册中心，职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    # 将7001注册到7002中
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/
```

## cloud-paovider-payment8001 支付微服务

- 创建典型的mvc工程
- 实现两个方法
    - 新建账单
    - 通过id查询账单
    
### pom.xml

```
        <!--eureka server 客户端依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

### application.yaml


```
server:
  port: 8001
  
# 服务别名
spring:
  application:
    name: cloud-provider-service
    
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource      #当前数据源操作类型
    driver-class-name: org.gjt.mm.mysql.Driver        #mysql驱动包
    url: jdbc:mysql://localhost:3306/db_springcloud?useUnicode=true&characterEncoding-utr-8&useSSL=false
    username: root
    password: root

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.gujunbin.springcloud.entity      #所有Entity别名类所在包

eureka:
  client:
    #表示是否将自己注册进EurekaServer默认为true
    register-with-eureka: true
    #是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetch-registry: true
    service-url:
      #集群版
      #defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
      #单机版
      defaultZone: http://localhost:7001/eureka
  instance:
    # 修改eureka服务的status名称
    instance-id: payment8001
    #访问路径可以选择IP地址
    prefer-ip-address: true
    # 客户端向注册中心间隔多久发送一次心跳，默认为30s
    lease-renewal-interval-in-seconds: 1
    # 客户端在不发送心跳后，注册中心需要等待的时间
    lease-expiration-duration-in-seconds: 2
```

### SpringBoot启动类
-  @EnableDiscoveryClient、@EnableEurekaClient： 都是能够让注册中心能够发现，扫描到该服务。
- @EnableEurekaClint &nbsp;&nbsp;&nbsp; 适用于服务注册中心采用Eureka的服务
- @EnableDiscoveryClient &nbsp;&nbsp; 支持其他注册中心的服务

```
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class PaymentMain8001 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8001.class, args);
    }
}

```

### PaymentController

> create(==@RequestBody== Payment payment) {...}   
    当80端口远程调用8001端口的create方法时    
    没有使用@RequestBody注解，会造成插入空值的情况

```
@Slf4j
@RestController
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    // 注入服务发现客户端
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);
        log.info("*******插入结果为: " + result);
        if (result > 0) {
            return new CommonResult(200, "插入数据库成功,serverPort:" + serverPort, result);
        } else {
            return new CommonResult(444, "插入数据库失败", null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment paymentById = paymentService.getPaymentById(id);
        log.info("*********查找结果为: " + paymentById);
        if (paymentById != null) {
            return new CommonResult(200, "查找成功,serverPort:" + serverPort, paymentById);
        } else {
            return new CommonResult(444, "查找为空,查找ID为: " + id, null);
        }
    }

    @GetMapping(value = "/payment/discovery")
    public Object discovery() {
        //获取服务清单列表
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("***********service:" + service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PROVIDER-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;
    }
}
```

## cloud-paovider-payment8002 支付微服务
同 8001 ，更改端口为 8002    
用于实现服务的负载均衡

### cloud-consumer-order80 客户订单服务

   > 远程调用 8001/8002 支付微服务 创建/查询订单

### pom.xml

```
        <!--eureka-client-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```


### application.yaml
```
server:
  port: 80

spring:
  application:
    name: cloud-order-server

eureka:
  client:
    #表示是否将自己注册进EurekaServer默认为true
    register-with-eureka: true
    #是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/ #集群版
#      defaultZone: http://localhost:7001/eureka
```
### ApplicationContextConfig

@LoadBalanced：实现负载均衡，默认是轮询算法 交替访问8001、8002

```
@Configuration
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

### OrderController

将RestTemplate注入到spring容器内，然后调用payment服务里面的
controller通过getForObject时，因 @GetMapping 不能传参数，需要在路径后面拼接 +id 


**PAYMENT_URL= "http://CLOUD-PROVIDER-SERVICE";**   
将单端口改为多端口    
要在 ApplicationContextConfig 配置中使用 ==@LoadBalanced==赋予RestTemplate==负载均衡==的能力    
否则只会访问单端口  不会实现负载均衡

```
@RestController
@Slf4j
public class OrderController {

//    public static final String PAYMENT_URL= "http://localhost:8001";
   
    public static final String PAYMENT_URL= "http://CLOUD-PROVIDER-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public CommonResult<Payment> create(Payment payment){
        log.info("*******消费者启动创建订单*******");
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }
}
```


---
## Linux JDK环境搭建
1. 下载jdk安装包：链接：https://pan.baidu.com/s/102h14-Z2D2l6vTawr-pyEA　　         提取码：air3

1. 进入存放压缩包的文件夹，解压到当前目录

```
- tar -zxvf jdk-8u221-linux-x64.tar.gz
```
###  配置环境变量
1. 进入当前的用户目录
2. 进入该目录，找到.bash_profile文件，执行命令

```
[root@localhost ~]# cd /root
[root@localhost ~]# ls -a
.                .bash_logout   .config    .ICEauthority         .pki        
..               .bash_profile  .cshrc     initial-setup-ks.cfg  .tcshrc      
anaconda-ks.cfg  .bashrc        .dbus      .local                .viminfo     
.bash_history    .cache         .esd_auth  .oracle_jre_usage     .Xauthority 
[root@localhost ~]# vi .bash_profile 
```

##### 进入编辑文件块，输入:

```
#PATH=$PATH:$HOME/.local/bin:$HOME/bin
#export PATH
（－－注意注释－－）
export JAVA_HOME=/home/studylinux/java/jdk1.8.0_221
export JRE_HOME=/home/studylinux/jdk1.8.0_221/jre
export CLASS_PATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH
```
- 保存退出
```
#查看内容
cat .bash_profile 

#使配置的环境变量生效
source .bash_profile 

#查看jdk是否安装成功
java -version
```
-------
# Zookeeper


> zookeeper作为服务注册中心，微服务作为节点注册进来，那这个节点是==临时的==,只要一段时间你的服务不重新传回心跳,zookeeper就会将你剔除,当下次你进来重新注册

### pom.xml
**注意：客户端自带3.5.3版本zookeeper 与安装版本会冲突  解决方法如下**
```
<!--springboot整合zookeeper客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
            <!--先排除自带的 zookeeper 3.5.3-->
            <exclusions>
                <exclusion>
                    <groupId>org.apache.zookeeper</groupId>
                    <artifactId>zookeeper</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- 导入zookeeper 3.4.14 版本-->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.14</version>
            <exclusions>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```
### application.yaml
192.168.78.128:2181 是Linux的IP和端口
```
server:
  port: 8004

#服务别名----注册zookeeper到注册中心名称
spring:
  application:
    name: zookeeper-provider-payment
  cloud:
    zookeeper:
      connect-string: 192.168.78.128:2181
```
### Linux ZookeeperClient
```
# 关闭防火墙
systemctl stop firewalld

# 查看防火墙状态
systemctl status firewalld

# 进入zookeeper-3.4.6/bin目录，启动zookeeper服务。
cd /usr/local/zookeeper/zookeeper-3.4.6/bin
./zkServer.sh start

# 启动完成后，查看服务状态。
./zkServer.sh status

#进入Zookeeper客户端
./zkCli.sh

[zk: localhost:2181(CONNECTED) 6] ls /
[services, zookeeper]

[zk: localhost:2181(CONNECTED) 7] ls /services
[cloud-provider-payment]

[zk: localhost:2181(CONNECTED) 9] ls /services/cloud-provider-payment
[c80c7e0c-17a4-4fc6-9ee1-9270c02a32a6]
```
----
# Consul

官网下载解压后得到 consul.exe 文件

在 consul.exe 文件目录下进入cmd


```
#查看版本
consul --version

#启动服务
consul agent -dev
```
# cloud-provider-consul-payment8006 服务注入到consul注册中心
### pom.xml

```
        <!--SpringCloud consul-server-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
```
### application.yaml

```
server:
  port: 8006

spring:
  application:
    name: consul-provider-payment
  # consul注册中心地址
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        #hostname: 127.0.0.1
        service-name: ${spring.application.name}
```


## ==注册中心Eureka、Zookeeper、Consul的异同点==

组件名 | 语言 | CAP | 服务监控检查 | 对外暴露接口 | Springcloud集成
--|--|--|--|--|--
Eureka   |Java | AP | 可配支 | HTTP     |已集成
Consul   |	Go | CP	| 支持   | HTTP/DNS |已集成
Zookeeper|Java | CP | 支持   | 客户端	|已集成


## CAP

> CAP原则又称CAP定理，指的是在一个分布式系统中，一致性（Consistency）、可用性（Availability）、分区容错性（Partition tolerance）。<br>
CAP 原则指的是，这三个要素最多==只能同时实现两点==，不可能三者兼顾。

- C：Consistency （强一致性）
- A：Available （可用性）
- P：Partition tolerance （分区容错性）

---

CAP理论提出就是针对分布式数据库环境的，所以，P这个属性是必须具备的。  <br>
-  　 AP：eureka　要高可用并允许分区，则需放弃一致性。一旦分区发生，节点之间可能会失去联系，为了高可用，每个节点只能用本地数据提供服务，而这样会导致全局数据的不一致性。现在众多的NoSQL都属于此类。  
  　 


-  　 CP：zookeeper/consul　不要求A（可用），相当于每个请求都需要在Server之间强一致，而P（分区）会导致同步时间无限延长，如此CP也是可以保证的。很多传统的数据库分布式事务都属于这种模式。
----
# Ribbon

**Spring Cloud Ribbon 是基于Netflix Ribbon 实现的一套客户端 负载均衡的工具。**    
Ribbon 是 Netflix 发布的开源项目，主要功能是提供==客户端==的软件==负载均衡算法==和==服务调用==。Ribbon 客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出**Load Balancer**（简称LB）后面所有的机器，Ribbon 会自动的帮助你基于某种规则（如简单轮询、随机连接等）去连接这些机器。我们很容易使用Ribbon实现自定义的负载均衡算法。

##### LB负载均衡（Load Balance）
简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的==HA（高可用）==。    
常见的负载均衡有软件 Nginx，LVS，硬件F5 等。

- 集中式B
     - 即在服务的消费方和提供方之间使用独立的LB设施（可以是硬件，如F5，也可以是软件，如Nginx）,由该设施负责把访问请求通过某种策略转发至服务的提供方
 - 进程内LB
     - 将 LB 逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。Ribbon就属于进程内 LB ，它只是一个类库，集成与消费方进程，消费方通过它来获取到服务提供方的地址。

**==Ribbon 就是 负载均衡 + RestTemplate调用，最终实现RPC的远程调用。==**

> 注： Eureka 内置Ribbon  无需导入依赖 

#### Ribbon的负载均衡机制 IRule

Ribbon 提供了几个负载均衡的组件，其目的就是让请求转给合适的服务器处理，因此，如何选择合适的服务器变成了负载均衡机制的核心，Ribbon 提供了如下负载均衡规则：

- RoundRobinRule：默认规则，通过简单的轮询服务列表来选择服务器
- RandomRule：随机选择可用服务器
- AvailabilityFilteringRule：可用性筛选规则
   -  忽略无法连接的服务器，默认情况下，如果3次连接失败，该服务将会被置为"短路"的状态，该状态持续30秒；如果再次连接失败，"短路"状态的持续时间将会以几何级数增加，可以通过 niws.loadbalancer.<clientName>.connectionFailureCountThreshold 属性，来配置连接失败的次数；
    - 忽略并发过高的服务器，如果连接到该服务器的并发数过高，也会被这个规则忽略，可以通过修改 <clientName>.ribbon.ActiveConnectionsLimit 属性来设定最高并发数。
- WeightedResponseTimeRule：为每个服务器赋予一个权重值，服务器的响应时间越长，该权重值就越少，这个规则会随机选择服务器，权重值有可能会决定服务器的选择
- ZoneAvoidanceRule：该规则以区域、可用服务器为基础进行服务器选择，使用区域（Zone）对服务器进行分类
- BestAvailableRule：忽略"短路"的服务器，并选择并发数较低的服务器
- RetryRule：含有重试的选择逻辑，如果使用 RoundRobinRule 选择的服务器无法连接，那么将会重新选择服务器

### Ribbon 规则替换

> 这个自定义配置类不能放在 @ComponentScan 所扫描的当前包下以及子包下，否则自定义的配置类就会被所有的 Ribbon 客户端所共享，达不到特殊化定制的目的了。

在启动类的同级目录下新建 myrule包 自定义 MySelfRule配置类

```
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

```
> 注：在==主启动类添加 @RibbonClient==使配置生效


# RestTemplate

### RestTemplate调用
```
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
```

# OpenFeign

Feign是一种声明式、模板化的HTTP客户端。在Spring Cloud中使用Feign，可以做到使用HTTP请求访问远程服务，就像调用本地方法一样的，开发者完全感知不到这是在调用远程方法，更感知不到在访问HTTP请求。

后面有结合Hystrix和它的fallback降级实现

### pom.xml

```
        <!--openfeign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```
### application.yml
```
server:
  port: 80

eureka:
  client:
    register-with-eureka: false
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka

#设置feign 客户端超时时间（openFeign默认支持ribbon）
ribbon:
  #指的是建立连接后从服务器读取到可用资源所用的时间(毫秒)
  ReadTimeout: 5000
  #指的是建立连接所用的时间，适用于网络状况正常的情况下，两端连接所用的时间
  ConnectTimeout: 5000
logging:
  level:
    #Feign日志 以什么级别监控哪个接口
    com.gujunbin.springcloud.service.PaymentFeignService: debug
```
### SpringBoot 启动类

老规矩加入新组件依赖     
注解@EnableFeignClients启用feign客户端

```
@SpringBootApplication
@EnableFeignClients
public class OrderFeignMain80 {

    public static void main(String[] args) {
            SpringApplication.run(OrderFeignMain80.class,args);
        }
}
```
### FeignConfig 配置类

```
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}

```
### OrderFeignController

```
@RestController
public class OrderFeignController {

    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> paymentCommonResult(@PathVariable("id") Long id){
        CommonResult paymentById = paymentFeignService.getPaymentById(id);
        return paymentById;
    }

    @GetMapping("/consumer/payment/feign/timeout")
    public String paymentFeignTimeOut(){
        //OpenFeign-Ribbon, 客户端一般默认等待1秒钟
        return paymentFeignService.paymentFeignTimeOut();
    }
}

```
------------
# Hystrix
- Hystrix是一个用于==处理分布式系统的延迟和容错的开源库==,在分布式系统里,许多依赖不可避免的会调用失败，比如超时、异常等
- Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障,以提高分布式系统的弹性。

“断路器”本身是一种开关装置,当某个服务单元发生故障之后，通过断路器的故障监控(类似熔断保险丝)，**向调用方返回一个符合预期的、可处理的备选响应(FallBack)** ，而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要地占用,从而避免了故障在分布式系统中的蔓延,乃至雪崩。

1. Fallback 服务降级 ：（程序异常、超时、服务熔断触发服务降级、线程池/信号量打满） 服务器忙，请稍后再试，等友好的提示
1. Break 服务熔断 ： 类比保险丝达到最大服务访问，直接拒绝。然后调用服务降级的方法返回友好提示
1. FlowLimit 服务限流 : 秒杀、高并发等操作，排队一秒N个有序进入 
-------
# cloud-provider-hystrix-payment8001 支付端微服务降级
### pom.xml

```
        <!--hystrix-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
```
### applicatiom.yml

```
server:
  port: 8001

# 服务名称
spring:
  application:
    name: CLOUD-PROVIDER-HYSTRIX-PAYMENT
eureka:
  client:
    # 入驻eureka
    register-with-eureka: true
    # 去获取已注册的服务，在集群的时候，每个微服务都需要设置为true，配合ribbon进行负载均衡
    fetch-registry: true
    # 将自己注册进的 eureka服务管理中心 的url
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka
      #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
```
### SpringBoot启动类

==降级激活==
@EnableCircuitBreaker

```
@SpringBootApplication
@EnableEurekaClient
// 降级激活
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {

    public static void main(String[] args) {
            SpringApplication.run(PaymentHystrixMain8001.class,args);
        }
}

```
### PaymentService

```
@Service
public class PaymentService {

    /**
     * 正常访问
     *
     * @param id
     * @return java.lang.String
     * @author GuJunBin
     */
    public String paymentInfo_OK(Integer id) {
        return "线程池：" + Thread.currentThread().getName() +
               "  paymentInfo_OK,id:  " + id + "\t" + "O(∩_∩)O哈哈~";
    }

    /**
     * 超时访问
     * fallbackMethod = "paymentInfo_TimeOutHandler"
     *
     * @param id
     * @return java.lang.String
     * @author GuJunBin 
     */
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler",commandProperties ={
            @HystrixProperty(name ="execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    public String paymentInfo_TimeOut(Integer id) {

        /**
         * 模拟线程异常 也会跳转至 paymentInfo_TimeOutHandler兜底方法
         * int age= 1/0;
         */

        Integer Time = 1;
        //模拟线程耗时
        try {
            TimeUnit.SECONDS.sleep(Time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() +
               " id: " + id + "\t" + "(*^▽^*)  耗时（s）：" + Time;
    }

    public String paymentInfo_TimeOutHandler(Integer id) {
        return "程序运行繁忙或报错,请稍后再试*****" + "当前线程: " +
                Thread.currentThread().getName() + id + "\t " + "(꒦_꒦) )";
    }
}
```
# cloud-consumer-feign-hystrix-order80 订单端降级处理

### pom.xml

```
    <!--    openfeign    -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!--   hystrix     -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency>
```
### application.yml

```
server:
  port: 80


eureka:
  client:
    register-with-eureka: false
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka

feign:
  hystrix:
    enabled: true

```
### SpringBoot启动类

```
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class OrderHystrixMain80 {

    public static void main(String[] args) {
            SpringApplication.run(OrderHystrixMain80.class,args);
        }
}
```
### PaymentHystrixService 

```
@Component
@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT",fallback = PaymentHystrixServiceImpl.class)
public interface PaymentHystrixService {

    @GetMapping("/payment/hystrix/ok/{id}")
    String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/payment/hystrix/timeOut/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id);
}

```
### OrderHystrixController

 - 这种注解适用特定的方法 不然一个方法一个注解 代码膨胀
@HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
})
 - 可以采用在类上加全局默认降级注解    
@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")

 - 最好是采用service实现类实现     
@FeignClient(value = "...",fallback=PaymentHystrixServiceImpl.class)

   

```
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
public class OrderHystrixController {

    @Resource
    private PaymentHystrixService paymentHystrixService;

    @GetMapping(value = "/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id) {
        String result = paymentHystrixService.paymentInfo_OK(id);
        log.info("**********Result:" + result);
        return result;
    }

    @GetMapping("/consumer/payment/hystrix/timeOut/{id}")
   /* @HystrixCommand(fallbackMethod = "paymentTimeoutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    })*/
    // @HystrixCommand 没有用上面特别指明 就默认全局通用的降级处理
    @HystrixCommand
    public String paymentTimeout(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentInfo_TimeOut(id);
    }

    public String paymentTimeoutHandler(@PathVariable("id") Integer id) {
        return "我是消费端的80接口，对方的支付系统繁忙，请稍后再试...┭┮﹏┭┮...";
    }

    /**
     * 全局通用降级处理
     * 该类加上  @DefaultProperties(defaultFallback = "paymentGlobalTimeoutHandler")
     * 默认全局 方法上加 @HystrixCommand
     *
     * @return
     */
    public String paymentGlobalTimeoutHandler() {
        return "Global全局降级处理，请稍后再试.../(⊙︿⊙)/";
    }

}

```


### PaymentHystrixServiceImpl 解耦实现全局降级处理

```
@Service
public class PaymentHystrixServiceImpl implements PaymentHystrixService {

    @Override
    public String paymentInfo_OK(Integer id) {
        return "----PaymentHystrixServiceImpl  Fall Back-paymentInfo_OK ~~~ o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "----PaymentHystrixServiceImpl  Fall Back-paymentInfo_TimeOut ~~~ o(╥﹏╥)o";
    }
}
```
-------------

# cloud-provider-hystrix-payment8001 8001端口自测服务熔断
### 熔断机制概述

> 熔断机制是应对雪崩效应额一种微服务链路保护机制。当扇出链路的某个微服务出错不可用或者响应时间太长，会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的响应信息。当检测到该节点微服务调用响应正常后，==恢复调用链路==。

#### Hystrix为我们实现了自动恢复功能。

- 当断路==打开==,对主逻辑进行熔断之后，hystrix会启动一个休眠时间窗在这个时间窗内,降级逻辑为临时的主逻辑。
- 当休眠时间窗到期，断路器将进入==半开==状态,释放一次请求到原来的主逻辑上
    - 如果此次请求正常返回,那么断路器将继续==闭合==,主逻辑恢复。
    - 如果这次请求依然有问题，断路器继续进入打开状态,休眠时间窗重新计时。

### 断路器的三个重要参数：
- **快照时间窗**:断路器统计一些请求和错误数据，而统计的时间范围就是快照时间窗,默认为最近的10秒。
- **请求总数阀值**:在快照时间窗内，必须满足请求总数阀值才有资格熔断。默认为20, 意味着在10秒内,如果该hystrix命令的调用次数不足20次,即使所有的请求都超时或其他原因失败，断路器都不会打开。
- **错误百分比阀值**:当请求总数在快照时间窗内超过了阀值，比如发生了30次调用，如果在这30次调用中，有15次发生了超时异常,也就是超过50%的错误百分比，在默认设定50%阀值情况下，这时候就会将断路器打开。

### PaymentService
  
```
    // 服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerFallback", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),//是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),// 请求总数阈值
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//快照时间窗
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")//错误百分比阈值
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        if (id < 0) {
            throw new RuntimeException();
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t " + "调用成功,流水号: " + serialNumber;
    }

    public String paymentCircuitBreakerFallback(@PathVariable("id") Integer id) {
        return "id不能为负数,请稍后再试~ id: " + id;
    }
```
### PaymentController
调用服务熔断方法
```
    // 服务熔断
    @GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        String circuitBreaker = paymentService.paymentCircuitBreaker(id);
        log.info("******result: " + circuitBreaker);
        return circuitBreaker;
    }
```
----------

# SpringCloud [Gateway](https://cloud.spring.io/spring-cloud-gateway/2.2.x/reference/html/)

>  类似Nginx的网关路由代理    
它是基于异步非阻塞模型上进行开发的

##### 三大核心概念
 - Route（路由）
     - 路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true则匹配该路由
 - Predicate（断言）
     - 开发人员可以匹配HTTP请求中的所有内容（例如请求头或请求参数），如果请求与断言相匹配则进行路由
 - Filter（过滤）
     - 指的是Spring框架中GatewayFilter的实例，使用过滤器，可以在请求被路由前或者之后对请求进行修改
     
### pom.xml
这个工程不需要web依赖 导入会报错哦
```
<!--gateway-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
### application.yaml

```
server:
  port: 9527

spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true # 开启从注册中心动态动态创建路由的功能,利用微服务名进行路由
      routes:
        - id: payment_routh #payment_routh    # 路由的ID，没有固定规则但要求唯一，简易配合服务名
          uri: lb://CLOUD-PROVIDER-SERVICE   # URI的协议为lb（LoadBalance） 表示启用 Getaway的负载均衡功能 实现动态路由
          # uri: http://localhost:8001         # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/get/**          # 断言，路径相匹配的进行路由

        - id: payment_routh2 #payment_routh   # 路由的ID，没有固定规则但要求唯一，简易配合服务名
          uri: lb://CLOUD-PROVIDER-SERVICE   # URI的协议为lb 表示启用 Getaway的负载均衡功能
          # uri: http://localhost:8001          # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/lb/**             # 断言，路径相匹配的进行路由

#            - After=2020-04-15T11:46:19.781+08:00[Asia/Shanghai] # 当前时区之后才能访问
#            - Between=2020-04-15T11:46:19.781+08:00[Asia/Shanghai],2020-04-15T12:46:19.781+08:00[Asia/Shanghai]

#            - Cookie=username,gujunbin
#            cmd 访问 curl http://localhost:9527/payment/lb --cookie "username=gujunbin"

#            - Header=X-Request-Id, \d+
#              请求头需要有X-Request-Id属性名,值为整数正则表达式
#              cmd curl http://localhost:9527/payment/lb -H "X-Request-Id:1234"

#            - Method=GET
#            - Query=username, \d+  # 要参数名username且值为正数  http://localhost:9527/payment/lb?username=11

eureka:
  instance:
    hostname: cloud-gateway-service
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址
      #      defaultZone: http://eureka7002.com:7002/eureka/
      #      单机版eureka
      defaultZone: http://eureka7001.com:7001/eureka/
```
### GetawayConfig 配置类实现路由转发

```
    /***
     * 配置了 id 为 path_route_baidu 的路由规则
     * 当访问 localhost：9527/guonei 时 会转发至下面配置的 URI
     * 
     * @param routeLocatorBuilder 
     * @return org.springframework.cloud.gateway.route.RouteLocator
     * @author GuJunBin
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();

        routes.route("path_route_baidu",
                r -> r.path("/guonei")
                        .uri("http://news.baidu.com/guonei")).build();
        return routes.build();
    }
```
### MyGateWayFilter 自定义Filter

主要实现GlobalFilter、Order两个接口

```
@Component
@Slf4j
@Order(0)
public class MyGateWayFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("********Welcome to MyGateWayFilter :" + new Date());
        String username = exchange.getRequest().getQueryParams().getFirst("username");
        if( null == username ){
            log.warn("****警告：用户名为 NULL 非法用户 [○･｀Д´･ ○]");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
```

--------------

# [SpringCloud Config](https://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.2.2.RELEASE/reference/html/) 

- 分布式架构面临的问题    
> 微服务意味着要将单体应用中的业务拆分成一个个子服务,每个服务的粒度相对较小，因此系统中会出现大量的服务。于每个服务都需要必要的配置信息才能运行，所以一套==集中式的、动态的配置管理==设施是必不可少的。

## springcloud-config 

1. 在 GitHub 新建 springcloud-config 仓库
2. 新建config-dev.yml、config-prod.yml、config-test.yml
3. 分别设置端口为 3333、4444、5555
4. 提交至springcloud-config master分支
5. 修改hosts本地端口映射 127.0.0.1   config-3344.com

## cloud-config-center3344 服务端中心配置

### pom.xml

```
    <!-- server 服务端配置 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
```
### application.yml

```
server:
  port: 3344

spring:
  application:
    name: cloud-config-center
  cloud:
    config:
      server:
        git:
          uri: https://github.com/Gjub/springcloud-config.git # git仓库名字
          # 搜索目录
          search-paths:
            - springcloud-config
      # 读取分支
      label: master

eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka # 注册进eureka
```
###  ConfigCenterMain3344 启动类

激活启动后 可以访问 http://config-3344.com:3344/master/config-dev.yml 查看文件
```
@SpringBootApplication
// 激活配置中心
@EnableConfigServer
public class ConfigCenterMain3344 {

    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterMain3344.class, args);
    }
}
```

## cloud-config-client3355 客户端配置
### pom.yml
```
<!-- 客户端配置 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```
### bootstrap.yml
- applicaiton. yml 是用户级的资源配置项
- bootstrap. yml 是系统级的，优先级更加高    
==也就是说bootstrap. yml是先于applicaiton. yml加载的==
```
server:
  port: 3355

spring:
  application:
    name: config-client
  cloud:
    #Config客户端配置  http://config-3344.com:3344/master/config-dev.yml
    config:
      label: master #分支名称
      name: config #配置文件名称
      profile: dev #读取后缀名称 上述3个综合：master分支上config-dev.yml的配置文件被读取
      uri: http://localhost:3344 #配置中心地址 表示通过这个服务端访问

#服务注册到eureka地址
eureka:
  client:
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:7001/eureka
```
### ConfigCenterMain3355 启动类

```
@SpringBootApplication
@EnableEurekaClient
public class ConfigCenterMain3355 {

    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterMain3355.class, args);
    }
}
```
### ConfigClientController 客户端接口

```
@RestController
public class ConfigClientController {

    @Value("${server.port}")
    private String serverPort;  //访问并获取3344上的信息 即更改端口号

    @GetMapping("/serverPort")	//请求地址
    public String getServerPort(){
        return serverPort;
    }
}

```

## Config客户端动态刷新

1. 在GitHub 对配置文件进行修改
2. 刷新3344，发现ConfigServer会立刻响应
3. 刷新3355，发现ConfigClient没有响应  需要重新加载

## 修改3355模块

### pom引入actuator监控
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
### yml 新增配置

```
#暴露监控端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
### controller层新增一个注解 @RefreshScope

@RefreshScope 实现自动刷新

```
@RestController
@RefreshScope
public class ConfigClientController {
    .......
}
```
### 发送Post请求刷新3355
cmd 发送 
```
curl -X POST "http://localhost:3355/actuator/refresh"
```
---------
# [SpringCloud Stream](https://spring.io/projects/spring-cloud-stream#overview)

作用：屏蔽底层消息中间件的差异，统一消息的编程模型

## cloud-stream-rabbitMQ-provider8801 供应商-发送消息
### pom.xml

```
    <!-- rabbitMq的依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    </dependency>
```
### application.yml

```
server:
  port: 8801

spring:
  application:
    name: cloud-stream-provider

  cloud:
    stream:
      binders: #在此处配置要绑定的rabbitmq的服务信息
        defaultRabbit: #表示定义的命名，用于binding整合
          type: rabbit #消息组件类型
          environment: #设置rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: 192.168.78.128
                port: 5672
                username: guest
                password: guest
      bindings: #服务的整合处理
        output: #这个名字是一个通道的名称
          destination: studyExchange #表示要使用的Exchange名称定义
          content-type: application/json #设置消息类型，本次为json，文本则为 text/plain
          binder: defaultRabbit #设置要绑定的消息服务的具体设置

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 #心跳时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 5 #超时间隔（默认90秒）
    instance-id: send-8801.com #在信息列表时显示主机名称
    prefer-ip-address: true #访问路径变为ip地址

# 关闭健康检查
#management:
#  health:
#    rabbit:
#      enabled: false

```
### StreamMQ8801 启动类

QAQ：@EnableEurekaClient 这个注解不加也会注册进Eureka服务    
　　　　不要问，问就是　　我也不清楚

```
@SpringBootApplication
//@EnableEurekaClient
public class StreamMQ8801 {

    public static void main(String[] args) {
        SpringApplication.run(StreamMQ8801.class, args);
    }
}
```

### ProviderMessageImpl 消息推送管道实现类
@EnableBinding(Source.class)  绑定实现发送消息
```
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
```

###  SendMessageController 

```
@RestController
public class SendMessageController {

    @Resource
    private IProviderMessage providerMessage;

    @GetMapping("/send")
    public String send(){
        return providerMessage.send();
    }
}

```

## cloud-stream-rabbitMQ-consumer8802 客户1-接收消息
```
server:
  port: 8802

spring:
  application:
    name: cloud-stream-consumer

  cloud:
    stream:
      binders: #在此处配置要绑定的rabbitmq的服务信息
        defaultRabbit: #表示定义的命名，用于binding整合
          type: rabbit #消息组件类型
          environment: #设置rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: 192.168.78.128
                port: 5672
                username: guest
                password: guest
      bindings: #服务的整合处理
        input: #这个名字是一个通道的名称
          destination: studyExchange #表示要使用的Exchange名称定义
          content-type: application/json #设置消息类型，本次为json，文本则为 text/plain
          binder: defaultRabbit #设置要绑定的消息服务的具体设置
          group: consumerA #分组设置 还能持久化

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka
  instance:
    lease-renewal-interval-in-seconds: 2 #心跳时间间隔（默认30秒）
    lease-expiration-duration-in-seconds: 5 #超时间隔（默认90秒）
    instance-id: receive-8802.com #在信息列表时显示主机名称
    prefer-ip-address: true #访问路径变为ip地址

# 关闭健康检查
management:
  health:
    rabbit:
      enabled: false
```
### ReceiveMessageController 接收消息

```
@Component
@EnableBinding(Sink.class)
public class ReceiveMessageController {

    @Value("${server.port}")
    private String serverPort;

    @StreamListener(Sink.INPUT)
    public void receiveMessage(Message<String> message) {
        System.out.println("客户1 ：" + message.getPayload() + "\t port:" + serverPort);
    }
}

```


## cloud-stream-rabbitMQ-consumer8803 客户2-接收消息
问题：加入客户2后，默认是不同分组可以完全消费，导致出现重复消费    
方案：自定义配置同一组，只有一个能消费  同8802配置可以实现同组 （group：consumerA）

-------------
# SpringCloud Sleuth 链路跟踪
> Spring-Cloud-Sleuth是Spring Cloud的组成部分之一，为Spring Cloud应用实现了一种分布式追踪解决方案，其兼容了Zipkin, HTrace和log-based追踪

薛微了解一哈  本篇卒

-------
# [SpringCloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba/blob/master/README-zh.md)  走起

# Nacos 服务注册和配置中心
> Nacos = Eureka + Config + Bus  
 nacos/bin目录下启动shutdown.cmd    
 访问localhost:8848/nacos     
 默认账号：nacos 密码：nacos
 
 ==Nacos可以在AP(高可用)和CP(强一致)之间切换，一般是AP模式==
 
## cloud-alibaba-provider-payment9001
### pom.xml
```
        <!-- SpringCloud alibaba nacos依赖的引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```
### application.yml

```
server:
  port: 9001

spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848

# 暴露服务
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
最终实现以接口获取服务端口号

## cloud-alibaba-provider-payment9002
参照9001新建9002 演示负载均衡

## cloud-alibaba-consumer-nacos-order83
### application.yml

```
server:
  port: 83
spring:
  application:
    name: nacos-order-consumer
  cloud:
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848
# 消费者将去访问微服务的名称
service-url:
  nacos-user-service: http://nacos-payment-provider
```
Nacos 集成了Ribbon 当然能调用RestTemplate啦    
用RestTemplate.getForObject() 访问nacos-payment-provider的端口

## cloud-alibaba-config-nacos-client3377 配置中心   
### pom.xml

```
        <!-- SpringCloud alibaba nacos依赖的引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
```
### bootstrap.yaml

```
server:
  port: 3377

spring:
  application:
    name: nacos-config-client
  cloud:
    nacos:
      discovery:
        # 配置nacos地址
        server-addr: localhost:8848
      config:
        # 配置中心的地址
        server-addr: localhost:8848
        # 指定读取后缀名为 yaml的配置文件
        file-extension: yaml
        # 分组
        group: DEV_GROUP
        # 命名空间
        namespace: 98c28915-2bfb-404e-9fb9-8d40281d6608

# ${prefix}-${spring.profile.active}.${file-extension}  //官网配置公式
# ${spring.application.name}-${spring.profile.active}.${spring.cloud.nacos.config.file.extension}
# nacos-config-client-dev.yml
```
### application.yaml

```
spring:
  profiles:
    #active: info
    active: dev  #开发环境
    #active: test  #测试环境
```
在Nacos客户端新建配置文件 用3377的接口获取info  
访问优先级  namespace --> group --> DataID

# [Sentinel](https://github.com/alibaba/Sentinel/wiki/%E4%BB%8B%E7%BB%8D)

**Sentinel 以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。**

> 下载sentinel-dashboard-1.7.0.jar    
java -jar sentinel-dashboard-1.7.0.jar  运行jar包    
localhost:8080  访问登录 用户密码都是 sentinel    
阿里的Sentinel界面，功能都囊括的差不多了

## cloud-alibaba-sentinel-service8401 服务流量控制、熔断降级

### pom.xml
```
        <!-- nacos依赖的引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- 用作持久化 -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
        <!-- sentinel引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
```
### application.yaml

```
server:
  port: 8401
spring:
  application:
    name: cloud-alibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        #Nacos服务注册中心地址
        server-addr: localhost:8848
    sentinel:
      transport:
        #Sentinel-dashboard地址
        dashboard: localhost:8080
        #默认端口号，如果被占用则从8719依次+1 直至找到未被占用端口
        port: 8719

#暴露所有端点
management:
  endpoints:
    web:
      exposure:
        include: "*"

```
### FlowLimitController 热点key限流

```
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
```

# 服务熔断功能
### cloud-alibaba-sentinel-service8401  Sentinel规则持久化配置
#### pom.xml
```
 <!-- nacos依赖的引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- 用作sentinel规则持久化 -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
        <!-- sentinel引入 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
```

#### application.yaml
```
spring：
  cloud：
    sentinel：
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            dataId: cloud-alibaba-sentinel-service
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

#### Nacos客户端 新建配置 配置JSON格式内容如下：
```
[
    {
        "resource":"/byURL",   //资源名称
        "limitApp":"default",  //来源应用
        "grade":1,  //阈值类型：0表示线程数，1表示QPS
        "conunt":1, //单机阈值
        "strategy":0, //流控模式，0：直接、1：关联、2：链路
        "controlBehavior":0, //流控效果，0：快速失败、1：Warm up、2：排队等待
        "clusterMode":false  //是否集群
    }
]
```

## [Seata](http://seata.io/zh-cn/docs/user/quickstart.html) 分布式事务处理

**处理过程： ==一个XID + 三组件(TC、TM、RM)模型==**
    









