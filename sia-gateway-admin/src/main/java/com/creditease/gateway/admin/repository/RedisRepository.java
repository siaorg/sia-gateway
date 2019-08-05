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


package com.creditease.gateway.admin.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.admin.repository.base.BaseAdminRepository;
import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;

/**
 * Redis黑白名單訪問
 *
 * @author peihua
 */
@Repository
public class RedisRepository extends BaseAdminRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDbRepository.class);

    public Object queryWhiteList(String zuulGroupName, String routeid) throws Exception {

        redisUtil.setStringSerializer();

        if (StringHelper.isEmpty(zuulGroupName) || StringHelper.isEmpty(routeid)) {
            throw new RuntimeException("groupId:" + zuulGroupName + ",routeid:" + routeid);
        }

        return redisUtil.hget(zuulGroupName, routeid);
    }

    public boolean addWBList2Route(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        redisUtil.setStringSerializer();

        if (StringHelper.isEmpty(zuulGroupName) || StringHelper.isEmpty(routeid)) {
            throw new RuntimeException("groupId:" + zuulGroupName + ",routeid:" + routeid);
        }

        return this.updateRedisHset(zuulGroupName, routeid, fo);
    }

    public boolean updateWBList2Route(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        redisUtil.setStringSerializer();

        if (StringHelper.isEmpty(zuulGroupName) || StringHelper.isEmpty(routeid)) {
            throw new RuntimeException("groupId:" + zuulGroupName + ",routeid:" + routeid);
        }
        Object obj = redisUtil.hget(zuulGroupName, routeid);

        if (obj != null) {
            return this.updateRedisHset(zuulGroupName, routeid, fo);
        }

        throw new RuntimeException("groupId:" + zuulGroupName + ",routeid:" + routeid + ",redis不存在对应value");
    }

    /**
     * @param zuulGroupName
     * @param routeid
     * @param fo
     * @return 更新redis 并同步 gateway L1cache
     */
    private boolean updateRedisHset(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        try {
            redisUtil.setStringSerializer();

            boolean result = redisUtil.hset(zuulGroupName, routeid, JsonHelper.toString(fo));

            LOGGER.info("Admin updateRedisHset rst: " + result);

        }
        catch (Exception e) {
            LOGGER.error("admin同步gateway L1Cache 异常", e);

            LOGGER.error(">updateRedisHset Exception:" + e.getCause());
            return false;
        }
        return true;
    }

    public boolean deleteRedis(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        redisUtil.setStringSerializer();

        redisUtil.hdel(zuulGroupName, routeid, JsonHelper.toString(fo));

        return true;
    }
}
