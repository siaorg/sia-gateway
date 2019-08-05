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

import com.creditease.gateway.helper.StringHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.RouteLimitService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 网关限流管理
 *
 * @author peihua
 * */

@RestController
public class RouteRateLimitController extends BaseAdminController{

    public static final Logger LOGGER = LoggerFactory.getLogger(RouteRateLimitController.class);

    @Autowired
    RouteLimitService rlService;

    /**
     * 限流设置
     *
     */
    @RequestMapping(value = "/routelimitquery", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String routeratelimitquery(@RequestBody Map<String,String> request) {

    	try {
			String routeid = request.get("routeid");

			String limit = rlService.queryRouteLimit(routeid);

			Message resp = new Message(limit);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);

		} catch (Exception e) {
			new GatewayException(ExceptionType.AdminException,e);

			LOGGER.error(">routeratelimitquery Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }

    /**
     * 限流保存
     *
     */
    @RequestMapping(value = "/routelimitsave", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String routeratelimitsave(@RequestBody Map<String,String> request) {

    	try {
			String routeid = request.get("routeid");
			String limit = request.get("limit");
            limit = StringHelper.isEmpty(limit) ? "0" : limit;

			rlService.saveRouteLimit(routeid , limit);

			Message resp = new Message(limit);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);

		} catch (Exception e) {
			new GatewayException(ExceptionType.AdminException,e);

			LOGGER.error(">routeratelimitsave Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
}
