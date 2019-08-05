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


package com.creditease.gateway.admin.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.creditease.gateway.admin.domain.AdminInfo;
import com.creditease.gateway.admin.domain.StatisticQuery;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.GatewayConstant.ZuulState;
import com.creditease.gateway.domain.AlarmInfo;
import com.creditease.gateway.domain.CounterPair;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 首页管理Service
 * 
 * @author peihua
 */

@Service
public class AdminService extends BaseAdminService {

    @Value("${delay}")
    private int delay = 4000;

    @Value("${monitorName}")
    private String monitorName;

    /**
     * 取得节点信息狀態 service
     * 
     */
    public List<ZuulInfo> getZuulList() throws Exception {

        List<ZuulInfo> listdb = adminDBRepository.queryZuulList();

        for (ZuulInfo zuul : listdb) {
            String zuulName = zuul.getZuulGroupName();
            List<String> zuulListEureka = zuulDiscovery.getServiceList(zuulName);
            zuul.setZuulStatus("Dead");
            for (String zInstanceId : zuulListEureka) {
                if (zuul.getZuulInstanceId().equals(zInstanceId)) {
                    zuul.setZuulStatus("Running");
                }
            }
        }
        return listdb;
    }

    /**
     * 查看节点信息详情
     */
    public ZuulInfo queryZuulDetail(String instanceID) throws Exception {

        return adminDBRepository.queryZuulDetail(instanceID);
    }

    /**
     * 删除节点
     */
    public int deleteZuul(String instanceID) throws Exception {

        return adminDBRepository.deleteZuul(instanceID);
    }

    /**
     * 取得Admin节点信息狀態 service
     */
    public List<AdminInfo> getAdminListStatus() throws Exception {

        List<AdminInfo> listdb = adminDBRepository.queryAdminList();
        for (AdminInfo admin : listdb) {

            String adminName = admin.getAdminHotsName();
            List<String> adminListEureka = zuulDiscovery.getServiceList(adminName);
            admin.setAdminStatus("dead");
            for (String admininst : adminListEureka) {
                if (admin.getAdminInstanceid().equals(admininst)) {
                    admin.setAdminStatus("running");
                }
            }
        }
        return listdb;
    }

    /**
     * 刪除Admin节点信息
     */
    public int deleteZuulAdmin(String adminInstanceid) throws Exception {

        return adminDBRepository.deleteZuulAdmin(adminInstanceid);
    }

    /**
     * 取得API累计调用次数
     */
    public int getSumAccess() throws Exception {

        int rst = 0;
        rst = adminDBRepository.getSumCounter();
        LOGGER.info("getSumAccess rst:" + rst);
        return rst;
    }

    /**
     * 取得API調用趨勢
     */
    public Message getAPIInvokeArray(int operateFlag) {

        try {
            Map<String, List<CounterPair>> counterPairMap = Maps.newHashMap();
            List<CounterPair> counterPairs = null;
            if (GatewayConstant.OptEnum.COUNTARRAY.value() == operateFlag) {
                counterPairs = adminDBRepository.getCounterPairs(GatewayConstant.TOTAL, "");
            }
            else {
                counterPairs = adminDBRepository.getHealthy(GatewayConstant.TOTAL, "");
            }
            counterPairMap.put(GatewayConstant.TOTAL, counterPairs);
            String startTime = !CollectionUtils.isEmpty(counterPairs)
                    ? counterPairs.get(counterPairs.size() - 1).getCounterkey() : "";

            List<String> instanceIds = adminDBRepository.getInstanceIdsByGroup(startTime);

            for (String instanceId : instanceIds) {
                if (operateFlag == GatewayConstant.OptEnum.COUNTARRAY.value()) {
                    counterPairs = adminDBRepository.getCounterPairs(instanceId, startTime);
                }
                else if (operateFlag == GatewayConstant.OptEnum.HEALTHARRAY.value()) {
                    counterPairs = adminDBRepository.getHealthy(instanceId, startTime);
                }
                if (!CollectionUtils.isEmpty(counterPairs)) {
                    counterPairMap.put(instanceId, counterPairs);
                }
            }
            /**
             * 加工数据 保证实例间的数据的一致性
             */
            if (!CollectionUtils.isEmpty(counterPairMap)) {
                processResultData(counterPairMap, operateFlag == GatewayConstant.OptEnum.COUNTARRAY.value()
                        ? GatewayConstant.ZERO : GatewayConstant.ONE);
            }

            return Message.buildSuccessResult(counterPairMap);
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
            return Message.buildExceptionResult();
        }
    }

