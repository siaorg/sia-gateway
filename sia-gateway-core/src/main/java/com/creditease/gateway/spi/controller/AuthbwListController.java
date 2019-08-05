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
import com.creditease.gateway.message.Message;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 用于Route的黑白名单服务
 * 
 * @author peihua
 **/
@RestController
public class AuthbwListController extends BaseAdminController {

    @ApiOperation("RouteBWListController / 黑白名单服务")
    @RequestMapping(value = SagProtocol.UPDATEFILTERCACHE, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptUpdatefiltercach(@RequestBody Message msg) {

        ObjectMapper mapper = new ObjectMapper();
        boolean rst = bwlistservice.updateBWList(msg);
        LOGGER.info(">黑白名单更新结果 rst：{}", rst);

        try {
            return mapper.writeValueAsString(rst);
        }
        catch (IOException e) {
            new GatewayException(ExceptionType.CoreException, e);
            LOGGER.error(">Exception:{}", e.getCause());
        }
        return null;
    }

    @ApiOperation("RouteBWListController / 黑白名单服务")
    @RequestMapping(value = SagProtocol.DELETEFILTERCACHE, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagOptDeletefiltercache(@RequestBody Message msg) {

        ObjectMapper mapper = new ObjectMapper();
        boolean rst = bwlistservice.deleteBWList(msg);
        LOGGER.info("> 黑白名单删除结果 rst:{}", rst);

        try {
            return mapper.writeValueAsString(rst);
        }
        catch (IOException e) {
            new GatewayException(ExceptionType.CoreException, e);
            LOGGER.error(">>> Exception:" + e.getCause());
        }
        return null;
    }

}
