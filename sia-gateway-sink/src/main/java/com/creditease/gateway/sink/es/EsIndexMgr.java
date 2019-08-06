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


package com.creditease.gateway.sink.es;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.creditease.gateway.elasticsearch.client.ESClient;
import com.creditease.gateway.message.appender.JsonTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: yongbiao
 * @Date: 2019/2/21 17:30
 */
public class EsIndexMgr {

    private static final Logger logger = LoggerFactory.getLogger(EsIndexMgr.class);

    private static final String NUMBER_OF_SHARDS = "index.number_of_shards";
    private static final String NUMBER_OF_REPLICAS = "index.number_of_replicas";

    @Autowired
    private ESClient client;

    private String indexTemplate;

    private String templateName;

    private String indexType;

    private String shards;

    private String replicas;

    private int order;

    public EsIndexMgr(String indexTemplate, String templateName, String indexType, String shards, String replicas,
                      int order) {

        this.indexTemplate = indexTemplate;
        this.templateName = templateName;
        this.indexType = indexType;
        this.shards = shards;
        this.replicas = replicas;
        this.order = order;
    }

    public String getIndexType() {

        return indexType;
    }

    @SuppressWarnings("unchecked")
    public void setIndexTemplate(String json) {

        Map<String, Map<String, Object>> mapping = JsonTransform.toObject((json), Map.class);
        if (mapping == null || mapping.isEmpty()) {
            logger.error("ES index mapping file does not exist or is not a json file, use the default mapping!");
        } else {
            setIndexTemplate(logFormatSetting(), mapping);
        }
    }

    /**
     * 设置索引模板，先删除原索引模板再创建
     */
    public void setIndexTemplate(Map<String, String> setting, Map<String, Map<String, Object>> mapping) {

        if (client.existIndexTemplate(templateName)) {
            boolean isDelete = client.deleteIndexTemplate(templateName);
            logger.info("Delete ES index template result:" + isDelete);
        }

        try {
            boolean isPut = client.putIndexTemplate(templateName, order, indexTemplate, indexType, setting, mapping,
                    null);

            logger.info("Put ES index template result:{}", isPut);
        } catch (IOException e) {
            logger.error("Put ES index template failed, name: " + templateName + ", template: " + indexTemplate, e);
        }
    }

    public Map<String, String> logFormatSetting() {

        Map<String, String> setting = new HashMap<>(2);
        setting.put(NUMBER_OF_SHARDS, shards);
        setting.put(NUMBER_OF_REPLICAS, replicas);

        return setting;
    }

    /***
     * 日志 es mapping
     */
    public Map<String, Map<String, Object>> logFormatMapping() {

        // 不设置分词器字段
        Map<String, Object> keywordFields = new LinkedHashMap<>(11);
        keywordFields.put("type", "keyword");

        // 设置分词器字段
        Map<String, Object> analyzedFields = new HashMap<>(8);
        analyzedFields.put("type", "text");
        analyzedFields.put("analyzer", "standard");

        // 时间字段多类型设置
        Map<String, Object> timestamp = new HashMap<>(8);
        Map<String, Object> timefields = new HashMap<>(8);
        Map<String, Object> longType = new HashMap<>(8);

        timestamp.put("type", "date");
        timestamp.put("fields", timefields);
        timefields.put("long", longType);
        longType.put("type", "long");

        // 设置Mapping对应fields
        Map<String, Map<String, Object>> mapping = new HashMap<>(8);
        mapping.put("appname", keywordFields);
        mapping.put("instanceid", keywordFields);
        mapping.put("logtime", timestamp);
        mapping.put("threadname", keywordFields);
        mapping.put("loggername", keywordFields);
        mapping.put("level", keywordFields);
        mapping.put("msg", analyzedFields);

        //请求、响应日志组件字段
        mapping.put("routeid", keywordFields);
        mapping.put("uri", keywordFields);
        mapping.put("parameter", analyzedFields);
        mapping.put("body", analyzedFields);

        return mapping;
    }
}
