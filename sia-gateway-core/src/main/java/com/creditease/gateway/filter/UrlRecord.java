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


package com.creditease.gateway.filter;

import com.creditease.gateway.reactive.impl.BaseEventInvoke;
import com.creditease.gateway.register.GatewayContextMgt;
import com.creditease.gateway.topology.Event;

/**
 * URL管理对象
 * 
 * @author peihua
 * 
 */
public class UrlRecord extends BaseEventInvoke<UrlRecord> {

    private String urlPath;

    private String instanceId;

    private String groupName;

    private String routeid;

    private long span;

    private long lastInvokeTime;

    public UrlRecord(Event t) {
        urlPath = t.getPath();
        routeid = t.getRouteId();
        span = t.getSpan();
        instanceId = t.getInstanceId();
        lastInvokeTime = t.getEndTime();
        groupName = t.getGroupName();
    }

    public UrlRecord(String gn, String ist, String rid, String path, long sp, long lasttime) {
        urlPath = path;
        routeid = rid;
        span = sp;
        instanceId = ist;
        lastInvokeTime = lasttime;
        groupName = gn;
    }

    @Override
    public void run(BaseEventInvoke<UrlRecord> e) {

        GatewayContextMgt.getUrlAnalysis().processEvent((UrlRecord) e);
    }

    @Override
    public EventType getEventType() {

        return EventType.CPU;
    }

    public String getUrlPath() {

        return urlPath;
    }

    public void setUrlPath(String urlPath) {

        this.urlPath = urlPath;
    }

    public String getInstanceId() {

        return instanceId;
    }

    public void setInstanceId(String instanceId) {

        this.instanceId = instanceId;
    }

    public String getRouteid() {

        return routeid;
    }

    public void setRouteid(String routeid) {

        this.routeid = routeid;
    }

    public long getSpan() {

        return span;
    }

    public void setSpan(long span) {

        this.span = span;
    }

    public long getLastInvokeTime() {

        return lastInvokeTime;
    }

    public void setLastInvokeTime(long lastInvokeTime) {

        this.lastInvokeTime = lastInvokeTime;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

}
