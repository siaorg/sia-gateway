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

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 用于Route的Bind组件绑定/解除綁定服务
 * 
 * @author peihua
 * 
 **/

@RestController
public class RouteCompController extends BaseAdminController {

    @ApiOperation("RouteBindRereshHandler / 路由绑定服务")
    @RequestMapping(value = SagProtocol.BINDROUTEREFRESH, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptBindrouterefresh(Message msg) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            LOGGER.info(">路由更新 >，更新參數:{}", msg);
            boolean rst = routeoptservice.routeBind();
            LOGGER.info(">路由更新> ,結果：{}", rst);

            msg.setCode(ResponseCode.SUCCESS_CODE.getCode());
            msg.setResponse(rst);
            return mapper.writeValueAsString(msg);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

    @ApiOperation("RouteCompRemoveHandler /组件删除服务")
    @RequestMapping(value = SagProtocol.COMPREMOVE, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptCompRemove(@RequestBody Message msg) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            LOGGER.info(">组件删除服务,参数:{}", msg);
            Map<String, String> request = msg.getRequest();
            String compFilterName = request.get("compFilterName");

            if (StringHelper.isEmpty(compFilterName)) {
                LOGGER.error(">>>>> 组件删除失败，compFilterName is Empty！！！");
                msg.setCode(ResponseCode.SERVER_ERROR_CODE.getCode());
                msg.setResponse(false);
                return mapper.writeValueAsString(msg);
            }

            boolean rst = routeoptservice.removeComp(compFilterName);
            LOGGER.info("> 组件删除服务結果> ,rst:{}", rst);

            msg.setCode(ResponseCode.SUCCESS_CODE.getCode());
            msg.setResponse(rst);
            return mapper.writeValueAsString(msg);
        }
        catch (Exception e) {
            LOGGER.error(">sagOptCompRemove Exception:" + e.getCause());
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }
}
