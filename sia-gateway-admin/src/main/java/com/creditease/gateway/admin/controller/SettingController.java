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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.SettingService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.message.ZuulHandler;

/**
 * 网关设置管理
 * 
 * @author peihua
 */

@RestController
public class SettingController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

    public static final String INDEX_PREFIX = "sag-";

    public static final String SEPARATOR = "/";

    @Autowired
    SettingService settingService;

    @Value("${spring.kibana.url}")
    private String kibanaUrl;

    @Value("${spring.kibana.url.create}")
    private String kibanaCreateUrl;

    private static final String KIBANAVERSION = "5.5.2";

    private static final int MILLIESECONDS = 500;

    @Autowired
    ZuulHandler handler;

    /**
     * 设置预警邮箱
     */
    @RequestMapping(value = "/saveAlarmEmailSetting", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String saveAlarmEmailSetting(@RequestBody Map<String, String> request) throws IOException {

        try {
            LOGGER.info(">alarmEmailSetting by user:{}", authCheckor.getCurrentUser());

            LOGGER.info(">alarmEmailSetting by ZuulGroupName:{}", authCheckor.getZuulGroupName());

            String emailAddresss = request.get("emailAddress");

            String zuulGroup = authCheckor.getZuulGroupName();

            boolean rst = settingService.saveAlarmSetting(zuulGroup, emailAddresss);

            Message resp = new Message(rst);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    @RequestMapping(value = "/getAlarmEmailSetting", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAlarmEmailSetting() throws IOException {

        try {

            LOGGER.info(">alarmEmailSetting by ZuulGroupName:{}", authCheckor.getZuulGroupName());
            String zuulGroup = authCheckor.getZuulGroupName();
            String setting = settingService.getAlarmSetting(zuulGroup);

            Message resp = new Message(setting);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());

            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }

    }

    /***
     *
     * This method only support ELK:5.5.2版本
     *
     **/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping(value = "/getLogIndexMap", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getLogIndexMap(@RequestBody Map<String, String> request) throws IOException {

        try {
            String indexId = request.get("indexId");

            if (StringHelper.isEmpty(indexId)) {
                LOGGER.info(">>>indexId is error:" + indexId);
                return null;
            }

            LOGGER.info(">getLogIndexId by user:" + authCheckor.getCurrentUser() + ", ZuulGroupName:"
                    + authCheckor.getZuulGroupName());

            String result = handler.executeHttpCmd(kibanaUrl);

            Map<String, List> res = JsonHelper.toObject(result, Map.class);

            List<Map> sobjs = (List<Map>) res.get("saved_objects");

            String indexWithPrefix = INDEX_PREFIX + indexId.toLowerCase() + "*";

            for (Map obj : sobjs) {
                String id = (String) obj.get("id");
                if (indexWithPrefix.equals(id)) {
                    Message resp = new Message(id);
                    ObjectMapper mapper = new ObjectMapper();

                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);
                }
            }

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("kbn-version", KIBANAVERSION);

            Map<String, String> requestBody = new HashMap<>(3);
            requestBody.put("title", indexWithPrefix);
            requestBody.put("timeFieldName", "logtime");
            requestBody.put("notExpandable", "true");

            String postrst = handler.executePostEntity(kibanaCreateUrl + indexWithPrefix + "/_create", requestHeaders,
                    requestBody);

            LOGGER.info(">getLogIndexMap zuulGroup not exists: " + postrst);

            Map<String, Object> resultCreate = JsonHelper.toObject(postrst, Map.class);
            String id = (String) resultCreate.get("_id");

            // 给kibana跳转查询es留时间
            Thread.sleep(MILLIESECONDS);

            Message resp = new Message(id);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resp);

        }
        catch (Exception e) {

            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());

            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    @RequestMapping(value = "/loglevel", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String zuulLoglevel(@RequestBody Map<String, String> request) {

        String ipport = request.get("ipport");
        String path = request.get("path");

        if (StringHelper.isEmpty(ipport) || StringHelper.isEmpty(path)) {
            LOGGER.info("Parameter of ipport or path is null!");
            return null;
        }

        if (path.lastIndexOf(SEPARATOR) != path.indexOf(SEPARATOR)) {
            return handler.executeHttpCmd("http://" + ipport + "/setlevel" + path);
        }

        return handler.executeHttpCmd("http://" + ipport + "/getlevel" + path);
    }

    @RequestMapping(value = "/getversion", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getZuulVersion(@RequestBody Map<String, String> request) {

        String ipport = request.get("ipport");
        if (StringHelper.isEmpty(ipport)) {
            LOGGER.info("Parameter of ipport is null!");

            return null;
        }

        return handler.executeHttpCmd("http://" + ipport + "/getversion");
    }
}
