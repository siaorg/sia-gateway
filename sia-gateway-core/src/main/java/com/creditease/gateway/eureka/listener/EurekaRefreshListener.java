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


package com.creditease.gateway.eureka.listener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.creditease.gateway.cache.UpstreamCacheManager;
import com.creditease.gateway.domain.UpstreamObj;
import com.creditease.gateway.eureka.EurekaHandler;
import com.creditease.gateway.ribbon.updater.RibbonUpdater;
import com.google.common.collect.Lists;
import com.netflix.discovery.CacheRefreshedEvent;
import com.netflix.discovery.EurekaEvent;
import com.netflix.discovery.EurekaEventListener;

/**
 * Eureka回调监听
 * 
 * @Author: guohuixie2
 * @Date: 2019-04-24 17:19
 */
@Component
public class EurekaRefreshListener implements EurekaEventListener {

    private Logger logger = LoggerFactory.getLogger(EurekaRefreshListener.class);

    @Autowired
    private EurekaHandler eurekahandler;

    @Autowired
    private RibbonUpdater ribbonupdater;

    @Autowired
    private UpstreamCacheManager upstreamcache;

    @Override
    public void onEvent(EurekaEvent event) {

        synchronized (this) {
            try {
                logger.debug("ENTER [EurekaRefreshListener.onEvent] downCache:[{}]",
                        JSON.toJSONString(upstreamcache.getUpstreamCache()));
                if (event instanceof CacheRefreshedEvent) {
                    for (Iterator<Map.Entry<Long, UpstreamObj>> it = upstreamcache.getUpstreamCache().entrySet()
                            .iterator(); it.hasNext();) {
                        Map.Entry<Long, UpstreamObj> entry = it.next();
                        long timeStamp = entry.getKey();
                        long currentTimeStamp = System.currentTimeMillis();
                        if ((currentTimeStamp - timeStamp) / 1000 <= 30) {
                            UpstreamObj downServerObj = entry.getValue();
                            List<String> ls = Lists.newArrayList();
                            ls.add(downServerObj.getInstanceId());
                            logger.info("执行更新状态操作,downServerObj:[{}]", JSON.toJSONString(downServerObj));
                            boolean rst = eurekahandler.setOffLineStatus(downServerObj.getAppName(), ls, false);

                            if (rst) {
                                ribbonupdater.refreshRibbon(downServerObj.getAppName());
                            }

                            it.remove();
                        }
                        else {
                            it.remove();
                            UpstreamObj downServerObj = upstreamcache.getUpstreamCache().get(timeStamp);
                            logger.info("完成剔除操作，result:[{}],timeStamp:[{}]", JSON.toJSONString(downServerObj),
                                    timeStamp);
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error("EurekaRefreshListener execute Method [onEvent] FAIL...", e);
            }
        }
    }

}
