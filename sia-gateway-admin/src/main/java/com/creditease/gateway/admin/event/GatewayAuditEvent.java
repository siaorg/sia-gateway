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


package com.creditease.gateway.admin.event;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.creditease.gateway.admin.service.AuditService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.reactive.impl.BaseEventInvoke;

/**
 * @description: 操作审计信息落库Event
 * @author: guohuixie2
 * @create: 2019-06-17 16:00
 **/

@Component
@Scope(SCOPE_PROTOTYPE)
public class GatewayAuditEvent extends BaseEventInvoke<GatewayAuditEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(GatewayAuditEvent.class);

    @Autowired
    private AuditService auditService;

    @Override
    public void run(@SuppressWarnings("rawtypes") BaseEventInvoke event) {

        try {
            auditService.recordAuditInfo((GatewayAuditEvent) event);
        }
        catch (Exception e) {
            LOGGER.error("GatewayAuditTask操作审计={},落库失败！", JSON.toJSONString(event), e);
            new GatewayException(ExceptionType.AdminException, e);
        }
    }

    @Override
    public EventType getEventType() {

        return EventType.CPU;
    }

    /**
     * 自增id
     */
    private int id;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 网关组
     */
    private String zuulGroupName;

    /**
     * 请求路径
     */
    private String url;

    /**
     * ip地址
     */
    private String ip;
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private long timeLoss;

    /**
     * 执行描述（1:执行成功、2:执行失败）
     */
    private int status;

    /**
     * 请求参数
     */
    private String params;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public String getZuulGroupName() {

        return zuulGroupName;
    }

    public void setZuulGroupName(String zuulGroupName) {

        this.zuulGroupName = zuulGroupName;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {

        this.ip = ip;
    }

    public Date getStartTime() {

        return startTime;
    }

    public void setStartTime(Date startTime) {

        this.startTime = startTime;
    }

    public long getTimeLoss() {

        return timeLoss;
    }

    public void setTimeLoss(long timeLoss) {

        this.timeLoss = timeLoss;
    }

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {

        this.status = status;
    }

    public String getParams() {

        return params;
    }

    public void setParams(String params) {

        this.params = params;
    }
}
