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


package com.creditease.gateway.service.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CounterInfo;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.helper.StringHelper;

/**
 * 用于zuul的DB注册
 * 
 * @author peihua
 **/

@Repository
public class SchedulerRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerRepository.class);

    private static final String INSERT_COUNTER = "INSERT INTO gateway_counter (zuulGroupName, zuulInstance, counterKey, counterValue, datetime) values(?,?,?,?,?)";
    private static final String QUERY_ZUUL_LIST = "select * from gateway_info ";
    private static final String SELECT_ROUTEID_BIND_TO_STATISTIC = "SELECT routeidList FROM gateway_component where compFilterName='StatisticFilter' ";
    private static final String UPDATE_GATEWAY_STATUS_BY_ID = " update gateway_info  set zuulStatus = '%s' where zuulInstanceId = '%s' ";
    private static final String UPDATE_GATEWAY_STATUS = " update gateway_info  set zuulStatus = '%s' ";

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate gatewayJdbcTemplate;

    public boolean insertCounter(CounterInfo counter, String zuulGroupName) {

        LOGGER.info("insert CounterKey:{} ,groupName:{}, instance:{}", counter.getCouterKey(), zuulGroupName, counter.getZuulInstance());

        gatewayJdbcTemplate.update(INSERT_COUNTER, zuulGroupName, counter.getZuulInstance(), counter.getCouterKey(),
                counter.getCounterValue(), counter.getDatetime());

        return true;
    }

    /**
     * 获取全部 zuul节点
     *
     * @return
     */
    public List<ZuulInfo> queryZuulList() throws Exception {

        return gatewayJdbcTemplate.query(QUERY_ZUUL_LIST, new BeanPropertyRowMapper<>(ZuulInfo.class));
    }

    /**
     * 获取绑定到统计组件的路由ID
     *
     * @return
     */
    public String queryRouteIdList() throws Exception {

        return gatewayJdbcTemplate.queryForObject(SELECT_ROUTEID_BIND_TO_STATISTIC, String.class);
    }

    /**
     * 根据instance-id更新网关的状态
     */
    public void updateZuulInfoStatusByID(String instanceId, String status) {

        try {
            String updateSql = StringHelper.format(UPDATE_GATEWAY_STATUS_BY_ID, status, instanceId);
            gatewayJdbcTemplate.update(updateSql);
        } catch (Exception e) {
            LOGGER.error("网关系统-更新网关状态失败, 请检查!", e);
        }
    }

    /**
     * 将网关状态全部更新为RUNNING
     */
    public void updateZuulInfoStatus(String status) {

        try {
            String updateSql = StringHelper.format(UPDATE_GATEWAY_STATUS, status);
            gatewayJdbcTemplate.update(updateSql);
        } catch (Exception e) {
            LOGGER.error("全量更新网关状态失败,请检查!", e);
        }
    }
}
