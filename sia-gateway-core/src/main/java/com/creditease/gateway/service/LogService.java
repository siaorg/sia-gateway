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

import com.netflix.zuul.context.RequestContext;

/**
 * 日志服务
 * 
 * @author peihua
 * 
 * */
public interface LogService {

	/**
	 * 记录请求日志
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void recordRequest(RequestContext ctx, String routeid);
	
	/**
	 * 记录响应日志
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void recordResponse(RequestContext ctx, String routeid);
}
