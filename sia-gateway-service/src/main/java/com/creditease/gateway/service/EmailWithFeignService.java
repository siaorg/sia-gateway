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


package com.creditease.gateway.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description: email service
 * @author: guohuixie2
 * @create: 2019-02-22 10:45
 **/

//@FeignClient(name = "${ALARM_EMAIL_SERVICEID}")
public interface EmailWithFeignService {

    /**
     * 发送预警邮件
     * 
     * @param str
     * @return
     */
    @RequestMapping(value = "v1/sendEmail", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    ResponseEntity<String> sendAlarmEmail(@RequestBody String str);
}
