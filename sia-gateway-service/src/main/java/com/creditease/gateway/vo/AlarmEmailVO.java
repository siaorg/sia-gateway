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


package com.creditease.gateway.vo;

import java.util.List;

/**
 * @description: 网关预警邮件bean
 * @author: guohuixie2
 * @create: 2019-02-20 10:44
 **/

public class AlarmEmailVO {

    /**
     * 邮件主体
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 唯一主键
     */
    private String primary;

    /**
     * 邮件源应用名称
     */
    private String applicationName;

    private String instance;

    /**
     * 是否为定时任务健康监测
     */
    private Boolean flag;

    /**
     * 收件人邮箱
     * 
     * @return List
     */
    private List<String> mailto;

    public List<String> getMailto() {

        return mailto;
    }

    public void setMailto(List<String> mailto) {

        this.mailto = mailto;
    }

    public String getApplicationName() {

        return applicationName;
    }

    public void setApplicationName(String applicationName) {

        this.applicationName = applicationName;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {

        this.subject = subject;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public String getPrimary() {

        return primary;
    }

    public void setPrimary(String primary) {

        this.primary = primary;
    }

    public Boolean getFlag() {

        return flag;
    }

    public void setFlag(Boolean flag) {

        this.flag = flag;
    }

    public String getInstance() {

        return instance;
    }

    public void setInstance(String instance) {

        this.instance = instance;
    }

    @Override
    public String toString() {

        return "AlarmEmailVO{" + "subject='" + subject + '\'' + ", content='" + content + '\'' + ", primary='" + primary
                + '\'' + ", applicationName='" + applicationName + '\'' + ", instance='" + instance + '\'' + ", flag="
                + flag + '}';
    }
}
