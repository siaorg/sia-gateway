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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SynchSpeedProtocol;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.service.TriggerEvent;

/**
 * @description: 加速后端服务（upstream）状态变化感知接口
 * @author: guohuixie2
 * @create: 2019-04-11 11:36
 **/

@RestController
public class SynchSpeedController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TriggerEvent triggerEventService;

    @GetMapping(value = SynchSpeedProtocol.OFFLINEINTERFACE)
    public Message offlineService(@RequestParam String ip) {

        logger.info("enter offlineService;ip:[{}]", ip);

        if (StringHelper.isEmpty(ip)) {
            return Message.buildInValidParamResult();
        }
        return triggerEventService.offline(ip);
    }

}
