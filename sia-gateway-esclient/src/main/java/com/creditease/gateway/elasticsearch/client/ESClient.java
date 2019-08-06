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


package com.creditease.gateway.elasticsearch.client;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * ES客户端
 * 
 * @Author peihua
 * @Author yongbiao
 */
public class ESClient {

    private TransportClient client;

    public ESClient(String[] esAddrs, String clusterName) {

        init(esAddrs, clusterName);
    }

    public ESClient(String esAddrStr, String clusterName) {

        String[] esAddrs = esAddrStr.split(",");

        init(esAddrs, clusterName);
    }

    /**
     * init
     *
     * @param esAddrs
     * @param clusterName
     */
    private void init(String[] esAddrs, String clusterName) {

        Settings settings = Settings.EMPTY;

        if (clusterName != null && !clusterName.isEmpty()) {
            settings = Settings.builder().put("cluster.name", clusterName).build();
        }

        client = new PreBuiltTransportClient(settings);

        for (String esAddr : esAddrs) {
            String[] ipport = esAddr.split(":");
            client.addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress(ipport[0], Integer.parseInt(ipport[1]))));
        }
    }

    /**
     * 判断是否存在对应的索引模板
     *
     * @param templateName
     * @return
     */
    public boolean existIndexTemplate(String templateName) {

        List<IndexTemplateMetaData> templates = getIndexTemplate(templateName);
        for (IndexTemplateMetaData template : templates) {
            if (templateName.equals(template.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 创建索引模板，索引别名会用于每个索引，不能只用于最新索引
     *
     * @param templateName
     * @param order
     * @param indexTemplate
     * @param type
     *            如应用所有type则设为default
     * @param setting
     * @param mapping
     * @param alias
     *            如为空则不设置
     * @return
     * @throws IOException
     */
    public boolean putIndexTemplate(String templateName, int order, String indexTemplate, String type,
            Map<String, String> setting, Map<String, Map<String, Object>> mapping, String alias) throws IOException {

        PutIndexTemplateRequest request = new PutIndexTemplateRequest(templateName);

        request.order(order).template(indexTemplate).settings(createSetting(setting)).mapping(type,
                createMapping(type, mapping));

        if (alias != null && !alias.isEmpty()) {
            request.alias(new Alias(alias));
        }

        return client.admin().indices().putTemplate(request).actionGet().isAcknowledged();
    }

    /**
     * 获取索引模板
     *
     * @param templateName
     * @return
     */
    public List<IndexTemplateMetaData> getIndexTemplate(String templateName) {

        GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templateName);

        return client.admin().indices().getTemplates(request).actionGet().getIndexTemplates();
    }

    /**
     * 删除索引模板
     *
     * @param templateName
     * @return
     */
    public boolean deleteIndexTemplate(String templateName) {

        DeleteIndexTemplateRequest request = new DeleteIndexTemplateRequest(templateName);

        return client.admin().indices().deleteTemplate(request).actionGet().isAcknowledged();
    }

    /**
     * existIndex
     *
     * @param index
     * @return
     */
    public boolean existIndex(String index) {

        IndicesExistsRequest request = new IndicesExistsRequest(index);

        return client.admin().indices().exists(request).actionGet().isExists();
    }

    /**
     * creatIndex
     *
     * @param index
     * @return
     * @throws IOException
     */
    public boolean creatIndex(String index) throws IOException {

        return creatIndex(index, null, null, null);
    }

    public boolean creatIndex(String index, String type, Map<String, String> setting,
            Map<String, Map<String, Object>> mapping) throws IOException {

        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(index);
        if (type != null && mapping != null) {
            createIndexRequestBuilder.addMapping(type, createMapping(type, mapping));
        }
        if (setting != null) {
            createIndexRequestBuilder.setSettings(createSetting(setting));
        }

        return createIndexRequestBuilder.execute().actionGet().isAcknowledged();
    }

    private Settings createSetting(Map<String, String> setting) {

        return Settings.builder().put(setting).build();
    }

    private XContentBuilder createMapping(String type, Map<String, Map<String, Object>> properties) throws IOException {

        XContentBuilder mapping;

        mapping = jsonBuilder().startObject().startObject(type);

        mapping = mapping.startObject("properties");

        for (String key : properties.keySet()) {
            mapping = mapping.startObject(key);

            Map<String, Object> fv = properties.get(key);

            for (String field : fv.keySet()) {
                mapping = mapping.field(field, fv.get(field));
            }

            mapping = mapping.endObject();
        }

        mapping = mapping.endObject();

        mapping = mapping.endObject();

        mapping = mapping.endObject();

        return mapping;
    }

    /**
     * existType
     *
     * @param index
     * @param type
     * @return
     */
    public boolean existType(String index, String type) {

        TypesExistsRequest request = new TypesExistsRequest(new String[] { index }, type);

        return client.admin().indices().typesExists(request).actionGet().isExists();
    }

    /**
     * updateIndexSetting
     *
     * @param index
     * @param setting
     * @return
     */
    public boolean updateIndexSetting(String index, Map<String, Object> setting) {

        try {
            return client.admin().indices().prepareUpdateSettings(index).setSettings(setting).get().isAcknowledged();
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * setIndexTypeMapping require Index Creation first
     *
     * @param index
     * @param type
     * @param properties
     * @return
     * @throws IOException
     */
    public boolean setIndexTypeMapping(String index, String type, Map<String, Map<String, Object>> properties)
            throws IOException {

        PutMappingRequest pmp = Requests.putMappingRequest(index).type(type).source(createMapping(type, properties));

        return client.admin().indices().putMapping(pmp).actionGet().isAcknowledged();
    }

    /**
     * 添加索引别名
     *
     * @param index
     * @param alias
     * @return
     */
    public boolean addIndexAlias(String index, String alias) {

        try {
            return client.admin().indices().prepareAliases().addAlias(index, alias).get().isAcknowledged();
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除索引别名
     *
     * @param index
     * @param alias
     * @return
     */
    public boolean removeIndexAlias(String index, String alias) {

        try {
            return client.admin().indices().prepareAliases().removeAlias(index, alias).get().isAcknowledged();
        }
        catch (Exception e) {
            return false;
        }
    }

    public TransportClient getClient() {

        return this.client;
    }

    public void close() {

        if (this.client != null) {
            client.close();
        }
    }
}
