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

import java.util.HashMap;
import java.util.Map;

import com.creditease.gateway.domain.RouteRibbonHolder;
import com.netflix.zuul.context.RequestContext;

/**
 * 藍綠部署
 * 
 * @author peihua
 * 
 * */


public interface RouteRibbonService {

	/**
	 * 刷新蓝绿部署
	 * @param null
	 * @return boolean
	 * 
	 * */
	boolean routeRibbonRefresh();
	
	/**
	 * 灰度部署
	 * @param ctx
	 * @param routeid
	 * @return void
	 * 
	 * */
	void runRibbon(RequestContext ctx, String routeid);
	
	
	/**
	 * 设置蓝绿部署参数
	 * @param ribbonHolder
	 * @return void
	 * 
	 * */
	void setRibbonHolder(HashMap<String, RouteRibbonHolder> ribbonHolder);
		
	/**
	 * 取得蓝绿部署Map
	 * @param null
	 * @return Map
	 * 
	 * */
	Map<String, RouteRibbonHolder> getRibbonHolder();
	
}
