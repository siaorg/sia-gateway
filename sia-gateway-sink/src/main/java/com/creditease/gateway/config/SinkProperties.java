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


package com.creditease.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: yongbiao
 * @Date: 2019-06-04 18:13
 */
@ConfigurationProperties(prefix = "sink")
public class SinkProperties {

    private String esClusterName;

    private String esAddr;

    private String esIndexPrefix;

    private String esIndexTemplate;

    private String esTemplateName;

    private String esMappingFileName = "index-mapping.json";

    private String esIndexType = "stream_type";

    private String esIndexNumberOfShards = "5";

    private String esIndexNumberOfReplicas = "0";

    private int esTemplateOrder = 0;

    private int corePoolSize = 10;

    private int maximumPoolSize = 10;

    private int blockingQueueSize = 300;

    private boolean defaultRunnerEnable = false;

    public String getEsClusterName() {

        return esClusterName;
    }

    public void setEsClusterName(String esClusterName) {

        this.esClusterName = esClusterName;
    }

    public String getEsAddr() {

        return esAddr;
    }

    public void setEsAddr(String esAddr) {

        this.esAddr = esAddr;
    }

    public String getEsMappingFileName() {

        return esMappingFileName;
    }

    public void setEsMappingFileName(String esMappingFileName) {

        this.esMappingFileName = esMappingFileName;
    }

    public String getEsIndexType() {

        return esIndexType;
    }

    public String getEsIndexPrefix() {

        return esIndexPrefix;
    }

    public void setEsIndexPrefix(String esIndexPrefix) {

        this.esIndexPrefix = esIndexPrefix;
    }

    public String getEsIndexTemplate() {

        return esIndexTemplate != null ? esIndexTemplate : esIndexPrefix + "*";
    }

    public void setEsIndexTemplate(String esIndexTemplate) {

        this.esIndexTemplate = esIndexTemplate;
    }

    public String getEsTemplateName() {

        return esTemplateName;
    }

    public void setEsTemplateName(String esTemplateName) {

        this.esTemplateName = esTemplateName;
    }

    public String getEsIndexNumberOfShards() {

        return esIndexNumberOfShards;
    }

    public void setEsIndexNumberOfShards(String esIndexNumberOfShards) {

        this.esIndexNumberOfShards = esIndexNumberOfShards;
    }

    public String getEsIndexNumberOfReplicas() {

        return esIndexNumberOfReplicas;
    }

    public void setEsIndexNumberOfReplicas(String esIndexNumberOfReplicas) {

        this.esIndexNumberOfShards = esIndexNumberOfReplicas;
    }

    public int getEsTemplateOrder() {

        return esTemplateOrder;
    }

    public void setEsTemplateOrder(int esTemplateOrder) {

        this.esTemplateOrder = esTemplateOrder;
    }

    public int getCorePoolSize() {

        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {

        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {

        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {

        this.maximumPoolSize = maximumPoolSize;
    }

    public int getBlockingQueueSize() {

        return this.blockingQueueSize;
    }

    public void setBlockingQueueSize(int maximumPoolSize) {

        this.maximumPoolSize = maximumPoolSize;
    }

    public boolean getDefaultRunnerEnable() {

        return defaultRunnerEnable;
    }

    public void setDefaultRunnerEnable(boolean defaultRunnerEnable) {

        this.defaultRunnerEnable = defaultRunnerEnable;
    }

}
