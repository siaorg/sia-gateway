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


package com.creditease.gateway.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author peihua
 * 
 * serviceidMapInstanceMap
 * 
 * service: InstatnceId
   {
	  log: [10.2.23:9080]
   }   
 **/

public class GatewayListMsg {
	
	private String zuulGroupName;

	private String opt;
	
	private  Map<String, List<String>> serviceidMapInstanceMap = new LinkedHashMap<String, List<String>>();
	
	public GatewayListMsg init(String zuulGroupName, Map<String, List<String>> map ,String opt)
	{
		
		this.zuulGroupName = zuulGroupName;
		
		this.opt = opt;
		
		if(null == serviceidMapInstanceMap)
		{
			serviceidMapInstanceMap = new LinkedHashMap<String, List<String>>();;
		}else
		{
			serviceidMapInstanceMap.putAll(map);
		}
		return this;
	}
	
	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}
	

	public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

	public Map<String, List<String>> getServiceidMapInstanceMap() {
		return serviceidMapInstanceMap;
	}

	public void setServiceidMapInstanceMap(Map<String, List<String>> serviceidMapInstanceMap) {
		this.serviceidMapInstanceMap = serviceidMapInstanceMap;
	}

}
