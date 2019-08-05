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

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.RouteRibbonService;
import com.creditease.gateway.domain.RouteRibbonHolder;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 网关灰度部署管理
 * 
 * @author peihua
 * */

@RestController
public class RouteRibbonController extends BaseAdminController{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(RouteRibbonController.class);
	
    @Autowired
    RouteRibbonService rrService;

    /**
     * 路由版本查看
     * 
     * 1.第一步去Eureka查看所有Instance版本
     * 
     * 2.第二步去数据库查看当前是否已经设置路由的Ribbon-version
     * 
     */
    @RequestMapping(value = "/routeRibbonQuery", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String routeRibbonQuery(@RequestBody Map<String,String> request) {
    	
    	try {
			String routeid = request.get("routeid");
			String serviceId = request.get("serviceId");

			LOGGER.info(">routeRibbonQuery serviceId："+ serviceId + "routeid:" + routeid);
			
			RouteRibbonHolder maprst = rrService.queryRouteRibbon(serviceId,routeid);
			
			Message resp = new Message(maprst);
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);
			
		} catch (Exception e) {
		
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">routeRibbonQuery Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
  
    /**
     * 1. 路由版本保存 
     * 
     * 2. 通知网关刷新RibbonHolder
     * 
     */
 
    @RequestMapping(value = "/routeRibbonSave", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String routeRibbonSave(@RequestBody Map<String,String> request) {
    	
    	try {
			String serviceid = request.get("serviceId");
			
			String routeid = request.get("routeid");
			
			String version = request.get("version");
			
			RouteRibbonHolder maprst = rrService.saveBRRouteRibbon(serviceid,routeid,version);
			
			Message resp = new Message(maprst);
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);
			
		} catch (Exception e) {
			
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">routeRibbonSave Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
    
    /**
     * 1. 金丝雀部署保存
     * 
     * 2. 通知网关刷新RibbonHolder
     * 
     */
 
    @RequestMapping(value = "/routeCanaryRibbonSave", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String routeCanaryRibbonSave(@RequestBody Map<String,String> request) {
    	
    	try {
			String serviceid = request.get("serviceId");
			
			String routeid = request.get("routeid");
			
			String context = request.get("context");
			
			RouteRibbonHolder maprst = rrService.saveCanaryRouteRibbon(serviceid,routeid,context);
			
			Message resp = new Message(maprst);
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);
			
		} catch (Exception e) {
			
			new GatewayException(ExceptionType.AdminException,e);
			
			LOGGER.error(">routeCanaryRibbonSave Exception is:"+e.getLocalizedMessage());
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
    }
    
}
