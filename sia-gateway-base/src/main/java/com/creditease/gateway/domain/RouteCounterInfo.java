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


/**
 * 
 * Counter计数公共Domain
 * 
 * @author peihua
 * 
 * */

public class RouteCounterInfo {
	
    private String zuulInstance;
	private String datetime;
	
	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	private String couterKey;
	private int counterValue;
	

	public RouteCounterInfo(String zuulInstance,String couterKey,int counterValue,String date)
	{
		this.zuulInstance = zuulInstance;
		
		this.couterKey = couterKey;
		this.counterValue = counterValue;
		
		this.datetime = date;
	}
	
    public String getZuulInstance() {
		return zuulInstance;
	}
	public void setZuulInstance(String zuulInstance) {
		this.zuulInstance = zuulInstance;
	}

	public String getCouterKey() {
		return couterKey;
	}
	public void setCouterKey(String couterKey) {
		this.couterKey = couterKey;
	}
	public int getCounterValue() {
		return counterValue;
	}
	public void setCounterValue(int counterValue) {
		this.counterValue = counterValue;
	}


}
