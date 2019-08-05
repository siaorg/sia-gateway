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
 * 
 * 預警公共Domain
 * 
 * @author peihua
 * 
 * */

public class AlarmInfo {
	
    private Integer id;
    private String zuulInstance;
    private Timestamp alarmCreateTime;
	private String alarmInfomation;
	
	private String alarmtype;

    public String getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(String alarmtype) {
		this.alarmtype = alarmtype;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZuulInstance() {
        return zuulInstance;
    }

    public void setZuulInstance(String zuulInstance) {
        this.zuulInstance = zuulInstance;
    }

    public String getAlarmInfomation() {
        return alarmInfomation;
    }

    public void setAlarmInfomation(String alarmInfomation) {
        this.alarmInfomation = alarmInfomation;
    }
    
    public Timestamp getAlarmCreateTime() {
		return alarmCreateTime;
	}

	public void setAlarmCreateTime(Timestamp alarmCreateTime) {
		this.alarmCreateTime = alarmCreateTime;
	}
}
