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
 *@author peihua 
 *
 * */

public class AuthConstants {
    public static final String RESOURCE_SERVER_NAME = "oauth-server";
    public static final String INVALID_CLIENT_ID = "客户端验证失败，如错误的client_id/client_secret。";
    public static final String INVALID_TIME = "客户端验证失败，安全口令时间无效";
    public static final String INVALID_ACCESS_TOKEN = "accessToken无效或已过期。";
    public static final String INVALID_REDIRECT_URI = "缺少授权成功后的回调地址。";
    public static final String INVALID_AUTH_CODE = "错误的授权码。";
}
