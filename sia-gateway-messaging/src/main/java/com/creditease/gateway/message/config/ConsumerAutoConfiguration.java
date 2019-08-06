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

import com.creditease.gateway.message.api.BaseMqConsumer;
import com.creditease.gateway.message.api.impl.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * @Author: yongbiao
 * @Date: 2019-06-05 18:02
 */
@EnableConfigurationProperties(SagMqProperties.class)
@ConditionalOnMissingBean(ConsumerAutoConfiguration.class)
public class ConsumerAutoConfiguration {

    @Autowired
    private SagMqProperties properties;

    @Bean
    @ConditionalOnMissingBean(BaseMqConsumer.class)
    public BaseMqConsumer getMqConsumer() {

        if (properties.getMqType() == SagMqProperties.MqType.Kafka) {
            KafkaConsumer consumer = new KafkaConsumer();
            consumer.setTopicPrefix(properties.getKafka().getTopicPrefix());
            consumer.start();

            return consumer;
        }

        return null;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        // 设置批量消费
        factory.setBatchListener(true);

        return factory;
    }

}
