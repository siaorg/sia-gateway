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


package com.creditease.gateway.spi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.repository.RouteRepository;
import com.creditease.gateway.route.RouteLocatorUpdater;
import com.creditease.gateway.route.locator.ZuulListofServerLocator;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 用于Route的Web服务
 * 
 * @author peihua
 **/

@RestController
public class RouteRefreshController extends BaseAdminController {

    @Autowired
    RouteLocatorUpdater rutor;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    ZuulListofServerLocator listofserverLocator;

    @ApiOperation("RouteRefreshController /路由服务")
    @RequestMapping(value = SagProtocol.REFRESHROUTE, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")

    @ResponseBody
    public String sagOptRefreshroute(Message value) {

        try {
            LOGGER.info("> 路由刷新服務: 参数：{}", value);
            rutor.refreshRoute();
            return null;

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

}
