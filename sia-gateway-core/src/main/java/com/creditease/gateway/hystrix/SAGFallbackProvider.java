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


package com.creditease.gateway.hystrix;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.creditease.gateway.hystrix.strategy.FallbackStrategy;

/**
 *
 * 熔断降级处理类
 *
 * @author peihua
 *
 **/
@Component
public class SAGFallbackProvider implements FallbackProvider {

    private static Logger logger = LoggerFactory.getLogger(SAGFallbackProvider.class);

    @Value("${spring.application.name}")
    private String groupName;

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    /***
     * 所有路由 
     */
    private String defultRout = "*";

    @Override
    public String getRoute() {

        return defultRout;
    }

    @Autowired
    StrategyFactory factory;

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {

        logger.error("route getMessage:{}", cause.getMessage());

        StackTraceElement[] elm = cause.getStackTrace();
        StringBuffer stackTrace = new StringBuffer();

        for (StackTraceElement e : elm) {
            stackTrace.append(e.toString());
            stackTrace.append("\n");
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        FallbackEvent event = new FallbackEvent(groupName, instanceId, cause.getMessage(), stackTrace.toString(),
                currentTime);

        try {
            FallbackStrategy stg = factory.getStrategy(cause, event);

            if (null != stg) {
                stg.executeStrategy();
            }
            throw cause;

        }
        catch (Throwable e) {
            logger.error(">fallbackResponse error:" + e.getMessage());
        }
        return null;
    }

    @Override
    public ClientHttpResponse fallbackResponse() {

        return null;
    }

    public String getType(Throwable cause) {

        return "OtherError";

    }
}
