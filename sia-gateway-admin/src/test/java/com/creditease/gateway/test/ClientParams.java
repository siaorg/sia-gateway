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


package com.creditease.gateway.test;

/**
 * 封装OAuth Server端认证需要的参数
 * 
 * 
 *@author peihua 
 *
 * */

public class ClientParams {

	/**
	 *  应用id CLIENT_ID
	 */
    public static final String CLIENT_ID = "hh"; 

    /**
     *  应用secret CLIENT_SECRET
     */
    public static final String CLIENT_SECRET = "123456"; 

    /**
     *  ACCESS_TOKEN换取地址
     */
    public static final String OAUTH_SERVER_TOKEN_URL = "http://localhost:8090/oauth/token"; 

    /**
     *  回调地址
     */
    public static final String OAUTH_SERVER_REDIRECT_URI = "http://notes.coding.me"; 

    /**
     *  测试开放数据api
     */
    public static final String OAUTH_SERVICE_API = "http://localhost:8080/hh/available"; 

}
