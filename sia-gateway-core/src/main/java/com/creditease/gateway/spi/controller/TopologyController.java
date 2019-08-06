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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.domain.topo.RouteTopo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.topology.TopologyManager;

/**
 * @Author: peihua
 * 
 * 
 */
@RestController
public class TopologyController {

    private static Logger logger = LoggerFactory.getLogger(TopologyController.class);

    @Value("${spring.application.name}")
    private String sagGroupName;

    @Autowired
    private TopologyManager topoMgt;

    @RequestMapping(value = SagProtocol.GETTOPOLOGY, produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getTopology() {

        try {

            logger.debug("> start getTopology! >");

            Map<String, RouteTopo> map = topoMgt.getRouteTopoMap();

            Message msg = new Message(map, Message.ResponseCode.SUCCESS_CODE.getCode());
            if (null == msg.getRequest()) {
                Map<String, String> mapper = new HashMap<String, String>();
                mapper.put(GatewayConstant.GWGROUPNAME, sagGroupName);
                msg.setRequest(mapper);
            }
            return new ObjectMapper().writeValueAsString(msg);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

}
