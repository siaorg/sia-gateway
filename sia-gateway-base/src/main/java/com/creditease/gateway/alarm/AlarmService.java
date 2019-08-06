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


package com.creditease.gateway.alarm;

import java.util.HashMap;
import java.util.Map;

import com.creditease.gateway.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 * 预警服务
 *
 * @author peihua
 */
public class AlarmService {

    public static final Logger LOGGER = LoggerFactory.getLogger(AlarmService.class);

    private static final String CONNECTION_EXCEPTION = "org.springframework.dao.RecoverableDataAccessException";

    private static final String SUBJECT = "智能路由网关预警";

    @Value("${eureka.instance.instance-id}")
    private String instance;

    @Value("${spring.application.name}")
    private String zuulGroupName;

    @Value("${ALARM_EMAIL_ADDRESS:http://127.0.0.1:8070/alarmEmail/sendAlarmEmail}")
    private String alarmEmailAddress;

    @Autowired
    private RestTemplate restTemplate;

    public void reportAlarm(String alarmInfo) {
        this.reportAlarm(alarmInfo, null);
    }

    public void reportAlarm(String alarmInfo, String primaryKey) {

        LOGGER.info("> 触发预警服务，预警信息：" + alarmInfo);

        if (primaryKey.contains(CONNECTION_EXCEPTION)) {
            LOGGER.error("拦截到异常：" + primaryKey);
            return;
        }
        if (!StringHelper.isEmpty(primaryKey)) {
            try {
                Map<String, Object> requestBody = new HashMap<>(16);
                requestBody.put("subject", SUBJECT);
                requestBody.put("content", alarmInfo);
                requestBody.put("primary", primaryKey);
                requestBody.put("applicationName", zuulGroupName);
                requestBody.put("instance", instance);
                requestBody.put("flag", false);

                restTemplate.postForEntity(alarmEmailAddress, requestBody, String.class);

            } catch (Exception e) {
                LOGGER.error(">exception occur in AlarmService.reportAlarm....", e);
            }
        }
    }

    public void setZuulGroupName(String zuulGroupName) {
        this.zuulGroupName = zuulGroupName;
    }

}
