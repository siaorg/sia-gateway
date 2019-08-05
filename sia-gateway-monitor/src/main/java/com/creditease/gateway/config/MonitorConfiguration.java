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

import com.creditease.gateway.alarm.AlarmService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author: yongbiao
 * @Date: 2019-06-21 19:13
 */
@Configuration
public class MonitorConfiguration {

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate =  new RestTemplate();
        //解决String类型返回中文乱码的问题
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for(HttpMessageConverter converter : list) {
           if(converter instanceof StringHttpMessageConverter) {
               ((StringHttpMessageConverter) converter).setDefaultCharset(Charset.defaultCharset());
               break;
           }
        }

        return restTemplate;
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
