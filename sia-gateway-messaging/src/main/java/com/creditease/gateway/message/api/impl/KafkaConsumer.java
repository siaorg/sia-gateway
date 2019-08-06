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


package com.creditease.gateway.message.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.creditease.gateway.message.api.BaseMqConsumer;
import com.creditease.gateway.message.api.MqHandler;
import com.creditease.gateway.message.api.domain.MqMessage;

/**
 * 消息接受类
 *
 * @author peihua
 */
public class KafkaConsumer extends BaseMqConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private String topicPrefix;

    @Autowired
    private MqHandler mqHandler;

    public KafkaConsumer() {
        logger.info("KafkaConsumer init..");
    }

    @Override
    public void start() {

        if (topicPrefix.length() > 1) {
            logger.info("KafkaConsumer start, topicPrefix:{}", topicPrefix);
        } else {
            logger.error("KafkaConsumer topicPrefix is empty!");
        }
    }

    @Override
    public void shutdown() {

        logger.info("KafkaConsumer is shutdown.");
    }

    /**
     * topicPattern只能用常量，只好不使用SagMqProperties
     */
    @KafkaListener(topicPattern = "${spring.kafka.topicPrefix}" + "*")
    public void listen(List<ConsumerRecord<String, String>> records) {

        List<MqMessage> list = new ArrayList<>(records.size());

        for (ConsumerRecord<String, String> record : records) {
            String topic = record.topic().substring(topicPrefix.length());

            MqMessage msg = new MqMessage(topic, record.value());
            msg.addArg("partition", record.partition());
            msg.addArg("timestamp", record.timestamp());
            msg.addArg("key", record.key());

            list.add(msg);
        }

        mqHandler.handle(list);
    }

    public void listen(ConsumerRecord<String, String> record) {

        String topic = record.topic().substring(topicPrefix.length());
        MqMessage msg = new MqMessage(topic, record.value());
        msg.addArg("timestamp", record.timestamp());
        msg.addArg("partition", record.partition());
        msg.addArg("key", record.key());

        mqHandler.handle(msg);
    }

    public String getTopicPrefix() {

        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {

        this.topicPrefix = topicPrefix;
    }
}
