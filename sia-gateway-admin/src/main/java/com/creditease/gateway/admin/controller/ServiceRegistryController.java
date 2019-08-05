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


package com.creditease.gateway.admin.controller;

import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.EurekaInfo;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.repository.EurekaRepository;
import com.creditease.gateway.service.EurekaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 注册中心管理
 * 
 * @Author: yongbiao
 * @Date: 2019-06-26 15:40
 */
@Controller
public class ServiceRegistryController {

    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryController.class);

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private EurekaRepository repository;

    @Autowired
    private EurekaService eurekaService;

    @RequestMapping(value = "/getEurekaUrls", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getEurekaUrls(@RequestParam("zuulGroupName") String zuulGroupName) {

        logger.info("getEurekaUrls, zuulGroupName:[{}]", zuulGroupName);

        List<String> list = eurekaService.getCurrentEurekaUrls(zuulGroupName);

        return JsonHelper.toString(new Message(list));
    }

    @RequestMapping(value = "/getAllServiceStatus", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getAllServiceStatus(@RequestParam("zuulGroupName") String zuulGroupName) {

        logger.info("getAllServiceStatus, zuulGroupName:{}", zuulGroupName);

        Map<String, Map<String, String>> result;

        EurekaInfo info = repository.getEurekaInfo(zuulGroupName);
        if (info == null) {
            result = discoveryService.getServiceStatus();
        } else {
            result = discoveryService.getServiceStatusV2(info.getEurekaUrls());
        }

        return JsonHelper.toString(new Message(result));
    }

    @RequestMapping(value = "/setEurekaUrls", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String setEurekaUrls(@RequestParam("zuulGroupName") String zuulGroupName, @RequestParam("urls") String urls) {

        logger.info("setEurekaUrls, zuulGroupName:[{}], urls:{}", zuulGroupName, urls);

        return JsonHelper.toString(eurekaService.setZuulEurekaUrls(zuulGroupName, urls));
    }

    @RequestMapping(value = "/resetEurekaUrls", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String resetEurekaUrls(@RequestParam("zuulGroupName") String zuulGroupName) {

        logger.info("resetEurekaUrls, zuulGroupName:[{}]", zuulGroupName);

        return JsonHelper.toString(eurekaService.resetZuulEurekaUrls(zuulGroupName));
    }
}
