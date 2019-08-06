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

import com.creditease.gateway.reactive.impl.BaseEventInvoke;
import com.creditease.gateway.register.GatewayContextMgt;

/**
 * 熔断管理对象
 * 
 * @author peihua
 * 
 */
public class FallbackEvent extends BaseEventInvoke<FallbackEvent> {

    private String zuulGroupName;
    private String instanceId;

    private String fallbackType;

    private String fallbackMsg;

    private String stackTrace;

    private Timestamp startTime;

    public FallbackEvent(String zuulGroupName, String instanceId, String fallbackMsg, String stackTrace,
            Timestamp startTime) {

        this.zuulGroupName = zuulGroupName;
        this.instanceId = instanceId;
        this.fallbackMsg = fallbackMsg;
        this.stackTrace = stackTrace;
        this.startTime = startTime;
    }

    @Override
    public void run(BaseEventInvoke<FallbackEvent> evt) {

        GatewayContextMgt.getZuulRepo().insertFallback((FallbackEvent) evt);

    }

    @Override
    public EventType getEventType() {

        return EventType.CPU;
    }

    public String getZuulGroupName() {

        return zuulGroupName;
    }

    public void setZuulGroupName(String zuulGroupName) {

        this.zuulGroupName = zuulGroupName;
    }

    public String getInstanceId() {

        return instanceId;
    }

    public void setInstanceId(String instanceId) {

        this.instanceId = instanceId;
    }

    public String getFallbackType() {

        return fallbackType;
    }

    public void setFallbackType(String fallbackType) {

        this.fallbackType = fallbackType;
    }

    public String getFallbackMsg() {

        return fallbackMsg;
    }

    public void setFallbackMsg(String fallbackMsg) {

        this.fallbackMsg = fallbackMsg;
    }

    public String getStackTrace() {

        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {

        this.stackTrace = stackTrace;
    }

    public Timestamp getStartTime() {

        return startTime;
    }

    public void setStartTime(Timestamp startTime) {

        this.startTime = startTime;
    }

}
