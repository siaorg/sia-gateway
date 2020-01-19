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


package com.creditease.gateway.service.abstractlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.service.EmailWithFeignService;
import com.creditease.gateway.service.repository.AlarmRepository;
import org.springframework.util.StringUtils;

/**
 * @description: 网关系统邮件发送实现类
 * @author: guohuixie2
 * @create: 2019-02-21 16:18
 **/

@Component
public class EmailAlarmPostImpl extends AbstractPostAlarmInfo {

    @Autowired
    AlarmRepository alarmrepo;

    //@Autowired
    EmailWithFeignService emailWithFeignService;

    @Override
    public String getEmailByZuulGroupName(String zuulGroupName) {

        try {
            // 通过网关组获取预警邮箱
            String alarmEmailAddress = alarmrepo.getEmailAddr(zuulGroupName);

            if (StringHelper.isEmpty(alarmEmailAddress)) {
                alarmEmailAddress = siaEmailAddress;
            } else {
                alarmEmailAddress = alarmEmailAddress + (StringUtils.isEmpty(siaEmailAddress) ? SPLIT_SYMBOL + siaEmailAddress : "");
            }
            return alarmEmailAddress;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    ResponseEntity<String> postAction(String str) throws Exception {

        try {
            return emailWithFeignService.sendAlarmEmail(str);
        } catch (Exception e) {
            LOGGER.error("邮件发送失败！", e);
            throw e;
        }
    }

}
