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


package com.creditease.gateway.ribbon.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.creditease.gateway.ribbon.RibbonUpdaterInterface;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

/**
 * @author peihua
 * 
 *         RibbonUpdaterEtcd
 */

@Service
public class RibbonUpdaterEtcd implements RibbonUpdaterInterface {

    public final static Logger logger = LoggerFactory.getLogger(RibbonUpdaterEtcd.class);

    private String serviceId;

    public String getServiceId() {

        return serviceId;
    }

    public void setServiceId(String serviceId) {

        this.serviceId = serviceId;
    }

    public IRule getRule() {

        return rule;
    }

    public void setRule(IRule rule) {

        this.rule = rule;
    }

    public ServerList<Server> getList() {

        return list;
    }

    public void setList(ServerList<Server> list) {

        this.list = list;
    }

    private IRule rule;

    private ServerList<Server> list;

    public RibbonUpdaterEtcd() {

        logger.info("> RibbonUpdaterEtcd init > ");
    }

    @Override
    public void updateRibbonServerList() {

        // todo
    }

    @Override
    public void updateRibbonServerList(String serviceId, IRule rule, ServerList list) {

        // todo
    }

    @Override
    public RibbonUpdaterInterface initliztion(String serviceId, IRule rule, ServerList<Server> list) {

        this.serviceId = serviceId;
        this.rule = rule;
        this.list = list;

        return this;
    }

}
