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


package com.creditease.gateway.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: yongbiao
 * @Date: 2019-06-05 17:59
 */
@ConfigurationProperties(prefix = "spring")
public class SagMqProperties {

    private MqType mqType = MqType.Kafka;

    private final Kafka kafka = new Kafka();

    private final Appender appender = new Appender();

    public Kafka getKafka() {
        return kafka;
    }

    public Appender getAppender() {
        return appender;
    }

    public MqType getMqType() {
        return mqType;
    }

    public void setMqType(String type) {
        mqType.setType(type);
    }

    public static class Kafka {

        private String topicPrefix;

        private String topicPattern;

        private final Producer producer = new Producer();

        public String getTopicPrefix() {
            return topicPrefix;
        }

        public void setTopicPrefix(String topicPrefix) {
            this.topicPrefix = topicPrefix;
        }

        public String getTopicPattern() {
            return topicPattern != null ? topicPattern : topicPrefix + "*";
        }

        public void setTopicPattern(String topicPattern) {
            this.topicPattern = topicPattern;
        }

        public Producer getProducer() {
            return producer;
        }

        public static class Producer {

            private int numOfPartition = 0;

            public int getNumOfPartition() {
                return numOfPartition;
            }

            public void setNumOfPartition(int numOfPartition) {
                this.numOfPartition = numOfPartition;
            }
        }
    }

    public static class Appender {

        private boolean enable = true;

        private boolean runnerEnable = true;

        public boolean getEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public boolean getRunnerEnable() {
            return runnerEnable;
        }

        public void setRunnerEnable(boolean runnerEnable) {
            this.runnerEnable = runnerEnable;
        }
    }

    public enum MqType {

        Kafka("Kafka"), RocketMq("RocketMQ"), RabbitMQ("RabbitMQ");

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {

            if (type.equalsIgnoreCase(RocketMq.type)) {
                this.type = RocketMq.type;
            } else if (type.equalsIgnoreCase(RabbitMQ.type)) {
                this.type = RabbitMQ.type;
            } else {
                this.type = Kafka.type;
            }
        }

        MqType(String type) {
            this.type = type;
        }
    }

}
