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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.UrlRecordAggregate;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.scheduler.URLMonitorScheduledTask;

/**
 * @description:URL统计管理
 * 
 * @author peihua
 **/
@RestController
public class UrlViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlViewController.class);

    @Autowired
    URLMonitorScheduledTask urlTask;

    @Autowired
    private DiscoveryService zuuldisc;

    private static final String STATUS = "ok";

    @Autowired
    URLMonitorScheduledTask urlm;

    @RequestMapping(value = "/getURLView", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String getURLView(@RequestBody Message msg) {

        try {
            LOGGER.info("msg:{}", msg.toString());

            List<UrlRecordAggregate> list = new ArrayList<UrlRecordAggregate>();
            String groupName = msg.getRequest().get(GatewayConstant.GWGROUPNAME);

            triggerUrlRecord(groupName);

            Map<String, Map<String, UrlRecordAggregate>> all = urlm.getUrlCacheGroup();
            Map<String, UrlRecordAggregate> urlagree = all.get(groupName);

            if (urlagree == null) {
                return null;
            }

            for (String url : urlagree.keySet()) {
                list.add(urlagree.get(url));
            }

            Collections.sort(list);
            Message returnMsg = new Message(list, Message.ResponseCode.SUCCESS_CODE.getCode());
            LOGGER.info("returnMsg>{}", list.toString());
            return new ObjectMapper().writeValueAsString(returnMsg);
        }
        catch (Exception e) {
            LOGGER.error("getURLView error cause:{}", e.getCause());
        }
        return null;
    }

    public String triggerUrlRecord(String groupName) {

        try {

            List<String> zuulListEureka = zuuldisc.getServiceList(groupName);
            List<ZuulInfo> list = new ArrayList<ZuulInfo>();
            for (String ipport : zuulListEureka) {
                ZuulInfo zinfo = new ZuulInfo();
                zinfo.setZuulStatus(STATUS);
                zinfo.setZuulInstanceId(ipport);

                list.add(zinfo);

            }
            urlTask.statisticCronTask(list);
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());

        }
        return null;
    }
}
