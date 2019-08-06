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

import java.util.List;

import com.creditease.gateway.domain.CompInfo;


/**
 * CompAdapter對象
 * 
 * @author peihua
 * 
 * */

public class CompAdapter {

	private String firstLevel;
	
	private String cname;

	private List<CompInfo> compList;
    
	public String getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(String firstLevel) {
		this.firstLevel = firstLevel;
	}

	public CompAdapter(String firstLevel,String cname,List<CompInfo> compList)
	{
		this.firstLevel = firstLevel;
		this.cname = cname;
		this.compList = compList;
	}

	public List<CompInfo> getCompList() {
		return compList;
	}

	public void setCompList(List<CompInfo> compList) {
		this.compList = compList;
	}
	
    public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

}
