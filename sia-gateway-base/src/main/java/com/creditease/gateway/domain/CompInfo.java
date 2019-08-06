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
 * 组件對象
 * 
 * @author peihua
 * 
 * */
public class CompInfo {

	private String compName;
	
	private String compFilterName;

	private String compType;
	
	private String status;
	
	private Timestamp compUpdateTime;
	
	private int  compOrder;
	
	private String routeidList;
	
	private String compdesc;
	
	private String zuulGroupName;

	public CompInfo()
	{
		
	}
	
	public CompInfo(String compName, String compFilterName, String compType, String compDesc,int compOrder,Timestamp compUpdateTime,String status,String zuulGroupname)
	{
		this.compName=compName;
		this.compFilterName=compFilterName;
		this.compType=compType;
		this.compOrder=compOrder;
		this.compUpdateTime=compUpdateTime;
		this.status=status;
		this.compdesc = compDesc;
		this.zuulGroupName = zuulGroupname;
	}
	
	public String getCompdesc() {
		return compdesc;
	}

	public void setCompdesc(String compdesc) {
		this.compdesc = compdesc;
	}

	
	public String getRouteidList() {
		return routeidList;
	}

	public void setRouteidList(String routeidList) {
		this.routeidList = routeidList;
	}

	private Object context;
	
	
	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getCompType() {
		return compType;
	}

	public void setCompType(String compType) {
		this.compType = compType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCompUpdateTime() {
		return compUpdateTime;
	}

	public void setCompUpdateTime(Timestamp compUpdateTime) {
		this.compUpdateTime = compUpdateTime;
	}

	public int getCompOrder() {
		return compOrder;
	}

	public void setCompOrder(int compOrder) {
		this.compOrder = compOrder;
	}


	public String getCompFilterName() {
		return compFilterName;
	}

	public void setCompFilterName(String compFilterName) {
		this.compFilterName = compFilterName;
	}
	
	public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

}
