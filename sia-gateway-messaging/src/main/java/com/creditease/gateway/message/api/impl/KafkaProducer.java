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

import java.util.Random;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.creditease.gateway.message.api.BaseMqProducer;
import com.creditease.gateway.message.api.domain.MqMessage;

/**
 * 消息生产者
 *
 * @author peihua
 ***/
public class KafkaProducer extends BaseMqProducer {

    private String topicPrefix;

    private int numOfPartition;

    private Random random = new Random();

    @Autowired
    private KafkaTemplate<Object, String> kafkaTemplate;

    public KafkaProducer() {

        logger.info("KafkaProducer init..");
    }

    @Override
    public void send(MqMessage msg) {

        ProducerRecord<Object, String> record = new ProducerRecord<>(topicPrefix + msg.getTopic(),
                random.nextInt(numOfPartition), (Long) msg.getArg("timestamp"), msg.getArg("key"), msg.getMsg());

        kafkaTemplate.send(record);
    }

    @Override
    public void start() {

        if (topicPrefix.length() > 1) {
            logger.info("KafkaProducer start, partition: {}", numOfPartition);
        } else {
            logger.error("KafkaProducer topicPrefix is empty!");
        }
    }

    @Override
    public void shutdown() {

        logger.info("KafkaProducer shutdown..");
    }

    public String getTopicPrefix() {

        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {

        this.topicPrefix = topicPrefix;
    }

    public int getNumOfPartition() {

        return numOfPartition;
    }

    public void setNumOfPartition(int numOfPartition) {

        this.numOfPartition = numOfPartition;
    }

}
