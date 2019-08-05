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


package com.creditease.gateway.route.locator;

import java.util.LinkedHashMap;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;

/**
 * TODO:自定义DiscovercRouteLocator路由定位器（支持多注册中心）
 * 
 * @author peihua
 * 
 * 
 **/

public class ZuulDiscovercRouteLocator extends DiscoveryClientRouteLocator {

    public ZuulDiscovercRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties) {
        super(servletPath, discovery, properties);
    }

    @Override
    protected LinkedHashMap<String, ZuulRoute> locateRoutes() {

        return new LinkedHashMap<String, ZuulRoute>();
    }
}
