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


package com.creditease.gateway.excpetion;

import com.creditease.gateway.alarm.AlarmContextAware;
import com.creditease.gateway.alarm.AlarmService;
import com.creditease.gateway.helper.DataConvertHelper;
import com.creditease.gateway.helper.StringHelper;

/***
 * 网关统一异常
 * 
 * @author peihua
 * 
 */

public class GatewayException extends Exception {

	protected AlarmService alarmservice = AlarmContextAware.getAlarmservice();
	private static final long serialVersionUID = 1L;
	public ExceptionType type;
	public String errorCause;
	public String message;

	public enum ExceptionType {
		AdminException, ServcieException, CoreException, StreamException, MonitorException,SynchSpeedException,CommExeption
	}

	/**
	 * Source Throwable, message, status code and info about the cause
	 * 
	 * @param throwable
	 * @param sMessage
	 * @param nStatusCode
	 * @param errorCause
	 */
	public GatewayException(Throwable throwable, String message, ExceptionType type, String errorCause) {
		super(message, throwable);
		this.type = type;
		this.errorCause = errorCause;
	}

	public GatewayException(ExceptionType type, Exception e) {
		super(e.getMessage());
		message = e.getMessage();
		message = StringHelper.isEmpty(message) ? DataConvertHelper.exception2String(e) : message;
		errorCause = null == e.getCause() ? DataConvertHelper.exception2String(e) : errorCause;
		this.initErrorMessage(message, type, errorCause, DataConvertHelper.exception2String(e));
	}

	public GatewayException(String sMessage, ExceptionType type, String errorCause, String primaryKey) {

		this.initErrorMessage(sMessage, type, errorCause, primaryKey);
	}

	private void initErrorMessage(String message, ExceptionType type, String errorCause, String primaryKey) {
		this.type = type;
		this.errorCause = errorCause;
		this.message = message;

		StringBuffer b = new StringBuffer();

		b.append("ErrorType:" + type + "\n");
		b.append("Message:" + message + "\n");
		b.append("errorCause:" + errorCause + "\n");

		if (!StringHelper.isEmpty(primaryKey)) {
			alarmservice.reportAlarm(b.toString(), processPrimaryStr(primaryKey));
		}
	}

	private String processPrimaryStr(String primaryKey) {
		try {
			String[] exceptionInfos = primaryKey.split("\n");
			if (exceptionInfos.length > 1) {
				primaryKey = exceptionInfos[0] + exceptionInfos[1];
			} else if (exceptionInfos.length == 1) {
				primaryKey = exceptionInfos[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKey;
	}

}
