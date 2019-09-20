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


package com.creditease.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.service.abstractlist.EmailAlarmPostImpl;
import com.creditease.gateway.vo.AlarmEmailVO;

/**
 * @description: 网关发送预警邮件接口
 * @author: guohuixie2
 * @create: 2019-02-20 10:28
 **/

@RestController
@RequestMapping("/alarmEmail")
public class SendAlarmEmailController {

    @Autowired
    EmailAlarmPostImpl emailAlarmPost;

    @RequestMapping(value = "/sendAlarmEmail", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public void sendAlarmEmail(@RequestBody AlarmEmailVO alarmEmailVO) {

        emailAlarmPost.post(alarmEmailVO);
    }
}