    private void processResultData(Map<String, List<CounterPair>> counterPairMap, String countValue) {

        List<CounterPair> totalList = counterPairMap.get(GatewayConstant.TOTAL);
        if(CollectionUtils.isEmpty(totalList)){
            return;
        }

        List<CounterPair> baseCounterPairs = totalList.stream().map(x -> {
            String timekey = x.getCounterkey();
            CounterPair counterPair = new CounterPair();
            counterPair.setCounterkey(timekey);
            counterPair.setCountervalue(countValue);
            return counterPair;
        }).collect(Collectors.toList());
        for (Map.Entry<String, List<CounterPair>> entry : counterPairMap.entrySet()) {
            String key = entry.getKey();
            List<CounterPair> counterPairList = entry.getValue();
            if (!GatewayConstant.TOTAL.equals(key)) {
                List<CounterPair> copyCounterPairs = Lists.newArrayList(baseCounterPairs);
                /**
                 * 去重 + 合并
                 */
                copyCounterPairs.removeAll(counterPairList);
                counterPairList.addAll(copyCounterPairs);
            }

            /**
             * 排序 --> 升序
             */
            if(!CollectionUtils.isEmpty(counterPairList)){
                Collections.sort(counterPairList, (o1, o2) -> o1.getCounterkey().compareTo(o2.getCounterkey()));
            }
        }
    }

    /**
     * 取得Route-API調用趨勢
     * 
     */
    @SuppressWarnings("rawtypes")
    public List getRouteInvokeArray(StatisticQuery request) throws Exception {

        return adminDBRepository.getRouteAccessCount(request);
    }

    /**
     * 取得Route-API健康趨勢
     */
    @SuppressWarnings("rawtypes")
    public List getRouteHealthyArray(StatisticQuery request) throws Exception {

        return adminDBRepository.getRouteHealthCount(request);
    }

    /**
     * 取得路由注册个数
     */
    public int getRegisterRouteNo() throws Exception {

        return adminDBRepository.queryRouteCount();
    }

    /**
     * 取得Alarm List
     */
    public List<AlarmInfo> getAlarmList() throws Exception {

        return adminDBRepository.queryAlarmList();
    }

    /**
     * 取得Alarm個數
     */
    public Integer getAlarmCount() throws Exception {

        return adminDBRepository.queryAlarmCount();
    }

    /**
     * Admin 管理
     */

    public boolean registerZuulAdmin() throws Exception {

        return adminDBRepository.registeradmin();
    }

    public boolean updateZuulAdmin(ZuulState running) throws Exception {

        return adminDBRepository.updateAdmin(running);
    }

    public String getValidRoutebyID(String adminInstanceid) throws Exception {

        String url = "http://" + adminInstanceid;
        String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.RWH.getValue(), null);
        return result;
    }

    /**
     * 根据路由id获取网关名称
     */
    @Override
    public String getZuulGroupName(String routeid) {

        return adminDBRepository.queryZuulGroupName(routeid);
    }

    /**
     * 取得所有Zuul的List
     */
    public List<ZuulInfo> getAllZuulList() {

        return adminDBRepository.queryAllZuulList();
    }

    public List<String> getAllGroupList() {

        return adminDBRepository.queryZuulGroupList();
    }

    /**
     * 取得所有Zuul的Group set
     */
    public Set<String> getAllGroupSet() {

        return new HashSet<>(getAllGroupList());
    }

    public int updateZuulDescByGroupName(Map<String, String> map) throws Exception {

        try {
            String zuulGroupName = map.get("zuulGroupName");
            String zuulGroupDesc = map.get("zuulGroupDesc");
            return adminDBRepository.updateZuulDescByGroupName(zuulGroupName, zuulGroupDesc);
        }
        catch (Exception e) {
            LOGGER.error("exception occur..", e);
            throw e;
        }
    }

    public Message getMonitorUrl(String hostInfo) {

        try {
            List<String> monitorInfos = zuulDiscovery.getServiceList(monitorName);
            if (CollectionUtils.isEmpty(monitorInfos) || StringUtils.isEmpty(monitorInfos.get(0))) {
                return Message.buildExceptionResult("hystrix dashboard not exists!");
            }
            String monitorUrl = StringHelper.format(GatewayConstant.MONITORURL, monitorInfos.get(0), delay, hostInfo,
                    hostInfo);
            LOGGER.error("monitorUrl={}", monitorUrl);
            return Message.buildSuccessResult(monitorUrl);
        }
        catch (Exception e) {
            LOGGER.error("Failed to get monitoring address...", e);
            return Message.buildExceptionResult();
        }

    }
}
