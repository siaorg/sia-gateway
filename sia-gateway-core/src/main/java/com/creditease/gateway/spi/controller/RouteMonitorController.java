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

import java.util.Calendar;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.DateTimeHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.spi.BaseAdminController;

import io.swagger.annotations.ApiOperation;

/**
 * 路由统计
 * 
 * @author peihua
 **/

@RestController
public class RouteMonitorController extends BaseAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteMonitorController.class);

    private String dateFormat = "yyyy-MM-dd HH";

    @ApiOperation("/sagOpt-gwstatitic / 网关統計服务")
    @RequestMapping(value = SagProtocol.GWSTATISTIC, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagAdminOptGWStatistic(@RequestBody Message msg) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            LOGGER.info(">开始网关统计>:{}", msg.getRequest());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

            String date = DateTimeHelper.dateFormat(calendar.getTime(), dateFormat);
            long gwSumCount = statisticservice.getCount(GatewayConstant.APISUMCOUNT + "-" + date);
            long gwFailCount = statisticservice.getCount(GatewayConstant.APIFAILCOUNT + "-" + date);
            String rst = "" + gwSumCount + "-" + gwFailCount;

            LOGGER.info("> 网关统计结果 >, routeID:{}", rst);

            return mapper.writeValueAsString(rst);

        }
        catch (Exception e) {

            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

    @ApiOperation("/sagOpt-routestatitic / 路由統計服务")
    @RequestMapping(value = SagProtocol.ROUTESTATISTIC, produces = "application/json;charset=UTF-8")
    @CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String sagAdminOptRouteStatistic(@RequestBody Message msg) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            String routeid = msg.getRequest().get("routeid");
            LOGGER.info("> 路由统计, routeID:" + routeid);

            long routeSumCount = statisticservice.getCount(routeid);
            long routeFailCount = statisticservice.getCount(GatewayConstant.APIFAILCOUNT + "-" + routeid);
            msg.setCode(ResponseCode.SUCCESS_CODE.getCode());

            String rst = "" + routeSumCount + "-" + routeFailCount;
            LOGGER.info("> 路由统计结果, routeID:" + rst);
            msg.setResponse(rst);
            return mapper.writeValueAsString(rst);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

}
