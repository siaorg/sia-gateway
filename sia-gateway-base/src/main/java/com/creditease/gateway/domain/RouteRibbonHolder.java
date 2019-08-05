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
 * 路由RibbonMap
 * 
 * @author peihua
 **/

public class RouteRibbonHolder {

    private String routeid;
    
    private String serviceid;

	private String currentVersion;
    
    private String allVersions;
    
    private Timestamp updateTime;
    
    private String strategy;
    
    private String context;
    

	public RouteRibbonHolder()
    {
    	
    }
    public RouteRibbonHolder(String routeid, String serviceid, String currentVersion,String allVersions,Timestamp updateTime,String strategy)
    {
    	this.routeid = routeid;
    	
    	this.serviceid = serviceid;
    	
    	this.currentVersion = currentVersion;
    	
    	this.allVersions = allVersions;
    	
    	this.updateTime = updateTime;
    	
    	this.strategy = strategy;
    }
    
	public String getServiceid() {
		return serviceid;
	}
	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}
	
    public String getRouteid() {
		return routeid;
	}

	public void setRouteid(String routeid) {
		this.routeid = routeid;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getAllVersions() {
		return allVersions;
	}

	public void setAllVersions(String allVersions) {
		this.allVersions = allVersions;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
    public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}

}
