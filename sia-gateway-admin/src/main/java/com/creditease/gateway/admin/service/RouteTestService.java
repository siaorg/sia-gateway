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


package com.creditease.gateway.admin.service;

import com.alibaba.fastjson.JSON;
import com.creditease.gateway.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 路由连通性测试
 * 
 * @author peihua
 * 
 * 
 */

@Service
public class RouteTestService extends BaseAdminService{
	
	@Autowired
	private RestTemplate restTemplate;
	
	enum RouteStatus {
		/**
		 * EDIT:编辑状态
		 * ONLINE：发布状态
		 * DOWNLINE：下线状态
		 * */	
		EDIT, ONLINE, DOWNLINE
	}
	
	/**
	 * 路由Test
	 * 
	 */
	public Message testRoute(Map<String,String> request){
		try {
			LOGGER.info("request = {}", JSON.toJSONString(request));

			String url = request.get("url");
			String method = request.get("method");
			String body = request.get("body");

			if(StringUtils.isEmpty(url) || StringUtils.isEmpty(method)){
				return Message.buildInValidParamResult();
			}
			String result = null;
			StringBuffer b =  new StringBuffer();
			if("POST".equals(method)){
				 result = restTemplate.postForObject(url, JSON.parse(body), String.class);
			} else {
				@SuppressWarnings("rawtypes")
				ResponseEntity responseEntity = handler.executeHttpTestCmd(url, "GET", null);
				result = responseEntity.getBody().toString();
			}

			LOGGER.info("testRoute rst:{}" ,result);
			b.append("HttpResponse Body :");
			b.append(result);
			
			return Message.buildSuccessResult(b.toString());
		} catch (Exception e) {
			LOGGER.error("Exception:{}",e.getCause());
			return Message.buildExceptionResult(e);
		}

	}

}
