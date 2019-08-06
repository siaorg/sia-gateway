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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.creditease.gateway.service.RouteOptService;

/**
 * Route路由ID与组件绑定初始化
 * 
 * @author peihua
 * 
 **/

@Component
public class RouteBindRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteBindRunner.class);

    @Autowired
    protected RouteOptService routeoptservice;

    /**
     * 初始化 routeCompMap
     */
    @Override
    public void run(String... args) {

        LOGGER.info("RouteRunner routeCompMap开始初始化");

        routeoptservice.routeBind();
    }
}
