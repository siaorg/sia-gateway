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
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.AdminInfo;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.AlarmInfo;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

import io.swagger.annotations.ApiOperation;

/**
 * 网关Dashboard功能
 * 
 * @author peihua
 */

@RestController
public class AdminController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    AdminService adminService;

    @Autowired
    AuthInterceptor authservice;

    /**
     * 查詢累计調用次數
     * 
     */
    @ApiOperation(value = "getSumAPIaccess")
    @RequestMapping(value = "/getSumAPIaccess", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSumAPIaccess() {

        try {
            LOGGER.info("累计調用次數user:{}", authCheckor.getCurrentUser());
            long access = adminService.getSumAccess();
            Message adminMsg = new Message(access);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢调用趋势
     * 
     *
     */
    @GetMapping(value = "/getAPICountArray", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Message getAPICountArray() {

        LOGGER.info("调用趋势 user:{}", authCheckor.getCurrentUser());

        int opt = GatewayConstant.OptEnum.COUNTARRAY.value();

        return adminService.getAPIInvokeArray(opt);
    }

    /**
     * 查詢訪問健康指數
     * 
     */
    @GetMapping(value = "/getAPIHealthyArray", produces = "application/json;charset=UTF-8")
    public Message getAPIHealthyArray() {

        LOGGER.info("健康指數 user:{}", authCheckor.getCurrentUser());

        int opt = GatewayConstant.OptEnum.HEALTHARRAY.value();

        return adminService.getAPIInvokeArray(opt);
    }

    /**
     * 查詢注冊的Route个数
     *
     */
    @RequestMapping(value = "/getRegisterRouteNo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getRegisterRouteNo() {

        try {
            LOGGER.info("注冊的Route个数user:{}", authCheckor.getCurrentUser());
            int r = adminService.getRegisterRouteNo();
            ObjectMapper mapper = new ObjectMapper();
            Message adminMsg = new Message(r);
            return mapper.writeValueAsString(adminMsg);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢预警個數
     *
     */
    @RequestMapping(value = "/getgwAlarmCount", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getgwAlarmCount() throws IOException {

        try {
            LOGGER.info("查詢预警個數 user:{}", authCheckor.getCurrentUser());
            Integer count = adminService.getAlarmCount();
            ObjectMapper mapper = new ObjectMapper();
            Message adminMsg = new Message(count.intValue());
            return mapper.writeValueAsString(adminMsg);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢预警列表
     *
     */
    @RequestMapping(value = "/getgwAlarmList", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getgwAlarmList() {

        try {
            LOGGER.info("预警列表user:{}", authCheckor.getCurrentUser());
            List<AlarmInfo> list = adminService.getAlarmList();
            ObjectMapper mapper = new ObjectMapper();
            Message adminMsg = new Message(list);
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢所有节点信息接口
     *
     */
    @RequestMapping(value = "/queryZuulListInfo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryZuulListInfo() {

        try {
            LOGGER.info("节点信息user:{}", authCheckor.getCurrentUser());
            List<ZuulInfo> list = adminService.getZuulList();
            ObjectMapper mapper = new ObjectMapper();

            Message adminMsg = new Message(list);
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());

            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查看网关详情
     *
     * @param pinfo
     * @return
     */
    @RequestMapping(value = "/queryZuul", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryZuulDetail(@RequestBody String jsonString) {

        try {
            LOGGER.info("网关详情 user:{}", authCheckor.getCurrentUser());

            @SuppressWarnings("unchecked")
            Map<String, String> map = JsonHelper.toObject(jsonString, Map.class);

            String instanceID = map.get("zuulInstanceId");
            ZuulInfo info = adminService.queryZuulDetail(instanceID);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(info);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error(e.getMessage());

            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 删除ZUUL信息
     *
     * @param pinfo
     * @return
     */
    @RequestMapping(value = "/deleteZuul", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteyZuul(@RequestBody String jsonString) {

        try {
            LOGGER.info("删除ZUUL信息user:{}", authCheckor.getCurrentUser());
            @SuppressWarnings("unchecked")
            Map<String, String> map = JsonHelper.toObject(jsonString, Map.class);
            String instanceID = map.get("zuulInstanceId");

            int info = adminService.deleteZuul(instanceID);
            ObjectMapper mapper = new ObjectMapper();

            Message response = new Message(info);
            return mapper.writeValueAsString(response);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢Admin节点信息接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/queryAdminListInfo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryZuulAdminInfo() throws IOException {

        try {
            LOGGER.info("查詢Admin节点信息接口 user:{}", authCheckor.getCurrentUser());
            List<AdminInfo> list = adminService.getAdminListStatus();

            ObjectMapper mapper = new ObjectMapper();
            Message adminMsg = new Message(list);
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {
            LOGGER.error(">>> Exception:" + e.getCause());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 删除ZUUL信息
     *
     * @param pinfo
     * @return
     */
    @RequestMapping(value = "/deletAdmin", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deletAdmin(@RequestBody String jsonString) {

        try {
            LOGGER.info("删除ZUUL信息user:{}", authCheckor.getCurrentUser());

            @SuppressWarnings("unchecked")
            Map<String, String> map = JsonHelper.toObject(jsonString, Map.class);

            String adminInstanceid = map.get("adminInstanceid");
            int info = adminService.deleteZuulAdmin(adminInstanceid);

            ObjectMapper mapper = new ObjectMapper();
            Message response = new Message(info);
            return mapper.writeValueAsString(response);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查看网关有效路由
     *
     * @param pinfo
     * @return
     */
    @RequestMapping(value = "/validrouteCheck", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String validrouteCheck(@RequestBody Map<String, String> request) {

        try {
            LOGGER.info("查看网关有效路由 user:{}", authCheckor.getCurrentUser());
            String zuulInstanceId = request.get("zuulInstanceId");

            String rst = adminService.getValidRoutebyID(zuulInstanceId);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(rst);

        }
        catch (Exception e) {
            LOGGER.error(">>> Exception:" + e.getCause());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    @RequestMapping(value = "/getZuulGgroupName", produces = "application/json;charset=UTF-8")
    public String getZuulGroupName() {

        try {
            String zuulGroupName = authservice.getZuulGroupName();
            Message adminMsg = new Message(zuulGroupName);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    @RequestMapping(value = "/updateZuulDescByGroupName", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public String updateZuulDescByGroupName(@RequestBody String jsonString) {

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> map = JsonHelper.toObject(jsonString, Map.class);
            int result = adminService.updateZuulDescByGroupName(map);
            return returnErrorMsg("成功修改记录" + result + "条", ResponseCode.SUCCESS_CODE);
        }
        catch (Exception e) {
            LOGGER.error("exception updateZuulDescByGroupName...", e);
            new GatewayException(ExceptionType.AdminException, e);
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    @GetMapping("/getMonitorUrl")
    public Message getMonitorUrl(@RequestParam String hostInfo) {

        return adminService.getMonitorUrl(hostInfo);
    }
}
