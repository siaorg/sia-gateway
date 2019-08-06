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


package com.creditease.gateway.topology;

import com.creditease.gateway.reactive.impl.BaseEventInvoke;
import com.creditease.gateway.register.GatewayContextMgt;

/**
 * 拓扑事件 
 * 
 * @author peihua
 * 
 */
public class Event extends BaseEventInvoke<Event> {

    public String routeId;

    private String groupName;

    private long startTime;

    private long endTime;

    private long span;

    // 客户端
    private String clientNodes;

    // 网关节点
    private String instanceId;

    // 下游服务
    private String forwardURL;

    private String path;

    private Throwable cause;

    @Override
    public void run(BaseEventInvoke<Event> e) {

        GatewayContextMgt.getTopoMgt().processEvent((Event) e);
    }

    @Override
    public EventType getEventType() {

        return EventType.CPU;
    }

    public Event(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteId() {

        return routeId;
    }

    public void setRouteId(String routeId) {

        this.routeId = routeId;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

    public long getStartTime() {

        return startTime;
    }

    public void setStartTime(long startTime) {

        this.startTime = startTime;
    }

    public long getEndTime() {

        return endTime;
    }

    public void setEndTime(long endTime) {

        this.endTime = endTime;
    }

    public long getSpan() {

        return span;
    }

    public void setSpan(long span) {

        this.span = span;
    }

    public String getInstanceId() {

        return instanceId;
    }

    public void setInstanceId(String instanceId) {

        this.instanceId = instanceId;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public String getClientNodes() {

        return clientNodes;
    }

    public void setClientNodes(String clientNodes) {

        this.clientNodes = clientNodes;
    }

    public String getForwardURL() {

        return forwardURL;
    }

    public void setForwardURL(String forwardURL) {

        this.forwardURL = forwardURL;
    }

    public Throwable getCause() {

        return cause;
    }

    public void setCause(Throwable cause) {

        this.cause = cause;
    }
}
