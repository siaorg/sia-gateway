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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.SettingService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;

/**
 * URL统计管理
 * 
 * @author peihua
 */

@RestController
public class URLRecordController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(URLRecordController.class);

    @Autowired
    SettingService settingService;

    @Autowired
    private DiscoveryService zuuldisc;

    @Value("${spring.application.name}")
    private String appName;

    private static final String APIGATEWAYSERVICEPOST = "-GATEWAY-SERVICE";

    @Autowired
    ZuulHandler handler;

    @RequestMapping(value = "/getURLRecord", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getGatewayTopo(@RequestBody Map<String, String> request) {

        String groupName = request.get("groupName");

        String result = null;

        if (StringHelper.isEmpty(groupName)) {
            LOGGER.info("getGatewayTopo groupName is null!");
            return null;
        }

        String apiGatewayService = appName.substring(0, appName.indexOf("-")) + APIGATEWAYSERVICEPOST;

        try {

            LOGGER.info("apiGatewayService:{}", apiGatewayService);

            List<String> ipports = zuuldisc.getServiceList(apiGatewayService);

            if (null == ipports || ipports.size() < 0) {
                LOGGER.error(">ipports is empty!<");
            }

            for (String ipport : ipports) {

                try {
                    Map<String, String> parmeter = new HashMap<String, String>(8);

                    parmeter.put(GatewayConstant.GWGROUPNAME, groupName);
                    Message msg = new Message(parmeter);

                    /**
                     * step2: 请求URLRecord
                     */
                    String url = "http://" + ipport + "/getURLView";
                    result = handler.executeHttpCmd(url, msg);
                    break;
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    continue;
                }

            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

}
