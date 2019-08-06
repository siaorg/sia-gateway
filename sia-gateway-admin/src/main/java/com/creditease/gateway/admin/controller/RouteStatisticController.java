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

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.StatisticQuery;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 路由统计管理
 * 
 * @author peihua
 * */

@RestController
public class RouteStatisticController extends BaseAdminController{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(RouteStatisticController.class);
	
	@Autowired
    AdminService adminService;
	
    /**
     * 查詢Zuul每天的方位次數統計
     * 
     */
    @RequestMapping(value = "/getRouteCountArray", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public String getRouteCountArray(@RequestBody StatisticQuery request){

		try {
			
			List rst = adminService.getRouteInvokeArray(request);
			
			Message adminMsg = new Message(rst);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(adminMsg);
			
		} catch (Exception e) {
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">getRouteCountArray Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
    
    /**
     * 查詢Zuul的訪問健康指數
     * 
     */
    @RequestMapping(value = "/getRouteHealthyArray", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public String getAPIHealthyArray(@RequestBody StatisticQuery request){
    	
    	try {
			List rst = adminService.getRouteHealthyArray(request);
			
			Message adminMsg = new Message(rst);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(adminMsg);
			
		} catch (IOException e) {
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">getAPIHealthyArray Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
			
		} catch (Exception e) {
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">getAPIHealthyArray Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
  
 
}
