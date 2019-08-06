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


package com.creditease.gateway.ribbon.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Service;

import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;

/**
 * Refresh Ribbon opt
 * 
 * @author peihua
 */

@Service
public class RibbonUpdater {

    public final static Logger logger = LoggerFactory.getLogger(RibbonUpdater.class);

    @Autowired
    private SpringClientFactory factory;

    @SuppressWarnings("rawtypes")
    public void refreshRibbon(String appName) throws Exception {

        try {
            /**
             * 刷新 ribbon
             */
            ILoadBalancer balancer = factory.getLoadBalancer(appName);
            logger.info(">refreshRibbon appName : {}, balancer : {}", appName, balancer);

            if (balancer instanceof DynamicServerListLoadBalancer) {
                ((DynamicServerListLoadBalancer) balancer).updateListOfServers();
            }
            logger.info(">end refresh ....");
        }
        catch (Exception e) {
            logger.error(">SET EUREKA-CLIENT STATUS FAIL...", e);
            new GatewayException(ExceptionType.CoreException, e);
            throw e;
        }
    }
}
