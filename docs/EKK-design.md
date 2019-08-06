# 网关集中式日志解决方案-EKK设计

### 1.简介

EKK是是Elasticsearch、Kafka和Kibana的简称，组合为网关提供的集中式日志解决方案。

Elasticsearch是一个开源分布式搜索引擎，提供搜索、分析、存储数据等功能。
Kibana 是一个基于 Web 的友好图形界面，用于搜索、分析和可视化存储在 Elasticsearch 中的数据。
Kafka是一种高吞吐量的分布式发布订阅消息系统，在EKK中作为消息中间件。

EKK参考业界成熟的ELK方案，提供自定义Logback等日志框架Appender将日志发送至Kafka，在服务端集中消费日志数据并处理后存储至Elasticsearch。EKK在日志采集端不需要独立进程，只需要引入相应配置即可实现日志归集，相对于ELK更轻量，日志字段精准拆分、定制和搜索，易于维护。

### 2.版本

| 软件 | 版本 |
|---- | ---- |
| Elasticsearch | 5.5.2 |
| Kibana | 5.5.2 |
| Kafka | 2.12-2.0.0 |

### 3.设计

下图是集中式日志解决方案EKK架构图，其中Appender、Producer和Sink为自实现。

![](docs/static_files/ekk架构.png)

#### 3.1 数据源

EKK存在两种数据来源，一种是通过消息中间件Producer发送的业务数据，另一种是通过日志框架Appender发送的日志数据。

##### 3.1.1 Producer

EKK基于spring-kafka 1.1.8.RELEASE封装为messaging模块，同时支持用户自主扩展其他消息中间件，使用spring-kafka自动配置，并自定义了部分自动配置项和注解，可根据自身需要修改配置。

对于非日志数据，可以通过messaging封装的Producer发送至Kafka，例如本团队的任务调度框架项目就是采用这种方式保留调度信息。

messaging使用topicPattern模式消费数据，所以用户指定topic后Producer将自动添加前缀作为真正toic，Consumer采用topicPattern模式消费后将还原topci为用户指定的topic。

使用生产者时需要添加`@EnableSagProducer`注解和配置，在代码中调用BaseDataCollector.send()方法即可。

##### 3.1.2 Appender

系统打日志时通常使用日志框架，目前主流的日志框架有Logback、Log4j和Log4j2等，上述日志框架均可以通过内置或自定义Appender的方式将日志发送至远端。

目前EKK自定义实现了Logback Appender(SagMqAppender)，该Appender继承自UnsynchronizedAppenderBase，重写的append方法中拆分出logtime、level、threadname、loggername、msg等字段并与自定义字段组合，最终通过Producer发送至Kafka。日志字段拆分存储到Elasticsearch后方便用户通过日志级别、线程、类名等更精确匹配日志内容。

需要注意的是由于SagMqAppender发送日志是通过Kafka，由于Kafka本身可能打日志，造成日志无限循环，所以需要在SagMqAppender中过滤Kafka相关日志，实现其他自定义Appender时需要注意。

```java
	@Override
	protected void append(ILoggingEvent event) {
        if (producer == null || !enable) {
            return;
        }

        // 过滤kafka相关包日志，防止kafka打日志形成死循环
        if (event.getLoggerName().startsWith(SPRING_KAFKA) || event.getLoggerName().startsWith(APACHE_KAFKA)) {
            return;
        }

        Map<String, Object> map = new LinkedHashMap<>(8);
        map.put("logtime", event.getTimeStamp());
        map.put("level", event.getLevel().toString());
        map.put("threadname", event.getThreadName());
        map.put("loggername", event.getLoggerName());
        map.put("msg", event.getFormattedMessage());
        map.putAll(args);

        producer.send(new MqMessage(topic, JsonTransform.toString(map));
    }
```

SagMqAppender发送日志调用封装的Kafka producer，需要初始化producer成员、topic和自定义字段等信息。自定义字段可指定为ip:port、appname等。由于appender由Logback初始化且时间较早，所以需要应用启动后通过Logback API获取appender实例后初始化。代码如下：

```java
	public static void init(String topic, BaseMqProducer producer, boolean enable, Map<String, Object> args) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : loggerContext.getLoggerList()) {
            Iterator<Appender<ILoggingEvent>> appenderIter = logger.iteratorForAppenders();
            while (appenderIter != null && appenderIter.hasNext()) {
                Appender<ILoggingEvent> appender = appenderIter.next();
                if (appender instanceof SagMqAppender) {
                    setParameter(logger, (SagMqAppender) appender, topic, producer, enable, args);
                }

                if (appender instanceof AsyncAppender) {
                    Iterator<Appender<ILoggingEvent>> subAppenderIter = ((AsyncAppender) appender).iteratorForAppenders();
                    while (subAppenderIter != null && subAppenderIter.hasNext()) {
                        Appender<ILoggingEvent> subAppender = subAppenderIter.next();
                        if (subAppender instanceof SagMqAppender) {
                            setParameter(logger, (SagMqAppender) subAppender, topic, producer, enable, args);
                        }
                    }
                }
            }
        }
    }
```

日志配置文件中引入SagMqAppender参考如下配置，基于性能考虑，建议搭配AsyncAppender实现异步日志发送。

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

#### 3.2 数据处理

Sink集消息消费、处理和存储于一体，同时自定义部分自动配置项，支持水平扩展，用户只需添加`@EnableSink`和配置即可使用。

Sink使用messaging消费者从kafka中批量和多线程消费数据。默认使用LogDataCollector将消费的日志数据存入Elasticsearch，同时支持用户自定义实现。

LogDataCollector采用异步方式插入Elasticsearch，返回的Future封装为Runnble放入线程池中等待插入结果。线程池ThreadPoolExecutor阻塞队列为ArrayBlockingQueue(固定队列)，拒绝策略为CallerRunsPolicy(线程池满则在当前线程调用)。异步插入加快了Elasticsearch插入速度，使用ArrayBlockingQueue排队可以节省异步插入结果的等待时间，CallerRunsPolicy控制插入速度防止消费速度大于插入速度时数据堆积在内存中。

Sink采用索引模板的方式设置索引，默认按月切索引。索引模版可配置，例如SagMqAppender的日志字段配置如下：

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
```

#### 3.3 数据展示

网关管理端集成Kibana作数据展示，用户通过网关管理端跳转时首先判断Kibana是否存在该网关组对应的Index Pattern，如果存在将直接挑转至网关组对应的Index Pattern，否则将先创建后跳转。

网关日志使用网关组名作为topic，Sink消费时默认根据topic将日志数据Elasticsearch的不同索引，所以每个网关组集群一个索引。Kibana自动跳转后展示的Index Pattern对应网关组日志索引数据，用户可通过应用名、ipport、日志级别、时间等字段精确查找日志。

![](docs/static_files/Kibana.jpg)

### 4.使用
EKK使用参考网关开源项目中的`EKK使用指南`

EKK不仅可以用在网关日志中，同样适用于任何采用集中式日志解决方案的SpringBoot项目中，尤其是不想引入第三方进程或希望日志归集自主可控的云平台应用。EKK设计之初也考虑了扩展性，用户可以自定义接入其他日志框架和消息中间件，自定义数据消费和处理方式、自定义索引模板等，提供给用户一个开箱即用，易于扩展的集中式日志解决方案。




