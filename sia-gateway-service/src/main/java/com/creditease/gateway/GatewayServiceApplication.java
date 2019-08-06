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

import com.creditease.gateway.message.config.EnableSagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务管理
 * 
 * @author peihua
 * 
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.creditease.gateway.service.**")
@EnableScheduling
@EnableSagProducer
public class GatewayServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayServiceApplication.class);

	public static void main(String[] args) {

        SpringApplication.run(GatewayServiceApplication.class, args);
        
		LOGGER.info(">>>GatewayServiceApplication started..");
	}
}
