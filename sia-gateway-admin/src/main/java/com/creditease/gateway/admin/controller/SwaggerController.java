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

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;

/**
 * Swagger管理
 * 
 * @author peihua
 */

@RestController
public class SwaggerController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(SwaggerController.class);

    public static final String INDEX_PREFIX = "sag-";

    private static final String PREFIX = "http://";

    private static final String POSTFILX = "/swagger-ui.html";

    private static final String FLAG = "Swagger UI";
    @Autowired
    AdminService adminService;

    @Autowired
    ZuulHandler handler;

    @RequestMapping(value = "/getSwaggerUrl", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSwaggerUrl() {

        String zuulInfo = null;
        List<ZuulInfo> list;
        try {
            list = adminService.getZuulList();

            if (null == list || list.size() < 0) {
                LOGGER.error(">list is null");
                return null;
            }
            String url = null;
            for (ZuulInfo zinfo : list) {

                if ("Running".equals(zinfo.getZuulStatus())) {
                    zuulInfo = zinfo.getZuulInstanceId();

                    url = zuulInfo + POSTFILX;
                    try {
                        LOGGER.info(">getSwaggerUrl url :{}", url);

                        String rst = handler.executeHttpCmd(url);

                        if (rst.indexOf(FLAG) > 0) {
                            break;
                        }
                        else {
                            LOGGER.info(">getSwaggerUrl rst :{}", rst);
                            continue;
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(">core executeAsynHttpCmderror:{}", e.getMessage());
                        continue;
                    }
                }
                else {
                    LOGGER.error(">core status is error:{}", zinfo.getZuulInstanceId());
                    continue;
                }
            }

            Message resp = new Message(url);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
        }
        catch (Exception e) {

            LOGGER.error(">getSwaggerUrl Exceptionis {}", e);
        }
        return null;

    }
}
