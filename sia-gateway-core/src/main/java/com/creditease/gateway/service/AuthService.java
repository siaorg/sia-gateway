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


package com.creditease.gateway.service;

import com.creditease.gateway.message.Message;
import com.netflix.zuul.context.RequestContext;

/**
 * 黑白名单服务
 * 
 * @author peihua
 * 
 * */
public interface AuthService {

	/**
	 * 更新黑白名单
	 * @param msg
	 * @return boolean
	 * 
	 * */
	boolean updateBWList(Message msg);

	/**
	 * 做全局黑白名单过滤处理
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void doGlobalbwListFilter(RequestContext ctx, String routeid);
	
	/**
	 * 做路由黑白名单过滤处理
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void doRoutebwListFilter(RequestContext ctx, String routeid);
	
	/**
	 * 检查Token认证
	 * @param accessToken
	 * @param routeid
	 * @return int
	 * 
	 * */
	int checkAuthToken(String accessToken,String routeid);

	/**
	 * 删除黑白名单
	 * @param msg
	 * @return boolean
	 * 
	 * */
	boolean deleteBWList(Message msg);
}
