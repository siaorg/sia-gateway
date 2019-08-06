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

import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.ZuulHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: yongbiao
 * @Date: 2019-06-21 18:57
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private ZuulHandler handler;

    @RequestMapping(value = "/logfile", produces = "text/plain;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getZuulLogFile(@RequestParam("ipport") String ipPort) {

        if (StringHelper.isEmpty(ipPort)) {
            logger.warn("getZuulLogfile, ipport is empty!");
            return null;
        }

        logger.info("getZuulLogfile, ipport:{}", ipPort);

        return handler.executeHttpCmd("http://" + ipPort + "/logfile");
    }
}
