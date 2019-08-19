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


package com.creditease.gateway.sink;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.creditease.gateway.elasticsearch.index.ESIndexHelper;
import com.creditease.gateway.message.appender.JsonTransform;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.elasticsearch.client.ESClient;
import com.creditease.gateway.sink.abs.BaseDataCollector;
import com.creditease.gateway.sink.es.EsIndexMgr;

/**
 * @Author: yongbiao
 * @Date: 2019/2/26 10:45
 */
public class LogDataCollector extends BaseDataCollector {

    private static final Logger logger = LoggerFactory.getLogger(LogDataCollector.class);

    private String indexPrefix;

    @Autowired
    private ESClient client;

    @Autowired
    private EsIndexMgr logIndexMgr;

    private ThreadPoolExecutor executor;

    @Override
    public void sinkToSource(Map<String, List<String>> dataMap) {

        for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
            List<Map<String, Object>> list = new LinkedList<>();

            for (String json : entry.getValue()) {
                Map map = jsonToMap(entry.getKey(), json);
                if (map != null) {
                    list.add(map);
                }
            }

            insertToEs(entry.getKey(), list);
        }
    }

    @Override
    public void sinkToSource(String index, String json) {

        Map map = jsonToMap(index, json);
        if (map != null) {
            insertToEs(index, map);
        }
    }

    public void insertToEs(String index, List<Map<String, Object>> list) {

        index = ESIndexHelper.getFirstDayOfMonth(indexPrefix + index);

        BulkRequestBuilder bulkRequest = client.getClient().prepareBulk();

        for (Map<String, Object> map : list) {
            try {
                pushToBulkRequest(index, logIndexMgr.getIndexType(), map, bulkRequest);
            } catch (Exception e) {
                logger.error("insert ES exception!", e);
            }
        }

        executor.execute(new BulkRequestGet(list.size(), index, bulkRequest.execute()));
    }

    public void insertToEs(String index, Map<String, Object> map) {

        index = ESIndexHelper.getFirstDayOfMonth(indexPrefix + index);

        BulkRequestBuilder bulkRequest = client.getClient().prepareBulk();

        try {
            pushToBulkRequest(index, logIndexMgr.getIndexType(), map, bulkRequest);
        } catch (Exception e) {
            logger.error("insert ES exception!", e);
        }

        executor.execute(new BulkRequestGet(1, index, bulkRequest.execute()));
    }

    public String getIndexPrefix() {

        return indexPrefix;
    }

    public void setIndexPrefix(String indexPrefix) {

        this.indexPrefix = indexPrefix;
    }

    public void setExecutor(int nThreads, int blockingQueueSize) {

        this.executor = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue(blockingQueueSize), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void pushToBulkRequest(String index, String type, Map<String, Object> map, BulkRequestBuilder bulkRequest) {

        String uuid = encodeMD5(map.toString());
        IndexRequestBuilder irb = client.getClient().prepareIndex(index, type, uuid);
        irb.setSource(map);
        bulkRequest.add(irb);
    }

    private Map<String, Object> jsonToMap(String index, String json) {

        Map<String, Object> map;

        // 兼容原日志数据
        if (JsonTransform.validateMap(json)) {
            map = JsonTransform.toObject(json, Map.class);
            if (map == null) {
                logger.error("json can't transform to Map!");
                return null;
            }
        } else {
            map = new LinkedHashMap<>(2);
            map.put("logtime", System.currentTimeMillis());
            map.put("msg", json);
        }

        return map;
    }

    public static String encodeMD5(String str) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return str;
        }
    }

    public class BulkRequestGet implements Runnable {

        private int size;
        private String index;
        private ListenableActionFuture future;

        BulkRequestGet(int size, String index, ListenableActionFuture future) {

            this.size = size;
            this.index = index;
            this.future = future;
        }

        @Override
        public void run() {

            long startTime = System.currentTimeMillis();
            BulkResponse bulkResponse = (BulkResponse) future.actionGet();
            long cost = System.currentTimeMillis() - startTime;

            if (bulkResponse.hasFailures()) {
                logger.error("insert ES failed: {}", bulkResponse.buildFailureMessage());
            } else {
                logger.info("insert ES, index: {}, size: {}, queue: {}, cost: {}", index, size,
                        executor.getQueue().size(), cost);
            }
        }
    }
}