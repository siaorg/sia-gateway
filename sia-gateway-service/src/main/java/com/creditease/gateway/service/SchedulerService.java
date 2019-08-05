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

import com.creditease.gateway.domain.ZuulInfo;

/**
 * 定时任务接口
 * 
 * @author peihua
 * 
 * */

public interface SchedulerService {

	/**
	 * 取得Counter個數
	 * 
	 * @param counterKeysuccess
	 * @return int
	 * @throws Exception
	 * */
	int getCounter(String counterKeysuccess)throws Exception;
 
	/**
	 * 取得ZUUL实例列表
	 * 
	 * @return List
	 * @throws Exception
	 * 
	 * */
	List<ZuulInfo> getZuulList()throws Exception;
	
	/**
	 * 取得路由ID列表
	 *
	 * @return String
	 * @throws Exception
	 * 
	 * */
	String getStatisicBindRouteIdlist()throws Exception;
}
