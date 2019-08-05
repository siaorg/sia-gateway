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


package com.creditease.gateway.admin.domain;


/**
 * 网关管理對象
 * 
 * @author peihua
 * 
 * */

public class MutiZuul {

	private String zuulGroupName;
	
	private int instanceNo;
	
	private String zuulDesc;
	
	public String getZuulDesc() {
		return zuulDesc;
	}

	public void setZuulDesc(String zuulDesc) {
		this.zuulDesc = zuulDesc;
	}

	private String zuulStatus;
	
	public String getZuulStatus() {
		return zuulStatus;
	}

	public void setZuulStatus(String zuulStatus) {
		this.zuulStatus = zuulStatus;
	}

	public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

	public int getInstanceNo() {
		return instanceNo;
	}

	public void setInstanceNo(int instanceNo) {
		this.instanceNo = instanceNo;
	}





}
