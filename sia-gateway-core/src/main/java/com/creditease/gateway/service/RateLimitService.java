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

import java.util.List;
import java.util.Map;
import com.netflix.zuul.context.RequestContext;

/**
 * 限流服務
 * 
 * @author peihua
 * 
 */
public interface RateLimitService {

	/**
	 * 做限流处理
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void runRateLimit(RequestContext ctx, String routeid);

	/**
	 * 设置限流参数
	 * @param routlimitMap
	 * @return void
	 * 
	 * */
	void setRoutlimitMap(List<Map<String, Object>> routlimitMap);
	
	/**
	 * 刷新限流参数
	 * @param null
	 * @return boolean
	 * 
	 * */
	boolean refreshRateLimtMap();
}
