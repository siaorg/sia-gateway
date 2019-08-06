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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.tomcat.util.modeler.Registry;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;

/**
 * @Author: yongbiao
 * @Date: 2019-03-12 19:07
 */
@RestController
public class ZuulCoreController {

    private static Logger logger = LoggerFactory.getLogger(ZuulCoreController.class);

    @Value("${server.port:8080}")
    private String port;

    @Value("${zuul.version:1.0}")
    private String version;

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/getversion", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getVersion() {

        try {
            logger.info("The zuul version is :{}", version);
            Message msg = new Message(version, Message.ResponseCode.SUCCESS_CODE.getCode());

            return new ObjectMapper().writeValueAsString(msg);
        }
        catch (IOException e) {
            new GatewayException(ExceptionType.CoreException, e);
            return null;
        }
    }

    @RequestMapping(value = "/updateServerList", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String updateServerList() {

        try {
            logger.info("The zuul version is :{}", version);

            try {
                Method method = DiscoveryClient.class.getDeclaredMethod("refreshRegistry");
                method.setAccessible(true);

                method.invoke(client, null);
            }
            catch (Exception e) {
                logger.error("Failed to refreshRegistry.", e);
            }
            return new ObjectMapper().writeValueAsString("success refresh");
        }
        catch (IOException e) {
            new GatewayException(ExceptionType.CoreException, e);

            return null;
        }
    }

    @RequestMapping(value = "/keepAliveCount", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public Object getKeepAliveCount() throws Exception {

        MBeanServer server = Registry.getRegistry(null, null).getMBeanServer();
        ObjectName threadObjName = new ObjectName("Tomcat:type=ThreadPool,name=\"http-nio-" + port + "\"");

        Map<String, Object> result = new HashMap<>(2);
        result.put("keepAliveCount", server.getAttribute(threadObjName, "keepAliveCount"));
        result.put("maxConnections", server.getAttribute(threadObjName, "maxConnections"));

        return JsonHelper.toString(result);

    }

}
