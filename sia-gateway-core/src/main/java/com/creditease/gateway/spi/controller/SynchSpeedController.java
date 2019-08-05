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


package com.creditease.gateway.spi.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.constant.SynchSpeedProtocol;
import com.creditease.gateway.domain.GatewayListMsg;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.synch.processor.UpstreamProcessor;

/**
 * @description: 刷新core 节点 eurekaList
 * @author: guohuixie2
 * @modify: peihua
 * @create: 2019-04-11 15:41
 **/


@RestController
public class SynchSpeedController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UpstreamProcessor upstreamprocessor;

    @PostMapping(value = SagProtocol.GATEWAYREFRESH)
    public Message sagOptUpstream(@RequestBody GatewayListMsg m){
    	
    	Map<String, List<String>> map = m.getServiceidMapInstanceMap();
        
        String opt =  m.getOpt();
        
        if(opt.equals(SynchSpeedProtocol.OFFLINEINTERFACE))
        {
        	 return upstreamprocessor.setoffLineEvent(map);
        	 
        }else if(opt.equals(SynchSpeedProtocol.ONLINEINTERFACE))
        {
        	logger.info("> not support RT onLine operation yet");
        }else
        {
        	logger.info("> no such operation");
        }
        
        String msg = CollectionUtils.isEmpty(map) ? "list is null " : map.toString();
        logger.info("enter sagOptEurekaRefresh..." + msg);
        return null;
       
    }
}
