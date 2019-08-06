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


import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message.ResponseCode;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author peihua
 * 
 * Auto-Client-demo
 * 
 * 
 * **/

public class OauthClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthClient.class);


    public static void main(String[] args) throws Exception {

        send(makeTokenRequestWithAuthCode().getAccessToken());
    }

    /**
     *
     * @param
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    private static OAuthAccessTokenResponse makeTokenRequestWithAuthCode() throws OAuthProblemException, OAuthSystemException {

        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(ClientParams.OAUTH_SERVER_TOKEN_URL)
                .setClientId(ClientParams.CLIENT_ID)
                .setClientSecret(ClientParams.CLIENT_SECRET)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .buildBodyMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);

        System.out.println("Access Token: " + oauthResponse.getAccessToken());

        return oauthResponse;
    }

    private static void send(String token) throws OAuthSystemException, OAuthProblemException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ClientParams.OAUTH_SERVICE_API);
        post.addHeader("Authorization",token);
        // 响应内容
        String responseContent = null; 
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            System.out.println(JsonHelper.toString(response));
            if (response.getStatusLine().getStatusCode() == ResponseCode.SUCCESS_CODE.getCode()) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }

            if (response != null)
            {    
            	response.close();
            }
            if (client != null)
            {
                client.close();
            }
            LOGGER.info("<<<<<<<<<<<<<<<<<<responseContent:" + responseContent);

        } catch(ClientProtocolException e) {
            LOGGER.info(e.getMessage());
        } catch(IOException e) {
            LOGGER.info(e.getMessage());
        }
    }



}
