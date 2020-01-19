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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.GatewayConstant.LinkState;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.domain.topo.Link;
import com.creditease.gateway.domain.topo.RouteTopo;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.scheduler.TopoLogyScheduledTask;

/**
 * @description: 网关拓扑查询
 * 
 * @author peihua
 **/
@RestController
public class TopoViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopoViewController.class);

    @Autowired
    TopoLogyScheduledTask task;

    private static final String STATUS = "ok";

    @Autowired
    private DiscoveryService zuuldisc;

    private Date pre5MinDate;

    @RequestMapping(value = "/getGatewayTopo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String getGatewayTopo(@RequestBody Message msg) {

        try {

            // 得到5分钟前时间
            pre5MinDate = new Date(System.currentTimeMillis());
            pre5MinDate.setTime(pre5MinDate.getTime() - 10 * 60 * 1000);

            LOGGER.info("getGatewayTopo msg>{}", msg.toString());

            RouteTopo gatewayTopo = new RouteTopo();

            String groupName = msg.getRequest().get(GatewayConstant.GWGROUPNAME);

            LOGGER.info("getGatewayTopo groupName>{}", groupName);

            Map<String, Map<String, RouteTopo>> topo = task.getTopoAll();
            Map<String, RouteTopo> allRouteTopo = topo.get(groupName);

            if (allRouteTopo == null) {
                LOGGER.info("> allRouteTopo is null!");
                return null;
            }

            for (Object value : allRouteTopo.values().toArray()) {
                try {
                    RouteTopo topoValue = (RouteTopo) value;

                    // 合并节点
                    gatewayTopo.getClientNodes().addAll(topoValue.getClientNodes());
                    gatewayTopo.getZuulNodes().addAll(topoValue.getZuulNodes());
                    gatewayTopo.getUpstreamNodes().addAll(topoValue.getUpstreamNodes());

                    // 合并Link
                    Set<Link> linklist = topoValue.getLink();
                    for (Link l : linklist) {
                        gatewayTopo.addLink(l);
                    }

                    // 合并异常信息
                    Map expt = topoValue.getExceptionCause();
                    if (null != expt) {
                        gatewayTopo.getExceptionCause().putAll(expt);
                    }

                    if (null != gatewayTopo) {

                        LOGGER.info("gatewayTopo:{} ", gatewayTopo.toString());

                        Set<Link> links = gatewayTopo.getLink();
                        if (null != links && links.size() > 0) {
                            for (Link l : links) {
                                LinkState state = getLinkState(gatewayTopo, l.getLastRequestTime());
                                l.setState(state);
                            }
                        }

                    }
                }
                catch (Exception e) {

                    LOGGER.error(">Exception:{}", e);
                    continue;
                }
            }

            Message returnMsg = new Message(gatewayTopo, Message.ResponseCode.SUCCESS_CODE.getCode());
            LOGGER.info("returnMsg ok >{}", returnMsg.toString());
            return new ObjectMapper().writeValueAsString(returnMsg);

        }
        catch (Exception e) {

            e.printStackTrace();
            LOGGER.error("> e:{}", e);
        }
        return null;
    }

    @RequestMapping(value = "/getRouteTopo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    @ResponseBody
    public String getRouteTopo(@RequestBody Message msg)
            throws JsonGenerationException, JsonMappingException, IOException {

        try {

            // 得到5分钟前时间
            pre5MinDate = new Date(System.currentTimeMillis());
            pre5MinDate.setTime(pre5MinDate.getTime() - 10 * 60 * 1000);

            LOGGER.info("msg>{}", msg.toString());

            String groupName = msg.getRequest().get(GatewayConstant.GWGROUPNAME);
            String routeId = msg.getRequest().get(GatewayConstant.ROUTENAME);

            LOGGER.info("Service groupName>{}", groupName);
            LOGGER.info("Service routeId>{}", routeId);

            triggerRouteTopo(groupName);

            Map<String, Map<String, RouteTopo>> topo = task.getTopoAll();
            Map<String, RouteTopo> routeTopo = topo.get(groupName);

            LOGGER.info("routeTopo size>{}", routeTopo.size());

            if (null != routeTopo) {
                RouteTopo routeview = routeTopo.get(routeId);

                if (null != routeview) {

                    LOGGER.debug("routeview:{} ", routeview.toString());
                    Set<Link> links = routeview.getLink();

                    if (null != links && links.size() > 0) {
                        for (Link l : links) {
                            LinkState state = getLinkState(routeview, l.getLastRequestTime());
                            l.setState(state);
                        }
                    }
                    LOGGER.info("routeview: >{}", routeview.toString());
                }

                Message returnMsg = new Message(routeview, Message.ResponseCode.SUCCESS_CODE.getCode());

                return new ObjectMapper().writeValueAsString(returnMsg);
            }
        }
        catch (Exception e) {

            e.printStackTrace();
            LOGGER.error("> e:{}", e);
            Message returnError = new Message("error", Message.ResponseCode.SERVER_ERROR_CODE.getCode());
            LOGGER.info(">returnMsg error :{}", e);
            return new ObjectMapper().writeValueAsString(returnError);
        }
        return null;

    }

    public void triggerRouteTopo(String groupName) {

        try {
            List<String> zuulListEureka = zuuldisc.getServiceList(groupName);
            List<ZuulInfo> list = new ArrayList<ZuulInfo>();
            for (String ipport : zuulListEureka) {
                ZuulInfo zinfo = new ZuulInfo();
                zinfo.setZuulStatus(STATUS);
                zinfo.setZuulInstanceId(ipport);

                list.add(zinfo);
            }
            task.statisticByZuulList(list);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public LinkState getLinkState(RouteTopo topoDest, long lastTimeStamp) {

        LinkState state;

        if (0 == lastTimeStamp || StringHelper.isEmpty(String.valueOf(lastTimeStamp))) {
            state = LinkState.GRAY;
            return state;
        }
        Date lastDate = new Date(lastTimeStamp);

        Map<String, String> exception = topoDest.getExceptionCause();

        if (lastDate.getTime() < pre5MinDate.getTime()) {
            state = LinkState.GRAY;
        }
        else {
            if (null != exception.get("" + lastTimeStamp)) {
                state = LinkState.RED;
            }
            else {
                state = LinkState.GREEN;
            }
        }

        LOGGER.debug(">>LinkState state:" + state);

        return state;
    }
}
