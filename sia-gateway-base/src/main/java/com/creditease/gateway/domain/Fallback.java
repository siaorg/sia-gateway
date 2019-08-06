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
 * 路由Domain
 * 
 * @author peihua
 **/

public class Fallback {

private String zuulGroupName;
	
	private String zuulInstance;

	private String fallbackType;

	private String fallbackMsg;
	
	private String stackTrace;
	
	private Timestamp startTime;
	
	public String getZuulInstance() {
		return zuulInstance;
	}

	public void setZuulInstance(String zuulInstance) {
		this.zuulInstance = zuulInstance;
	}
	
	public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

	public String getFallbackMsg() {
		return fallbackMsg;
	}

	public void setFallbackMsg(String fallbackMsg) {
		this.fallbackMsg = fallbackMsg;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	
	
	public String getFallbackType() {
		return fallbackType;
	}

	public void setFallbackType(String fallbackType) {
		this.fallbackType = fallbackType;
	}
}
