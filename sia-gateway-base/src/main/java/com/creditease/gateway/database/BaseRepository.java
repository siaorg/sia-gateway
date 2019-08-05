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


package com.creditease.gateway.database;

import com.creditease.gateway.domain.EurekaInfo;
import com.creditease.gateway.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: yongbiao
 * @Date: 2019-07-15 15:11
 */
@Service
@ConditionalOnClass(JdbcTemplate.class)
public class BaseRepository {

    private static final String QUERY_EUREKA_URLS = "select * from gateway_eureka where zuulGroupName = '%s' ";

    private static final String QUERY_ZUUL_GROUP_NAME = "select zuulGroupName from gateway_info where zuulInstanceId like '%s' ";

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

    public List<String> getZuulGroupName(String ip) {

        String querySql = StringHelper.format(QUERY_ZUUL_GROUP_NAME, ip + StringHelper.MAOHAO_SEPARATOR + "%");

        return jdbcTemplate.queryForList(querySql, String.class);
    }

}
