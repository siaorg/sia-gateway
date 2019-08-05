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

import com.creditease.gateway.service.EurekaService;
import com.creditease.gateway.spi.BaseAdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: yongbiao
 * @Date: 2019-07-02 14:25
 */
@Controller
public class ConfigController extends BaseAdminController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private EurekaService eurekaService;

    @RequestMapping(value = "/setEurekaUrls", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public boolean setEurekaUrls() {

        boolean result = eurekaService.setLocalEurekaUrls();

        logger.info("setEurekaUrls:{}", result);

        return result;
    }

    @RequestMapping(value = "/resetEurekaUrls", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public boolean resetEurekaUrl() {

        eurekaService.resetLocalEurekaUrls();

        logger.info("resetEurekaUrls");

        return true;
    }
}
