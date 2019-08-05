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


package com.creditease.gateway.admin.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.RouteTestService;
import com.creditease.gateway.message.Message;

/**
 * 路由测试管理
 * 
 * @author peihua
 */
@RestController
public class RouteTestController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(RouteTestController.class);

    @Autowired
    RouteTestService rtService;

    /**
     * 路由连通性测试接口
     */
    @PostMapping(value = "/routetest", produces = "application/json;charset=UTF-8")
    public Message routetest(@RequestBody Map<String, String> request) {

        LOGGER.info(">routetest by user:" + authCheckor.getCurrentUser());
        return rtService.testRoute(request);
    }
}
