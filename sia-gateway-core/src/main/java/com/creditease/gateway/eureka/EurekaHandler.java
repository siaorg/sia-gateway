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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creditease.gateway.cache.UpstreamCacheManager;
import com.creditease.gateway.domain.UpstreamObj;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Application;

/**
 * 
 * Eureka事件响应
 * 
 * @author peihua
 **/

@SuppressWarnings("deprecation")
@Service
public class EurekaHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UpstreamCacheManager upstreamCache;

    public boolean setoffLineEurekaList(Map<String, List<String>> map, boolean httpEvent) {

        synchronized (this) {
            try {
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    String appName = entry.getKey();
                    List<String> instanceids = entry.getValue();

                    setOffLineStatus(appName, instanceids, httpEvent);
                }
            }
            catch (Exception e) {
                logger.error("Cannot reflect!!!", e);
                new GatewayException(ExceptionType.CoreException, e);
                return false;
            }
        }
        return true;
    }

    public boolean setOffLineStatus(String appName, List<String> instanceids, boolean httpEvent) {

        try {
            DiscoveryClient client = DiscoveryManager.getInstance().getDiscoveryClient();

            Application app = client.getApplication(appName.toUpperCase());
            if (app != null) {
                for (String instancenId : instanceids) {
                    app.getByInstanceId(instancenId).setStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
                    logger.info("setOffLine Instance:{}",
                            instancenId + "|" + app.getByInstanceId(instancenId).getStatus());

                    if (httpEvent && app.getByInstanceId(instancenId)
                            .getStatus() == InstanceInfo.InstanceStatus.OUT_OF_SERVICE) {

                        UpstreamObj downServerObj = new UpstreamObj(appName, instancenId);

                        upstreamCache.getUpstreamCache().put(System.currentTimeMillis(), downServerObj);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("Cannot reflect!!!", e);
            new GatewayException(ExceptionType.CoreException, e);
            return false;
        }

        return true;
    }

}
