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
 * 用于限流
 * 
 * @author peihua
 * 
 **/

@RestController
public class RouteRateLimitController extends BaseAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRateLimitController.class);

    private static final String SUCCESS = "ok";
    private static final String FAILED = "error";

    /**
     * 限流服务
     * 
     **/
    @ApiOperation("RouteRateLimitController / 限流服务")
    @RequestMapping(value = SagProtocol.ROUTERATELIMIT, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptRouteRateLimit(@RequestBody Message msg) {

        try {

            boolean rst = ratelimitservice.refreshRateLimtMap();

            if (rst) {
                return SUCCESS;
            }
            else {
                return FAILED;
            }
        }
        catch (Exception e) {

            LOGGER.error(">Exception:{}" + e.getCause());
            new GatewayException(ExceptionType.CoreException, e);

            return FAILED;
        }
    }

}
