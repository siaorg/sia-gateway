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


package com.creditease.gateway.repository;

import com.creditease.gateway.domain.EurekaInfo;
import com.creditease.gateway.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: yongbiao
 * @Date: 2019-07-08 13:41
 */
@Repository
public class EurekaRepository {

    private static final Logger logger = LoggerFactory.getLogger(EurekaRepository.class);

    private static final String QUERY_EUREKA_URLS = "select * from gateway_eureka where zuulGroupName = '%s' ";

    private static final String INSERT_EUREKA_URLS = "INSERT INTO gateway_eureka (zuulGroupName, eurekaUrls, enable) values(?,?,?)";

    private static final String UPDATE_EUREKA_URLS = "update gateway_eureka set eurekaUrls = ?, enable = ? where zuulGroupName = ? ";

    private static final String DELETE_EUREKA_URLS = "delete from gateway_eureka where zuulGroupName = '%s' ";

    private static final String QUERY_ZUUL_INSTANCES = "select zuulInstanceId from gateway_info where zuulGroupName = '%s' ";

    private static final String QUERY_ONLINE_ROUTE_COUNT = "select count(*) from gateway_route where zuulGroupName = '%s' and routeStatus = 'ONLINE' ";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public EurekaInfo getEurekaInfo(String zuulGroupName) {

        String querySql = StringHelper.format(QUERY_EUREKA_URLS, zuulGroupName);

        try {
            RowMapper<EurekaInfo> rm = BeanPropertyRowMapper.newInstance(EurekaInfo.class);
            return jdbcTemplate.queryForObject(querySql, rm);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int updateEurekaUrls(EurekaInfo eurekaInfo) {

        return jdbcTemplate.update(UPDATE_EUREKA_URLS, eurekaInfo.getEurekaUrls(), eurekaInfo.getEnable(), eurekaInfo.getZuulGroupName());
    }

    public int insertEurekaUrls(EurekaInfo eurekaInfo) {

        return jdbcTemplate.update(INSERT_EUREKA_URLS, eurekaInfo.getZuulGroupName(), eurekaInfo.getEurekaUrls(),
                eurekaInfo.getEnable());
    }

    public int deleteEurekaUrls(String zuulGroupName) {

        String deleteSql = StringHelper.format(DELETE_EUREKA_URLS, zuulGroupName);

        return jdbcTemplate.update(deleteSql);
    }

    public List<String> queryZuulInstances(String zuulGroupName) {

        String querySql = StringHelper.format(QUERY_ZUUL_INSTANCES, zuulGroupName);

        return jdbcTemplate.queryForList(querySql, String.class);
    }

    public int queryOnlineRouteCount(String zuulGroupName) {

        String querySql = StringHelper.format(QUERY_ONLINE_ROUTE_COUNT, zuulGroupName);

        return jdbcTemplate.queryForObject(querySql, Integer.class);
    }
}
