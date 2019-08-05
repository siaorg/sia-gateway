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

import java.util.List;
import java.util.Map;
/**
 * 
 * GatewayList Model
 * 
 * @author peihua
 **/

public class GatewayListDB {
	
	private String zuulGroupName;
	               
	private String zuulInstanceId;

	private String serviceId;

	private  Map<String, List<String>> serviceidMapInstanceList;

	private String opt;
	
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

	
	public Map<String, List<String>> getServiceidMapInstanceList() {
		return serviceidMapInstanceList;
	}

	public void setServiceidMapInstanceList(Map<String, List<String>> serviceidMapiInstanceList) {
		this.serviceidMapInstanceList = serviceidMapiInstanceList;
	}
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getZuulInstanceId() {
		return zuulInstanceId;
	}

	public void setZuulInstanceId(String zuulInstanceId) {
		this.zuulInstanceId = zuulInstanceId;
	}
}
