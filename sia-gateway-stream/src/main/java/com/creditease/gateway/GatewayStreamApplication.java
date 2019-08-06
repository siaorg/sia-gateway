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


package com.creditease.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.creditease.gateway.config.EnableSink;

/**
 * 网关日志存储
 *
 * @author peihua
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableSink
public class GatewayStreamApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayStreamApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(GatewayStreamApplication.class, args);

        LOGGER.info(">GatewayStreamApplication > started..");
    }
}
