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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.CompAdapter;
import com.creditease.gateway.admin.service.CompTransferService;
import com.creditease.gateway.admin.service.ComponentService;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 网关组件管理
 * 
 * @author peihua
 */

@RestController
public class ComponentController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ComponentController.class);

    @Autowired
    ComponentService compService;

    @Autowired
    CompTransferService comptransfer;

    private static final String TAG = "公共组件";

    /**
     * 查詢网关组件列表
     * 
     */
    @RequestMapping(value = "/getCompList", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCompList() {

        try {
            List<CompInfo> comp = compService.getComplist();
            // trasfer
            List<CompAdapter> rest = comptransfer.transfer(comp);

            Message adminMsg = new Message(rest);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {

            e.printStackTrace();

            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error(">getCompList Excetion:{}", e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 查詢网关组件详情
     * 
     */
    @RequestMapping(value = "/getCompDetail", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCompDetail(@RequestBody CompInfo compInfo) {

        try {
            CompInfo info = compService.queryCompDetail(compInfo.getCompFilterName());

            Message adminMsg = new Message(info);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error(">getCompDetail Excetion:{}", e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }

    }

    /**
     * 组件绑定服务
     * 
     */
    @RequestMapping(value = "/bindRoute", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String bindRoute(@RequestBody List<String> request, @RequestParam() String compFilterName) {

        try {
            boolean rst = compService.compBindRoute(request, compFilterName);
            Message adminMsg = null;
            if (rst) {
                adminMsg = new Message(rst);
            }
            else {
                adminMsg = new Message(rst, ResponseCode.SERVER_ERROR_CODE.getCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error("bindRoute Excetion:{}", e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }

    }

    /**
     * 组件卸载服务
     * 
     */
    @RequestMapping(value = "/removeComponent", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String removeComponent(@RequestBody CompInfo compInfo) {

        try {
            String desc = compInfo.getCompdesc();
            int index = desc.indexOf(TAG);
            boolean rst = false;

            if (index < 0) {
                String filterName = compInfo.getCompFilterName();
                rst = compService.removeComponent(filterName);
            }
            else {
                LOGGER.info("> 删除公共组件 faied >");
            }

            Message msg = new Message(rst);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(msg);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            LOGGER.error("removeComponent Excetion:{}", e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 第三方组件查询
     */

    @RequestMapping(value = "/getAllBizCompList", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllBizCompList() {

        try {
            List<CompInfo> comp = compService.getBizComplist();

            Message adminMsg = new Message(comp);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(adminMsg);
        }
        catch (Exception e) {

            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error("getAllBizCompList Excetion:{}", e.getMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

}
