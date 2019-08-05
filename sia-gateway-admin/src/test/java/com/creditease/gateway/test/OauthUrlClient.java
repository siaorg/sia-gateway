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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author peihua
 * 
 * Created by Administrator on 2015/5/29.
 */
public class OauthUrlClient {

    /**
     * 获取accessToken
     *
     * @return
     */
    private static String getAccessToken() throws Exception {

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("client_id", ClientParams.CLIENT_ID);
        params.put("client_secret", ClientParams.CLIENT_SECRET);
        params.put("grant_type", "authorization_code");
        params.put("redirect_uri", ClientParams.OAUTH_SERVER_REDIRECT_URI);

        StringBuilder postStr = new StringBuilder();

        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postStr.length() != 0) {
                postStr.append('&');
            }
            postStr.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postStr.append('=');
            postStr.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }

        byte[] postStrBytes = postStr.toString().getBytes("UTF-8");

        URL url = new URL(ClientParams.OAUTH_SERVER_TOKEN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.POST);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(postStrBytes.length));
        connection.getOutputStream().write(postStrBytes);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        Gson gson = new GsonBuilder().create();
        Map<String, String> map = gson.fromJson(response.toString(), Map.class);
        String accessToken = map.get("access_token");
        System.out.println(accessToken);
        return accessToken;
    }


    /**
     * 获取accessToken
     *
     * @return
     */
    private static void getService(String accessToken) throws Exception {

        URL url = new URL(ClientParams.OAUTH_SERVICE_API + "?access_token=" + accessToken);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.GET);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
    }


    public static void main(String[] args) throws Exception {
       
        String accessToken = getAccessToken();
        getService(accessToken);
    }
}
