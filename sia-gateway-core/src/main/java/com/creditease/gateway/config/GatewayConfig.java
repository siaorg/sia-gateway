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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.creditease.gateway.javassist.FilterLoaderJavassit;
import com.creditease.gateway.route.RouteManager;
import com.creditease.gateway.route.locator.ZuulDiscovercRouteLocator;
import com.creditease.gateway.route.locator.ZuulRouteLocator;

/**
 * 网关路由及主要模块配置
 *
 * @author peihua
 *
 **/

@Configuration
public class GatewayConfig {

    @Autowired
    ZuulProperties zuulProperties;
    @Autowired
    ServerProperties server;

    @Value("${spring.application.name}")
    private String gatewayTopic;

    @Bean
    public ZuulRouteLocator routeLocator() {

        return new RouteManager().registerRouteLocator(zuulProperties, server);
    }

    @Bean
    public ZuulDiscovercRouteLocator routeDiscoverLocator() {

        return new ZuulDiscovercRouteLocator(gatewayTopic, null, zuulProperties);
    }

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {

        return new AsyncRestTemplate();
    }

    @Bean
    FilterLoaderJavassit fileLoaderHookLoader() {

        return new FilterLoaderJavassit().hookFileLoader();
    }
}
