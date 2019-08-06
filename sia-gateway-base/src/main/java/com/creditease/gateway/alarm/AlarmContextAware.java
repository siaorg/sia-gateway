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


package com.creditease.gateway.alarm;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 預警Context管理
 * 
 * @author peihua
 * 
 */
@Component
public class AlarmContextAware implements ApplicationContextAware {

	private static AlarmService alarmservice;
	
	public static AlarmService getAlarmservice() {
		return alarmservice;
	}

	public static void setAlarmservice(AlarmService alarmservice) {
		AlarmContextAware.alarmservice = alarmservice;
	}

	private AlarmContextAware context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		setContext(applicationContext.getBean(AlarmContextAware.class));
		AlarmContextAware.alarmservice = applicationContext.getBean(AlarmService.class);
		
	}

	public AlarmContextAware getContext() {
		return context;
	}

	public void setContext(AlarmContextAware context) {
		this.context = context;
	}

}
