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
 * Filter报文结构
 * 
 * @author peihua
 * 
 * 
 */
public class BwfilterObj {

	private String routeid;
	
	private String groupId;
	
	private String strategy;
	
	private String type;
	
	private Timestamp createTime;
	
	private Timestamp updateTime;
	
	private Boolean enabled;
	
	private String desc;
	
	private String list;

	private String jsessionid;

	public String getJsessionid() {
		return jsessionid;
	}

	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}



	public String getGroupId() {

		return groupId;
	}

	public void setGroupId(String groupId) {

		this.groupId = groupId;
	}

	public String getStrategy() {

		return strategy;
	}

	public void setStrategy(String strategy) {

		this.strategy = strategy;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public Timestamp getCreateTime() {

		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {

		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {

		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {

		this.updateTime = updateTime;
	}

	public Boolean getEnabled() {

		return enabled;
	}

	public void setEnabled(Boolean enabled) {

		this.enabled = enabled;
	}

	public String getDesc() {

		return desc;
	}

	public void setDesc(String desc) {

		this.desc = desc;
	}

	public String getList() {

		return list;
	}

	public void setList(String list) {

		this.list = list;
	}

	public String getRouteid() {

		return routeid;
	}

	public void setRouteid(String serviceid) {

		this.routeid = serviceid;
	}
}
