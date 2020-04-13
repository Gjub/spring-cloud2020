
# SpringCloud
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

        <!--导入本地 zookeeper 3.4.6 版本-->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.6</version>
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
