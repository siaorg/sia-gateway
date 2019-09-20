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


package com.creditease.gateway.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.UrlRecordAggregate;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.ZuulHandler;

/**
 * URL定时统计任务
 * 
 * @author peihua
 * 
 */

@Component
public class URLMonitorScheduledTask {

    @Autowired
    ZuulHandler handler;

    private static final Logger LOGGER = LoggerFactory.getLogger(URLMonitorScheduledTask.class);

    private Map<String, Map<String, UrlRecordAggregate>> urlCacheGroup = new HashMap<String, Map<String, UrlRecordAggregate>>();

    private Map<String, UrlRecordAggregate> urlRecordCache;

    public void statisticCronTask(List<ZuulInfo> zuulist) {

        LOGGER.info(">开始拓扑统计 time:{}" + new Date(System.currentTimeMillis()));
        urlRecordCache = new HashMap<String, UrlRecordAggregate>();

        for (ZuulInfo zinfo : zuulist) {

            String status = zinfo.getZuulStatus();
            String instatnce = zinfo.getZuulInstanceId();
            if (("Dead").equals(status)) {
                LOGGER.info(">ZuulInstanceId:{},zinfo status is :", zinfo.getZuulInstanceId(), status);
                continue;
            }

            String url = "http://" + instatnce;
            String rst = null;
            try {
                rst = handler.executeHttpGetCmd(url, GatewayConstant.ADMINOPTKEY.GURL.getValue());
                LOGGER.info(">getUrlRecordList rst:{}", rst);
            }
            catch (Exception e) {
                LOGGER.error(">>> get executeHttpCmd Exception:{}", e);
            }

            Map<String, Object> response = JsonHelper.toObject(rst, Map.class);
            if (null == response) {
                continue;
            }

            List<Map> responseValue = (List<Map>) response.get("response");
            if (null == responseValue || responseValue.size() <= 0) {
                continue;
            }

            for (Map aggre : responseValue) {

                UrlRecordAggregate src = new UrlRecordAggregate();
                transfer(src, aggre);
                UrlRecordAggregate dest = urlRecordCache.get(src.getUrl());

                if (null == dest) {
                    urlRecordCache.put(src.getUrl(), src);
                }
                else {
                    /**
                     * step1: 聚集不同实例的URL访问
                     */
                    dest.spanAggreCompute(src.getSumCount(), src.getFailedCount(), src.getSumSpan(), src.getMaxSpan(),
                            src.getMinSpan());
                }
            }
        }

        /**
         * step2: 重置CACHE
         */
        for (String url : urlRecordCache.keySet()) {
            UrlRecordAggregate src = urlRecordCache.get(url);

            String groupName = src.getGroupName();

            Map<String, UrlRecordAggregate> mapper = urlCacheGroup.get(groupName);

            if (null == mapper) {
                mapper = new HashMap<String, UrlRecordAggregate>();
                mapper.put(src.getUrl(), src);
                urlCacheGroup.put(src.getGroupName(), mapper);
            }
            else {
                UrlRecordAggregate urlCacheAggregate = mapper.get(src.getUrl());

                if (null == urlCacheAggregate) {
                    mapper.put(src.getUrl(), src);
                }
                else {
                    urlCacheAggregate.setSumCount(src.getSumCount());
                    urlCacheAggregate.setFailedCount(src.getFailedCount());
                    urlCacheAggregate.setSumSpan(src.getSumSpan());
                    urlCacheAggregate.setMaxSpan(src.getMaxSpan());
                    urlCacheAggregate.setMinSpan(src.getMinSpan());
                    urlCacheAggregate.setAvgSpan(src.getAvgSpan());
                }
            }
        }
    }

    public void transfer(UrlRecordAggregate src, @SuppressWarnings("rawtypes") Map aggre) {

        String urltmp = (String) aggre.get("url");
        String routeidtmp = (String) aggre.get("routeid");
        String groupNametmp = (String) aggre.get("groupName");
        long lastInvokeTime = (long) aggre.get("lastInvokeTime");
        int sumCount = (int) aggre.get("sumCount");
        int failedCount = (int) aggre.get("failedCount");
        long sumSpan = (int) aggre.get("sumSpan");
        long maxSpan = (int) aggre.get("maxSpan");
        long minSpan = (int) aggre.get("minSpan");
        long avgSpan = (int) aggre.get("avgSpan");

        src.init(sumCount, failedCount, urltmp, routeidtmp, groupNametmp, lastInvokeTime, sumSpan, maxSpan, minSpan,
                avgSpan);
    }

    public Map<String, Map<String, UrlRecordAggregate>> getUrlCacheGroup() {

        return urlCacheGroup;
    }
}
