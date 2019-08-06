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

import com.creditease.gateway.message.Message;

/**
 * @description: 刷新eureka 逻辑处理interface
 * @author: guohuixie2
 * @modify: peihua
 * @create: 2019-04-11 14:29
 **/

public interface TriggerEvent {

    /**
     * offline
     * 
     * @param ip
     * @return message
     */
    Message offline(String ip);

    /**
     * TODO：onLine
     * 
     * @param serviceId
     * @return message
     */
    Message online(String serviceId);
}
