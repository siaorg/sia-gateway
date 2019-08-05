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


package com.creditease.gateway.ribbon;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

/**
 * Ribbon更新接口
 * 
 * @author peihua
 * 
 */

public interface RibbonUpdaterInterface {

    /**
     * @comment: attributes是serviceId，rule，list，Ribbon初始化更新接口
     * 
     **/
    public RibbonUpdaterInterface initliztion(String serviceId, IRule rule, ServerList<Server> list);

    /**
     * @comment: 更新RibbonServerList
     * 
     **/
    public void updateRibbonServerList();

    /**
     * @comment: 根据attributes是serviceId，rule，list，Ribbon更新接口
     * 
     **/
    public void updateRibbonServerList(String serviceId, IRule rule, ServerList<Server> list);
}
