# EKK使用指南
EKK是Elasticsearch、Kafka和Kibana的简称，是网关提供的日志接收与管理服务的解决方案。

Kafka是一种高吞吐量的分布式发布订阅消息系统，在EKK中作为消息中间件。
Elasticsearch是一个开源分布式搜索引擎，提供搜索、分析、存储数据等功能。
Kibana设计用于和Elasticsearch一起工作，是一个开源的数据分析和可视化平台。

EKK基于spring-kafka 1.1.8.RELEASE和elasticsearch-client transport 5.5.2封装，支持spring-kafka自动配置，并自定义了部分自动配置项，
可根据自身需要修改配置。

## 一、Kafka生产者使用
Kafka生产者使用分如下三步：
### 1.1 pom.xml添加配置
```xml
<dependency>
    <groupId>com.sia</groupId>
    <artifactId>sia-gateway-messaging</artifactId>
    <version>1.0</version>
</dependency>
```
### 1.2 调用生产者
&#8195;（A）主类添加注解 **@EnableSagProducer**，该注解将启用生产者自动配置并初始化

&#8195;（B）新建生产者类并添加**@Autowired**注解的**BaseMqProducer**类型属性

&#8195;（C）调用**BaseMqProducer.send(MqMessage msg)**发送消息
```java
@Component
public class KafkaProducerTest {

    @Autowired
    private BaseMqProducer producer;
    
    public void send(MqMessage msg) {
        producer.send(msg);
    }
}
```
### 1.3 application.yml添加配置
```yml
spring.kafka.bootstrap-servers: 127.0.0.1:9092
# 生产者和消费者topic前缀，生产者和消费者配置需保持一致，自定义
spring.kafka.topicPrefix: topic.

# 消息发送时partition取[0-配置值)中的随机值，如kafka server端num.partitions=4则最大配置4
spring.kafka.producer.numOfPartition: 4
# 失败重试次数
spring.kafka.producer.retries: 2
# 每次批量发送消息的数量
spring.kafka.producer.batch-size: 16384
spring.kafka.producer.buffer-memory: 33554432
```
&#8195;&#8195;EKK的Kafka消费者使用topicPattern模式消费消息，KafkaProducer.send()发送消息时自动添加配置的前缀，
消费者消费消息后自动去除配置的前缀。例如：配置spring.kafka.topicPrefix: topic.，调用KafkaProducer.send()发送消息时指定topic为“abc”，
则topic实际为“topic.abc”，KafkaConsumer接收“topic.*”类型的消息并还原topic为“abc”。

&#8195;&#8195;配置spring.kafka.topicPrefix时请使用带有业务属性的字符串，且生产和消费者必须一致。

## 二、Kafka消费者使用
封装的Kafka消费者使用批量消费，使用分如下四步：
### 2.1 pom.xml添加配置
```xml
<dependency>
    <groupId>com.sia</groupId>
    <artifactId>sia-gateway-messaging</artifactId>
    <version>1.0</version>
</dependency>
```
### 2.2 主类添加注解 **@EnableSagConsumer**
&#8195;该注解将启用消费者自动配置项

### 2.3 实现MqHandler接口并注入Bean
&#8195;该接口用于指定消费消息后的处理逻辑，消费消息后将调用该接口的**handle**方法
```java
@Component
public class KafkaConsumerHandler implements MqHandler {

    @Override
    public void handle(MqMessage msg){
        System.out.println(msg.toString());
    }
}
```

### 2.4 application.yml添加配置
```yml
spring.kafka.bootstrap-servers: 127.0.0.1:9092
# 生产者和消费者topic前缀，生产者和消费者配置需保持一致，自定义
spring.kafka.topicPrefix: topic.
# 消费者所在组
spring.kafka.consumer.group-id: test-consumer
spring.kafka.consumer.auto-offset-reset: earliest
spring.kafka.consumer.enable-auto-commit: true
spring.kafka.consumer.auto-commit-interval: 100
# 批量消费一次最多消费条数
spring.kafka.consumer.max-poll-records: 100
# 并发消费线程数
spring.kafka.listener.concurrency: 2
spring.kafka.listener.poll-timeout: 3000
```

## 三、SagMqAppender使用
SagMqAppender是自定义的logback appender，logback配置文件配置该appender后，日志将被拆分为logtime、level、threadname、
loggername、msg字段，并和args中自定义字段发送至kafka，其中args是map，由用户初始化。为了防止形成循环日志，
SagMqAppender将过滤"org.springframework.kafka"和"org.apache.kafka"包日志。具体使用方式如下：
### 3.1 引入生产者pom.xml、yml配置和注解

### 3.2 Logback配置文件引入SagMqAppender
基于性能考虑，建议搭配AsyncAppender实现异步日志发送。
```xml
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml" />
    <appender name="KAFKA" class="com.creditease.gateway.message.appender.SagMqAppender"></appender>
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="KAFKA"/>
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC"/>
    </root>
</configuration>
```

## 四、Sink模块使用
Sink是EKK集Kafka消费和数据处理的模块，可从kafka中消费消息处理后存入ES中。Sink自定义了部分自动配置，并支持ES索引模板功能，
索引模板字段设置可通过配置文件配置。应用启动时将通过索引模板名判断索引模板是否存在，不存在将直接创建，存在则删除后创建。使用分为如下几步：

###4.1 pom.xml引入如下配置：
```xml
<dependency>
    <groupId>com.sia</groupId>
    <artifactId>sia-gateway-sink</artifactId>
    <version>1.0</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>5.5.2</version>
</dependency>
```
### 4.2 主类添加注解 **@EnableSink**
&#8195;该注解将启用Sink和消费者自动配置项

### 4.3 application.yml添加kafka消费者配置

### 4.4 application.yml添加如下配置
```yml
## 根据es配置填写
sink.esClusterName: siagateway
sink.esAddr: 127.0.0.1:9300
# es索引模板名，请勿与其他模板重复
sink.esTemplateName: sia_gateway_stream
# es索引模板前缀，创建索引时将自动添加该前缀
sink.esIndexPrefix: sag-
# 是否启动默认runner，默认runner将读取索引模板字段配置文件并更新索引模板设置
sink.defaultRunnerEnable: true

# 自此以下配置均为默认值，不配置将使用默认配置，建议忽略以下配置使用默认值
# es索引模板字段配置的文件名
sink.esMappingFileName: index-mapping.json
# 默认为索引前缀esIndexPrefix+"*"
sink.esIndexTemplate: sag-*
# es索引类型，es7开始将不支持索引类型，不建议修改
sink.esIndexType: stream_type
# es索引模板版本号
sink.esTemplateOrder: 0
# es索引分片数
sink.esIndexNumberOfShards: 5
# es索引副本数
sink.esIndexNumberOfReplicas: 0
```

### 4.5 增加es索引模板字段配置文件
es索引模板配置文件只支持json文件，与application.yml在同级目录下，文件名默认为：index-mapping.json，可根据配置的文件名添相应配置文件。
例如SagMqAppender的日志字段配置如下：
```json
{
  "msg": {
    "type": "text",
    "analyzer": "standard"
  },
  "instanceid": {
    "type": "keyword"
  },
  "appname": {
    "type": "keyword"
  },
  "level": {
    "type": "keyword"
  },
  "logtime": {
    "type": "date",
    "fields": {
      "long": {
        "type": "long"
      }
    }
  },
  "loggername": {
    "type": "keyword"
  },
  "threadname": {
    "type": "keyword"
  }
}
```

### 4.6 EKK设计文档
* EKK设计文档[ekk](/docs/EKK-design.md)。
