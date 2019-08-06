# 微服务灰度配置

### 网关灰度发布说明

目前灰度发布组件包含两种导流方式：蓝绿部署和金丝雀部署， 这两种流量导流策略都需要网关连接的后端服务是SpringBoot微服务并且注册到Eureka注册中心，并且SpringBoot服务需要配置文件中的MetaData版本信息。

### 配置文件

```yml

spring.application.name: xxx
server.port: ****
eureka.client.serviceUrl.defaultZone: http://127.0.0.1:19002/eureka/
eureka.instance.preferIpAddress: true
eureka.instance.instance-id: ${spring.cloud.client.ipAddress}:${server.port}
eureka.instance.prefer-ip-address: true
eureka.instance.metadata-map.version: 1

```
 上面是一个应用的配置文件，其中表明版本信息的MetaData如下，只需要根据不同版本信息表用节点版本即可。

```yml
eureka.instance.metadata-map.version: 1
```
