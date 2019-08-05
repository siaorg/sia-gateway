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


package com.creditease.gateway.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.creditease.gateway.route.locator.ZuulRouteLocator;

/**
 * 用于Route的更新
 * 
 * @author peihua
 * 
 **/

@Component
public class RouteLocatorUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteLocatorUpdater.class);

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    RouteLocator routeLocator;

    @Autowired
    ZuulRouteLocator routlocator;

    public void refreshRoute() {

        LOGGER.info("> 路由刷新調用...");

        /**
         * Step1: refreshCalled设置为TURE，强制locateRoutes从DB获取Route信息
         */
        routlocator.getRefreshCalled().compareAndSet(false, true);
        ;

        /**
         * Step2: 发送通知事件，DB同步Route信息取得最新的Route路由表
         */

        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);

        publisher.publishEvent(routesRefreshedEvent);

    }
}
