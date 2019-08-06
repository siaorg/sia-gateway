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


package com.creditease.gateway.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.SynchSpeedProtocol;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.GatewayListDB;
import com.creditease.gateway.domain.GatewayListMsg;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.respository.SynchDbRepository;
import com.creditease.gateway.service.TriggerEvent;
import com.google.common.collect.ImmutableMap;

/**
 * @description: refresh eureka list
 * @author: guohuixie2
 * @modify: peihua
 * @create: 2019-04-11 14:34
 **/
@Service
public class TriggerEventImpl implements TriggerEvent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscoveryService zuulDiscovery;

    @Autowired
    private SynchDbRepository dbRepository;

    @Autowired
    private ZuulHandler handler;

    @Value("${EUREKA_SERVER_NAME}")
    private String eurekaName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message offline(String ip) {

        try {
            logger.info("eurekaName : [{}]", eurekaName);

            /**
             * Step1:请求eureka-server 刷新状态
             */
            Map<String, Boolean> result = refreshEurekaServer(ip, null, SynchSpeedProtocol.OFFLINEINTERFACE);
            if (CollectionUtils.isEmpty(result)) {
                return Message.buildExceptionResult();
            }

            /**
             * step2:获得受影响的网关列表
             */
            Map<String, GatewayListMsg> map = getGWCoreList(SynchSpeedProtocol.OFFLINEINTERFACE, ip, result);

            /**
             * step3:网关下发消息
             */
            notifyGateway(map, SynchSpeedProtocol.OFFLINEINTERFACE);

        } catch (Exception e) {
            logger.error("refreshEurekaList fail...", e);
            return Message.buildExceptionResult();
        }
        return Message.buildSuccessResult();
    }

    public Map<String, Boolean> refreshEurekaServer(String ip, String serviceID, String protocal) {

        try {
            List<String> eurekaNameList = zuulDiscovery.getServiceList(eurekaName);

            String eurekaAddress = eurekaNameList.get(0);
            String url = "http://" + eurekaAddress + protocal + "?ip=" + ip;
            logger.info(">>>> eureka-server刷新地址为：" + url);

            String eurekaResult = handler.executeHttpCmd(url);
            logger.info("刷新eureka-server list remoteCall rst:{}", eurekaResult);
            @SuppressWarnings("unchecked")
            Map<String, Boolean> result = JsonHelper.toObject(eurekaResult, Map.class);

            return result;

        } catch (Exception e) {
            logger.error("refreshEurekaServer  exception:", e);
        }

        return null;
    }

    /**
     * ServiceMapInstanceIdList: { LONG-PATH-TEST=[10.143.135.136:8095], DEV-GATEWAY-CORE=[10.143.135.136:8082] }
     */
    public Map<String, GatewayListMsg> getGWCoreList(String opt, String ip, Map<String, Boolean> result)
            throws Exception {

        Map<String, GatewayListMsg> rst = new HashMap<String, GatewayListMsg>();

        Map<String, List<String>> serviceMapInstanceIdList = getInfluceService(ip, result);

        Set<String> serviceList = serviceMapInstanceIdList.keySet();

        List<GatewayListDB> list = dbRepository.queryGWCoreList(serviceList);

        if (!CollectionUtils.isEmpty(list)) {
            for (GatewayListDB db : list) {

                String zuulInstanceId = db.getZuulInstanceId();

                String serviceId = db.getServiceId();

                List<String> instList = serviceMapInstanceIdList.get(serviceId.toUpperCase());

                Map<String, List<String>> dest = ImmutableMap.of(serviceId, instList);

                GatewayListMsg object = rst.get(zuulInstanceId);

                if (null == object) {
                    GatewayListMsg msg = new GatewayListMsg().init(db.getZuulGroupName(), dest, opt);
                    rst.put(zuulInstanceId, msg);

                } else {
                    object.getServiceidMapInstanceMap().putAll(dest);
                }
            }
            return rst;
        }

        return null;

    }

    public void notifyGateway(Map<String, GatewayListMsg> map, String protocal) {

        try {

            if (null == map || map.size() < 1) {
                logger.error("notifyGateway：map is null");
                return;
            }
            Set<String> keyset = map.keySet();
            for (String key : keyset) {
                String hostPort = key;

                GatewayListMsg m = map.get(key);

                String zuulName = m.getZuulGroupName();

                List<String> zuulListEureka = zuulDiscovery.getServiceList(zuulName);

                if (!zuulListEureka.contains(hostPort)) {
                    logger.info(">>>>> InstanceId：" + hostPort + " is not running");
                    continue;
                }

                String eurekaUrl = "http://" + hostPort + "/" + GatewayConstant.ADMINOPTKEY.GWR.getValue();
                logger.info("刷新地址为：{}", eurekaUrl);

                String msg = handler.executeAsynHttpCmd(eurekaUrl, m, null);

                logger.info("刷新core eureka list remoteCall rst:{}", msg);
            }
        } catch (Exception e) {

            logger.error("notifyGateway exception:", e);
        }
    }

    /**
     * key: serviceId value: influceInstanceId
     */
    public Map<String, List<String>> getInfluceService(String ip, Map<String, Boolean> result) {

        Map<String, List<String>> serviceIdList = new LinkedHashMap<>();
        Map<String, List<String>> appMap = zuulDiscovery.getServiceListByIp(ip);

        for (String serviceId : appMap.keySet()) {
            for (String instanceId : appMap.get(serviceId)) {
                if (result.keySet().contains(instanceId) && result.get(instanceId)) {

                    List<String> instanceList = serviceIdList.get(serviceId);
                    if (null != instanceList) {
                        instanceList.add(instanceId);
                    } else {
                        instanceList = new ArrayList<>();
                        instanceList.add(instanceId);
                        serviceIdList.put(serviceId, instanceList);
                    }
                }
            }
        }

        return serviceIdList;
    }

    @Override
    public Message online(String serviceId) {

        Map<String, Boolean> result = refreshEurekaServer(null, serviceId, SynchSpeedProtocol.ONLINEINTERFACE);
        if (CollectionUtils.isEmpty(result)) {
            return Message.buildExceptionResult();
        }
        return null;
    }

}
