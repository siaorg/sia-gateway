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

import com.creditease.gateway.message.api.BaseMqProducer;
import com.creditease.gateway.message.config.SagMqProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yongbiao
 * @Date: 2019/6/21 14:18
 **/
public class SagAppenderRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SagAppenderRunner.class);

    @Autowired
    private SagMqProperties sagMqProperties;

    @Value("${spring.application.name:messaging}")
    private String appName;

    @Value("${spring.cloud.client.ipAddress:127.0.0.1}:${server.port:8080}")
    private String instanceId;

    @Autowired
    private BaseMqProducer producer;

    @Override
    public void run(String... args) {

        logger.info("SagAppenderRunner start..");

        Map<String, Object> appenderArgs = new HashMap<>(2);
        appenderArgs.put("instanceid", instanceId);
        appenderArgs.put("appname", appName);

        SagMqAppender.init(appName, producer, sagMqProperties.getAppender().getEnable(), appenderArgs);
    }
}
