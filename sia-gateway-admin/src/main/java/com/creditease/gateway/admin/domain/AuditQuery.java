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


package com.creditease.gateway.admin.domain;

import java.util.Date;

import com.creditease.gateway.admin.domain.common.PageQuery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @description: 操作审计查询类
 * @author: guohuixie2
 * @create: 2019-06-05 15:19
 **/

@ApiModel
public class AuditQuery extends PageQuery {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作人邮箱")
    private String userName;

    @ApiModelProperty(value = "搜索开始时间")
    private Date startTime;

    @ApiModelProperty(value = "搜索结束时间")
    private Date endTime;

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public Date getStartTime() {

        return startTime;
    }

    public void setStartTime(Date startTime) {

        this.startTime = startTime;
    }

    public Date getEndTime() {

        return endTime;
    }

    public void setEndTime(Date endTime) {

        this.endTime = endTime;
    }
}
