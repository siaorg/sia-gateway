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

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.route.locator.ZuulRouteLocator;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 全部有效路由查看
 * 
 * @author peihua
 **/

@RestController
public class RouteWatchController extends BaseAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteWatchController.class);

    @Autowired
    ZuulHandlerMapping zuulHandlerMapping;

    @Autowired
    ZuulRouteLocator localtor;

    @ApiOperation("/sagOpt-routewatch / 路由查看服务")
    @RequestMapping(value = SagProtocol.ROUTEWATCH, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagAdminOptRouteWatch() {

        try {

            Map<String, Object> handlerMap = zuulHandlerMapping.getHandlerMap();

            for (String key : handlerMap.keySet()) {

                LOGGER.debug(">可用路由 key:{},可用路由 value:{}", key, handlerMap.get(key));
            }
            String validRoute = localtor.getRouteBuffer().toString();
            return validRoute;
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

    /**
     *
     * 为API网关提供一个HTTP-URL作为健康检查入口
     */
    @RequestMapping(value = SagProtocol.HEALTHMONITOR, produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String healthMonitor() {

        try {
            LOGGER.info(">>健康检查正常！");
            Message adminMsg = new Message("健康检查正常！", 200);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());

            return returnErrorMsg("健康检查失败！", ResponseCode.SERVER_ERROR_CODE);
        }
    }

    public String returnErrorMsg(String errorMsg, ResponseCode code) {

        Message errorResponse = new Message(errorMsg, code.getCode());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(errorResponse);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
