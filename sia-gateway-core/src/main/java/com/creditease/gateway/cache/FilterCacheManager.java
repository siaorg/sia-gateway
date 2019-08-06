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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.localcache.LocalCacheManager;
import com.creditease.gateway.redis.RedisUtil;

/**
 * 
 * FilterCacheManager：目前主要缓存黑白名单信息
 * 
 * 
 * Redis存儲 <--------->緩存映射关系 | | Regin+Key <--------->ZUULGROUPNAMEVALUE+key
 * 
 * @author peihua
 *
 */
@Component
public class FilterCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterCacheManager.class);

    @Value("${spring.application.name}")
    private String zuulGroupNameValue;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LocalCacheManager cmg;

    /**
     * 初始化黑白名单緩存
     * 
     * @param routeid
     * @return
     */
    public void initbwListCache() {

        redisUtil.setStringSerializer();

        /**
         * 黑白名单用ZUULGroupName作为KEY值
         */

        Map<Object, Object> hmRs = redisUtil.hmget(zuulGroupNameValue);

        hmRs.forEach((k, v) -> {
            BwfilterObj obj = JsonHelper.toObject(v.toString(), BwfilterObj.class);

            cmg.enableL1Cache(zuulGroupNameValue + k.toString());
            cmg.put(zuulGroupNameValue + k.toString(), obj);

            LOGGER.info("FilterCacheManager同步 zgroupName:{} key:{},value:{}", zuulGroupNameValue, k.toString(), v);

        });

        LOGGER.info(">>>>> FilterCacheManager初始化完成");

    }

    /**
     * 根据serviceId获取obj
     * 
     * @param routeid
     * @return
     */
    public BwfilterObj getRouteId(String routeid) {

        BwfilterObj filterInfo = (BwfilterObj) cmg.get(zuulGroupNameValue + routeid);

        return filterInfo;
    }

    /**
     * 更新L1cache，根据routeid
     * 
     * @param routeid
     * @param obj
     */
    public boolean updateServiceId(BwfilterObj obj) {

        try {

            /**
             * WBList从HTTP接口而不是Redis拿，以免同步并发冲突问题
             * 
             * Object hgRs = redisUtil.hget(ZUULGROUPNAMEVALUE, routeid); BWFilterObj obj =
             * JSONHelper.toObject(hgRs.toString(), BWFilterObj.class);
             **/
            String routeid = obj.getRouteid();

            cmg.enableL1Cache(zuulGroupNameValue + routeid);
            cmg.put(zuulGroupNameValue + routeid, obj);

        }
        catch (Exception e) {

            LOGGER.error(">updateServiceId is error:{}", e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 删除L1cache，根据routeid
     *
     * @param routeid
     * @param obj
     */
    public boolean deleteServiceId(BwfilterObj obj) {

        try {

            String routeid = obj.getRouteid();
            cmg.del(zuulGroupNameValue + routeid);

        }
        catch (Exception e) {

            LOGGER.error(">deleteServiceId is error:{}", e.getMessage());
            return false;
        }

        return true;
    }

}
