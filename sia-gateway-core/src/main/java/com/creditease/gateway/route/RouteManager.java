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
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Component;

import com.creditease.gateway.route.locator.ZuulRouteLocator;

/**
 * 用于RouteLoctor的管理
 * 
 * @author peihua
 * 
 **/

@Component
public class RouteManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteManager.class);

    ZuulRouteLocator routeLocator;

    public ZuulRouteLocator registerRouteLocator(ZuulProperties zuulProperties, ServerProperties server) {

        return routeLocator(zuulProperties, server);
    }

    public ZuulRouteLocator routeLocator(ZuulProperties zuulProperties, ServerProperties server) {

        routeLocator = new ZuulRouteLocator(server.getServletPrefix(), zuulProperties);

        LOGGER.info("> routelocator properties: " + routeLocator.toString());

        return routeLocator;
    }
}
