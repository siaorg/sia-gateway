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

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.vo.AlarmEmailVO;

/**
 * 用于Monitor的DB寫入
 *
 * @author peihua
 **/

@Repository
public class AlarmRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmRepository.class);

    private static final String INSERTALARMOBJ = "INSERT INTO gateway_alarm (zuulInstance,alarmCreateTime,alarmInfomation,zuulGroupName) values(?,?,?,?)";

    private static final String QUERYALARMSETTING = "select alarmEmailAddr from gateway_setting  where zuulGroupName ='%s'";

    private static final int MAXLENGTH = 3000;

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate jdbcTemplate;

    public boolean reportAlarm(AlarmEmailVO alarmEmai) {

        try {
            String alarmInfo = alarmEmai.getContent();
            String appName = alarmEmai.getApplicationName();
            String instance = alarmEmai.getInstance();

            if (alarmInfo.length() > MAXLENGTH) {
                alarmInfo = alarmInfo.substring(0, MAXLENGTH);
            }

            jdbcTemplate.update(INSERTALARMOBJ, instance, new Timestamp(System.currentTimeMillis()), alarmInfo,
                    appName);
            LOGGER.info("预警落库成功, groupName:{}, instance:{}", appName, instance);

            return true;
        }
        catch (Exception e) {
            LOGGER.error("reportAlarm 预警落库失败，原因：{}", e.getCause());

            return false;
        }
    }

    public String getEmailAddr(String groupName) {

        String setting = null;

        try {
            String querySQL = StringHelper.format(QUERYALARMSETTING, groupName);

            LOGGER.info("getEmailAddr querySQL:{}", querySQL);

            setting = jdbcTemplate.queryForObject(querySQL, String.class);
        }
        catch (EmptyResultDataAccessException e) {
            LOGGER.error("getEmailAddr groupName:{} EMAIL Setting is Null!", groupName);

            return null;
        }
        catch (Exception e) {
            LOGGER.error("getEmailAddr groupName:{} EMAIL Setting  occur exception!", groupName, e);
        }

        return setting;
    }
}
