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


package com.creditease.gateway.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 网关admin审计持久化实体类
 * @author: guohuixie2
 * @create: 2019-06-04 10:53
 **/

public class GatewayAuditObj implements Serializable {

    private static final long serialVersionUID = 2364653005593880743L;
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
