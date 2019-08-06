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

import com.alibaba.fastjson.JSON;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.MutiZuul;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 获取用户所属网关组
 * 
 * @author peihua
 */

@RestController
public class AdminAuthController extends BaseAdminController {

	public static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthController.class);

	@Autowired
	AdminService adminService;

	@Autowired
	AuthInterceptor authservice;

	@RequestMapping(value = "/getMutiGroupNames", produces = "application/json;charset=UTF-8")
	public String getMutiGroupNames() {

		try {
			Map<String, MutiZuul> zuulGroupNameList = authservice.getMutiGroupName();
			LOGGER.info(">zuulGroupNameList{}", JSON.toJSONString(zuulGroupNameList));
			if (zuulGroupNameList != null) {
				Message adminMsg = new Message(zuulGroupNameList.values());
				ObjectMapper mapper = new ObjectMapper();
				return mapper.writeValueAsString(adminMsg);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
		LOGGER.info(">Error,返回");
		return null;
	}

	@RequestMapping(value = "/setCurrentGroupName", produces = "application/json;charset=UTF-8")
	public String setCurrentGroupName(@RequestBody String jsonString) {

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> map = JsonHelper.toObject(jsonString, Map.class);

			String currentRole = map.get("currentRole");

			String groupName = authservice.setCurrentRole(currentRole);
			LOGGER.info(">设置当前用户组用户权限：{}", groupName);
			Message adminMsg = new Message(groupName);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(adminMsg);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	@RequestMapping(value = "/getGroupName", produces = "application/json;charset=UTF-8")
	public String getGroupName() {

		try {
			String groupName = authservice.getZuulGroupName();
			LOGGER.info(">取得當前用户组用户权限：{}", groupName);

			Message adminMsg = new Message(groupName);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(adminMsg);

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}
}
