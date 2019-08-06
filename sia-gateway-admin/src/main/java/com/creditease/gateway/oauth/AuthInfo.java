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


package com.creditease.gateway.oauth;

/**
 * @author peihua
 *
 */
public class AuthInfo {

	private String routeid;

	private String client_secret;

	private String token;
	private String startTime;
	private String tokenUpdateTime;
	private String endTime;

	public String getTokenUpdateTime() {
		return tokenUpdateTime;
	}

	public void setTokenUpdateTime(String tokenUpdateTime) {
		this.tokenUpdateTime = tokenUpdateTime;
	}

	public String getStartTime() {

		return startTime;
	}

	public void setStartTime(String startTime) {

		this.startTime = startTime;
	}

	public String getEndTime() {

		return endTime;
	}

	public void setEndTime(String endTime) {

		this.endTime = endTime;
	}

	public String getRouteid() {

		return routeid;
	}

	public void setRouteid(String routeid) {
		this.routeid = routeid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
}
