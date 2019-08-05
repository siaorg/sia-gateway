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


import java.sql.Timestamp;

/**
 * zuul配置信息
 * 
 * @author peihua
 * 
 * */

public class ZuulInfo {

	private String zuulInstanceId;

	private String zuulHotsName;
	
	private String zuulGroupName;
	
	private String zuulDesc;
	
	private int zuulRouteEnable;
	
	private Timestamp zuulLastStartTime;
	
	private String zuulStatus;
	
	public String getZuulInstanceId() {
		return zuulInstanceId;
	}

	public void setZuulInstanceId(String zuulInstanceId) {
		this.zuulInstanceId = zuulInstanceId;
	}

	public String getZuulHotsName() {
		return zuulHotsName;
	}

	public void setZuulHotsName(String zuulHotsName) {
		this.zuulHotsName = zuulHotsName;
	}

	public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

	public String getZuulDesc() {
		return zuulDesc;
	}

	public void setZuulDesc(String zuulDesc) {
		this.zuulDesc = zuulDesc;
	}

	public int getZuulRouteEnable() {
		return zuulRouteEnable;
	}

	public void setZuulRouteEnable(int zuulRouteEnable) {
		this.zuulRouteEnable = zuulRouteEnable;
	}

	public Timestamp getZuulLastStartTime() {
		return zuulLastStartTime;
	}

	public void setZuulLastStartTime(Timestamp zuulLastStartTime) {
		this.zuulLastStartTime = zuulLastStartTime;
	}

	public String getZuulStatus() {
		return zuulStatus;
	}

	public void setZuulStatus(String zuulStatus) {
		this.zuulStatus = zuulStatus;
	}
}
