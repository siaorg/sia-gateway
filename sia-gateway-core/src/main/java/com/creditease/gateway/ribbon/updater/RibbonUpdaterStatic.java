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
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Service;

import com.creditease.gateway.ribbon.RibbonUpdaterInterface;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

/**
 * @author peihua 
 * 
 * RibbonLofServer
 * 
 */

@Service
public class RibbonUpdaterStatic implements RibbonUpdaterInterface {

    public final static Logger logger = LoggerFactory.getLogger(RibbonUpdaterStatic.class);

    private SpringClientFactory factory;

    private String serviceId;

    private IRule rule;

    private ServerList<Server> list;

    public RibbonUpdaterStatic() {

        logger.info("RibbonUpdaterLofServer init ");
    }

    @Override
    public RibbonUpdaterInterface initliztion(String serviceId, IRule rule, ServerList<Server> list) {

        this.serviceId = serviceId;
        this.rule = rule;
        this.list = list;
        return this;
    }

    @Override
    public void updateRibbonServerList() {

        updateRibbonServerList(serviceId, rule, list);

    }

    public RibbonUpdaterStatic setFactory(SpringClientFactory factory) {

        this.factory = factory;

        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void updateRibbonServerList(String serviceId, IRule rule, ServerList list) {

        logger.info(">updateRibbonServerList ,serviceId:{},list:{} >", serviceId, list.toString());

        ILoadBalancer balancer = factory.getLoadBalancer(serviceId);

        RibbonLoadBalancerContext context = factory.getLoadBalancerContext(serviceId);

        if (balancer instanceof DynamicServerListLoadBalancer) {

            logger.info("> updateRibbonServerList start >");

            balancer.addServers(list.getUpdatedListOfServers());

            ((DynamicServerListLoadBalancer) balancer).setRule(rule);

            ((DynamicServerListLoadBalancer) balancer).setServerListImpl(list);

            ((DynamicServerListLoadBalancer) balancer).updateListOfServers();

            context.setLoadBalancer(balancer);

        }
    }
}
