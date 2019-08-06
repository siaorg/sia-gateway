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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.FallbackQuery;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.admin.service.FallbackService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;

import io.swagger.annotations.ApiOperation;

/**
 * 熔断管理
 * 
 * @author peihua
 */

@RestController
public class FallbackController extends BaseAdminController {

	public static final Logger LOGGER = LoggerFactory.getLogger(FallbackController.class);

	@Autowired
	AuthInterceptor authInterceptor;

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	FallbackService fallbackservice;

	@ApiOperation(value = "按条件搜索熔断信息")
	@RequestMapping(value = "/getFallback", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Message getFallback(@RequestBody FallbackQuery fallbackQuery) {

		try {
			return fallbackservice.queryFallbackInfos(fallbackQuery);
		} catch (Exception e) {
			LOGGER.error("getFallback Excetion:{}", e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
		}
		return null;
	}
}
