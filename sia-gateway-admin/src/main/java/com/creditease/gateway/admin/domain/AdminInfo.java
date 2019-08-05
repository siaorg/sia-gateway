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


import java.sql.Timestamp;


/**
 * Admin對象
 * 
 * @author peihua
 * 
 * */

public class AdminInfo {

	private String adminInstanceId;
	
	private String adminHotsName;
	
	private Timestamp zuulLastStartTime;
	
	private String adminStatus;
	
	public String getAdminInstanceid() {
		return adminInstanceId;
	}

	public void setAdminInstanceid(String adminInstanceid) {
		this.adminInstanceId = adminInstanceid;
	}

	public String getAdminHotsName() {
		return adminHotsName;
	}

	public void setAdminHotsName(String adminHotsName) {
		this.adminHotsName = adminHotsName;
	}

	public Timestamp getZuulLastStartTime() {
		return zuulLastStartTime;
	}

	public void setZuulLastStartTime(Timestamp zuulLastStartTime) {
		this.zuulLastStartTime = zuulLastStartTime;
	}

	public String getAdminStatus() {
		return adminStatus;
	}

	public void setAdminStatus(String adminStatus) {
		this.adminStatus = adminStatus;
	}

}
