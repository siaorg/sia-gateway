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


package com.creditease.gateway.respository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.GatewayListDB;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.StringHelper;

/**
 * 
 * Synch管理功能
 * 
 * @author peihua
 */

@Repository
public class SynchDbRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchDbRepository.class);

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate adminJdbcTemplate;

    private static final String QUERYZUULGROUPNAMEBYSERVICEID = "SELECT  distinct zuulGroupName,serviceid FROM gateway_route WHERE upper(serviceid) = '%s'";

    private static final String QUERYCORELIST = "SELECT  distinct gateway_route.zuulGroupName, gateway_route.serviceid, gateway_info.zuulInstanceId from gateway_route , gateway_info"
            + " where gateway_route.zuulGroupName=gateway_info.zuulGroupName and gateway_info.zuulStatus='RUNNING' ";

    public List<Map<String, Object>> queryZuulGroupNameByServiceId(String serviceId) throws Exception {

        try {
            String querySQL = StringHelper.format(QUERYZUULGROUPNAMEBYSERVICEID, serviceId);

            return adminJdbcTemplate.queryForList(querySQL);

        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.SynchSpeedException, e);
            LOGGER.error("queryZuulGroupNameByServiceId occur exception....", e);
            throw e;
        }
    }

    public List<GatewayListDB> queryGWCoreList(Set<String> set) throws Exception {

        try {
            String querySQL = QUERYCORELIST + appendInSql(set);

            return adminJdbcTemplate.query(querySQL, new BeanPropertyRowMapper<>(GatewayListDB.class));

        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.SynchSpeedException, e);
            LOGGER.error("queryZuulGroupNameByServiceId occur exception....", e);
            throw e;
        }
    }

    public String appendInSql(Set<String> set) {

        StringBuffer buf = new StringBuffer();
        buf.append(" and serviceid in");
        buf.append("(");
        buf.append(getlist(set));
        buf.append(")");

        String rst = buf.toString();

        LOGGER.info(">>>>> appendInSql SQL :{}", rst);

        return rst;
    }

    public String getlist(Set<String> set) {

        StringBuffer buf = new StringBuffer();

        for (String item : set) {
            buf.append("'");
            buf.append(item);
            buf.append("'");
            buf.append(",");
        }

        String rst = buf.toString();

        if (!StringHelper.isEmpty(rst)) {
            rst = rst.substring(0, rst.lastIndexOf(","));
        }

        return rst;
    }
}
