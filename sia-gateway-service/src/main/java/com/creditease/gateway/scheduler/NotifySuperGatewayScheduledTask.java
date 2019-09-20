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


package com.creditease.gateway.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.message.ZuulHandler;

/**
 * 超级网关定时加载路由更新操作
 * 
 * @author peihua
 * 
 */

@Component
public class NotifySuperGatewayScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySuperGatewayScheduledTask.class);

    @Autowired
    ZuulHandler handler;

    @Autowired
    protected DiscoveryService zuulDiscovery;

    /**
     * 每小时刷新超级网关路由通知
     * 
     * @throws Exception
     * 
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void notifyCronTask() {

        LOGGER.info(">开始刷新超级网关通知事件 time:{}", new Date());

        List<String> zuulList = new ArrayList<String>();
        try {
            zuulList = zuulDiscovery.getServiceList(GatewayConstant.API_GATEWAY_CORE);
        }
        catch (Exception e) {
            LOGGER.error("刷新超级网关通知 Exception:{}", e);
        }
        for (String path : zuulList) {
            String url = "http://" + path;
            String result = null;

            try {
                result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.RR.getValue(), null);
            }
            catch (Exception e) {
                LOGGER.error("> notifyCronTask exception: " + e);
            }
            LOGGER.info("refreshRoute rst:{}", result);
        }

    }

}
