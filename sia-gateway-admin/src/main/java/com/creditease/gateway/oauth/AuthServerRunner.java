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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.creditease.gateway.oauth.admin.OauthAdminService;
import com.creditease.gateway.oauth.server.OauthServer;

/**
 * 认证服务器初始化動作
 *
 * @author peihua
 **/

@Component
public class AuthServerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerRunner.class);

    @Autowired
    OauthAdminService authAdminService;

    @Autowired
    OauthServer authServer;

    @Override
    public void run(String... args) {

        LOGGER.info("AuthServerRunner开始初始化");

        registerAuthCache();
    }

    public void registerAuthCache() {

        try {
            Map<String, String> tokenMap = authServer.getTockencache();
            Map<String, String> secretMap = authServer.getSecretcache();
            Map<String, String> tokenUpdateTimeMap = authServer.getTokenUpdateTimecache();


            List<AuthInfo> authList = authAdminService.findAll();
            for (AuthInfo authInfo : authList) {
                String routeId = authInfo.getRouteid();
                String secret = authInfo.getClient_secret();
                String token = authInfo.getToken();

                if (token != null) {
                    tokenMap.put(routeId, token);
                    tokenUpdateTimeMap.put(token, authInfo.getTokenUpdateTime());
                }

                if (secret != null) {
                    secretMap.put(routeId, secret);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
