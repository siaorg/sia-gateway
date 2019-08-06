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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 用于蓝绿部署情况
 * 
 * @author peihua
 * 
 **/

@RestController
public class RouteSwitchController extends BaseAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteSwitchController.class);
    private static final String SUCCESS = "ok";
    private static final String FAILED = "error";

    /**
     * 根据路由更新某一个Route路由信息 同步更新数据库及缓存信息
     * 
     **/
    @ApiOperation("RouteSwitchController / 藍綠服务")
    @RequestMapping(value = SagProtocol.SWITCHROUTE, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptSwitchroute(@RequestBody Message msg) {

        try {
            Map<String, String> request = msg.getRequest();

            String routied = request.get("routid");
            String version = request.get("version");

            LOGGER.info("> 路由切換 @RequestParam routied:{},version:{}", routied, version);

            ribbonoptservice.routeRibbonRefresh();

            return SUCCESS;

        }
        catch (Exception e) {

            new GatewayException(ExceptionType.CoreException, e);
            return FAILED;
        }
    }

}
