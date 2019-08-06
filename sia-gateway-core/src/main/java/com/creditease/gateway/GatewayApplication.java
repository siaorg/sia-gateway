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
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import com.creditease.gateway.javassist.EurekaClientJavassist;
import com.creditease.gateway.message.config.EnableSagProducer;
import com.spring4all.swagger.EnableSwagger2Doc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 网关入口启动类
 * 
 * @author peihua
 * 
 * 
 **/
@EnableSagProducer
@EnableSwagger2Doc
@EnableSwagger2
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class GatewayApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApplication.class);

    public static void main(String[] args) {

        new EurekaClientJavassist().hookCacheRefresh();

        SpringApplication.run(GatewayApplication.class, args);

        LOGGER.info(">GatewayApplication 启动...");
    }
}
