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


package com.creditease.gateway.config;

import com.creditease.gateway.elasticsearch.client.ESClient;
import com.creditease.gateway.handler.DataConsumerHandler;
import com.creditease.gateway.message.api.MqHandler;
import com.creditease.gateway.runner.SinkRunner;
import com.creditease.gateway.sink.LogDataCollector;
import com.creditease.gateway.sink.abs.BaseDataCollector;
import com.creditease.gateway.sink.es.EsIndexMgr;
import com.creditease.gateway.sink.starter.AbstractSinkStarter;
import com.creditease.gateway.sink.starter.LogSinkStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Sink配置
 * 
 * @author peihua
 **/
@EnableConfigurationProperties(SinkProperties.class)
@ConditionalOnMissingBean(SinkAutoConfiguration.class)
public class SinkAutoConfiguration {

    @Autowired
    private SinkProperties properties;

    @Bean
    @ConditionalOnMissingBean(ESClient.class)
    public ESClient getEsClient() {

        return new ESClient(properties.getEsAddr(), properties.getEsClusterName());
    }

    @Bean
    @ConditionalOnMissingBean(SinkRunner.class)
    public SinkRunner getSinRunner() {

        if (properties.getDefaultRunnerEnable()) {
            return new SinkRunner();
        }

        return null;
    }

    @Bean
    @ConditionalOnMissingBean(BaseDataCollector.class)
    public BaseDataCollector getDataCollector() {

        LogDataCollector collector = new LogDataCollector();
        collector.setIndexPrefix(properties.getEsIndexPrefix());
        collector.setExecutor(properties.getNThreads(), properties.getBlockingQueueSize());

        return collector;
    }

    @Bean
    @ConditionalOnMissingBean(EsIndexMgr.class)
    public EsIndexMgr getEsIndexMgr() {

        return new EsIndexMgr(properties.getEsIndexTemplate(), properties.getEsTemplateName(),
                properties.getEsIndexType(), properties.getEsIndexNumberOfShards(),
                properties.getEsIndexNumberOfReplicas(), properties.getEsTemplateOrder());
    }

    @Bean
    @ConditionalOnMissingBean(MqHandler.class)
    public MqHandler getMqHandler() {

        return new DataConsumerHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractSinkStarter.class)
    public AbstractSinkStarter getSinkStarter() {

        LogSinkStarter sinkStarter = new LogSinkStarter();
        sinkStarter.setFileName(properties.getEsMappingFileName());

        return sinkStarter;
    }
}
