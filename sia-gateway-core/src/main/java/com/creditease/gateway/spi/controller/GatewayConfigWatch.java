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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;

import io.swagger.annotations.ApiOperation;

/**
 * 用于ZuulRegister的管理
 * 
 * @author peihua
 * 
 **/

@Component
@RestController
public class GatewayConfigWatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayConfigWatch.class);

    @Autowired
    ZuulProperties zuulProperties;

    @Autowired
    ServerProperties server;

    @ApiOperation("/SAGOpt-zuulconfige / 路由配置信息查看")
    @RequestMapping(value = SagProtocol.ADMINOPTCONFIGVIEW, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagZuulConfigWatch(@RequestBody Message msg, @RequestParam() String sagopt) {

        Map<String, Object> pro = new HashMap<String, Object>(8);

        pro.put("DisplayName", server.getDisplayName());
        pro.put("Address", server.getAddress().toString());
        pro.put("Route", zuulProperties.getRoutes());

        String rst = JsonHelper.toString(pro);

        return rst;
    }

}
