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


package com.creditease.gateway.cache;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.creditease.gateway.domain.UpstreamObj;
import com.google.common.collect.Maps;

/**
 * 
 * UpstreamCacheManager：管理后端ServiceID节点状态
 * 
 * @author peihua
 *
 */
@Component
public class UpstreamCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamCacheManager.class);

    /**
     * 下游服务信息缓存
     */
    public Map<Long, UpstreamObj> upstreamCache = Maps.newConcurrentMap();

    public UpstreamCacheManager() {
        LOGGER.info("> UpstreamCacheManager start >");
    }

    public Map<Long, UpstreamObj> getUpstreamCache() {

        return upstreamCache;
    }

    public void setUpstreamCache(Map<Long, UpstreamObj> upstreamCache) {

        this.upstreamCache = upstreamCache;
    }

}
