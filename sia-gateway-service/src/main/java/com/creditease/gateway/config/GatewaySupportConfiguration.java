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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.creditease.gateway.alarm.AlarmService;
import com.creditease.gateway.service.SchedulerService;
import com.creditease.gateway.service.impl.SchedulerServiceImpl;

/**
 * 用于輔助GW的服務
 * 
 * @author peihua
 **/
@Configuration
public class GatewaySupportConfiguration {

    private static final int CONNECTTIMEOUT = 2000;

    private static final int READTIMEOUT = 3000;

    @Value("${spring.application.name}")
    private String serviceTopic;

    @Bean
    @ConditionalOnMissingBean
    public SchedulerService statisticService() {

        return new SchedulerServiceImpl();
    }

    @Bean
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECTTIMEOUT);
        requestFactory.setReadTimeout(READTIMEOUT);
        return new RestTemplate(requestFactory);
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {

        return new AsyncRestTemplate();
    }

    @Bean
    public AlarmService getAlarm() {

        return new AlarmService();
    }
}
