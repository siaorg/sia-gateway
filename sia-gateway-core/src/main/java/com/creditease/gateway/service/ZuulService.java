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

import com.creditease.gateway.constant.GatewayConstant.ZuulState;
import com.creditease.gateway.domain.CompInfo;

/**
 * 网关服务
 * 
 * @author peihua
 * 
 * */

public interface ZuulService {

	/**
	 * 注册网关状态
	 * 
	 * @param null
	 * @return void
	 * @exception Exception
	 * @throws Exception
	 * 
	 * */
	void registerZuul()throws Exception;
	
	/**
	 * 更新网关状态
	 * 
	 * @param stat
	 * @param zuulGourpName
	 * @return void
	 * 
	 * */
	void updateZuul(ZuulState stat, String zuulGourpName);
	
	/**
	 * 注册Filter组件
	 * 
	 * @param  comp
	 * @return boolean
	 * 
	 * */
	boolean registerFilter(CompInfo comp);

}
