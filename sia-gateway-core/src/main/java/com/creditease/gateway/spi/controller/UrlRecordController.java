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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.cache.LRUCache;
import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.domain.UrlRecordAggregate;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.service.impl.UrlAnalysisService;

/**
 * @Author: peihua
 * 
 * 
 */
@RestController
public class UrlRecordController {

    private static Logger logger = LoggerFactory.getLogger(UrlRecordController.class);

    @Value("${spring.application.name}")
    private String sagGroupName;

    @Autowired
    private UrlAnalysisService urlanalysisservice;

    @RequestMapping(value = SagProtocol.GETURLRECORDLIST, produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String sagOptgetUrlRecordList() {

        try {

            logger.debug("> start getUrlRecordList! >");

            LRUCache<String, UrlRecordAggregate> map = urlanalysisservice.getUrlAggregate();
            List<UrlRecordAggregate> list = new ArrayList<UrlRecordAggregate>();
            list.addAll(map.values());

            Message msg = new Message(list, Message.ResponseCode.SUCCESS_CODE.getCode());
            return new ObjectMapper().writeValueAsString(msg);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

}
