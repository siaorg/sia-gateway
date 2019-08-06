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


package com.creditease.gateway.synch.processor;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.creditease.gateway.eureka.EurekaHandler;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.ribbon.updater.RibbonUpdater;

/**
 * @author peihua
 **/

@Service
public class UpstreamProcessor {

    private Logger logger = LoggerFactory.getLogger(UpstreamProcessor.class);

    @Autowired
    private RibbonUpdater ribbonupdater;

    @Autowired
    EurekaHandler eurekahandler;

    private UpstreamProcessor() {
        logger.info("> UpstreamProcessor start >");
    }

    public Message setoffLineEvent(Map<String, List<String>> map) {

        synchronized (this) {
            try {
                /**
                 * step1: 通知eurekaClient下线
                 */
                boolean rst = eurekahandler.setoffLineEurekaList(map, true);

                /**
                 * step2: 更新Ribbon服务列表
                 */
                if (rst) {
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        String appName = entry.getKey();
                        ribbonupdater.refreshRibbon(appName);
                    }
                }
                else {
                    Exception e = new Exception(">> eurekaList refresh failed");
                    new GatewayException(ExceptionType.CoreException, e);
                    return Message.buildExceptionResult();
                }

                return Message.buildSuccessResult();

            }
            catch (Exception e) {
                logger.error("> setoffLineEvent exception", e);
                new GatewayException(ExceptionType.CoreException, e);
                return Message.buildExceptionResult();
            }
        }
    }

}
