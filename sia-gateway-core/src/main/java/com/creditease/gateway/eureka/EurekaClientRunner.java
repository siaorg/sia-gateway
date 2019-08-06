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


package com.creditease.gateway.eureka;

import com.creditease.gateway.eureka.listener.EurekaRefreshListener;
import com.creditease.gateway.service.EurekaService;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 注册eureka监听
 *
 * @author peihua
 **/
@SuppressWarnings("deprecation")
@Component
public class EurekaClientRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EurekaClientRunner.class);

    @Autowired
    private EurekaRefreshListener listener;

    @Autowired
    private EurekaService eurekaService;

    @Override
    public void run(String... args) {

        logger.info("注册eureka监听 开始初始化初始化。。。。。。。");

        DiscoveryClient client = DiscoveryManager.getInstance().getDiscoveryClient();
        // 注册监听服务列表改变等listener
        if (null != client) {
            client.registerEventListener(listener);
        }

        logger.info("OnEurekaRefreshListener register success!");

        boolean result = eurekaService.setLocalEurekaUrls();

        logger.info("Runner setEurekaUrls:{}", result);
    }
}
