/*-
 * <<
 * sag
 * ==
 * Copyright (C) 2019 sia
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */


package com.creditease.gateway.message.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.message.api.BaseMqProducer;
import com.creditease.gateway.message.api.domain.MqMessage;

import java.util.*;

/**
 * @Author: yongbiao
 * @Date: 2019/2/22 14:18
 */
public class SagMqAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public static final String SPRING_KAFKA = "org.springframework.kafka";
    public static final String APACHE_KAFKA = "org.apache.kafka";

    private String topic;
    private BaseMqProducer producer;
    private boolean enable = true;

    private HashMap<String, Object> args = new HashMap<>(8);

    @Override
    public void start() {
        super.start();
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setProducer(BaseMqProducer producer) {
        this.producer = producer;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void addArg(String key, Object obj) {
        this.args.put(key, obj);
    }

    public void putArgs(Map<String, Object> args) {
        this.args.putAll(args);
    }

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

        MqMessage message = new MqMessage(topic, JsonTransform.toString(map));

        producer.send(message);
    }

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

    private static void setParameter(Logger logger, SagMqAppender appender, String topic,
                                     BaseMqProducer producer, boolean enable, Map<String, Object> args) {

        appender.setTopic(topic);
        appender.setEnable(enable);
        appender.putArgs(args);
        // 放在最后，因为通过producer是否为null判断是否发日志
        appender.setProducer(producer);

        if (!enable) {
            logger.warn("SagMqAppender will not send logs to Mq, because enable is false!");
        }

        logger.info("SagMqAppender init completed. topic:{}, producer:{}, enable:{}, args:{}",
                topic, producer, enable, JsonTransform.toString(args));
    }
}
