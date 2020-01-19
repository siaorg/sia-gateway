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



package com.creditease.gateway.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.FIFOCache;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.domain.topo.Link;
import com.creditease.gateway.domain.topo.RouteTopo;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.service.SchedulerService;

/**
 * 拓扑数据定时统计任务
 * 
 * I. 保存拓扑数据到本地Local-数据存储(TODO) II. 初始化时加载本地Local-数据(TODO)
 * 
 * @author peihua
 * 
 */

@Component
public class TopoLogyScheduledTask {

    @Autowired
    SchedulerService sts;

    @Autowired
    ZuulHandler handler;

    private static final Logger LOGGER = LoggerFactory.getLogger(TopoLogyScheduledTask.class);

    private AtomicReference<Map<String, Map<String, RouteTopo>>> topoAll = new AtomicReference<>();

    @Scheduled(cron = "0 0/10 * * * *")
    public void statisticTopoTask() {

        LOGGER.info(">开始拓扑统计 ");
        List<ZuulInfo> zuuList = new ArrayList<>();
        try {
            zuuList = sts.getZuulList();
        }
        catch (Exception e) {
            LOGGER.error(">TopoLogyScheduledTask Exception:{}", e.getCause());
        }
        LOGGER.info(">zuuList size:{}", zuuList.size());

        statisticByZuulList(zuuList);
    }

    public void statisticByZuulList(List<ZuulInfo> zuulist) {

        try {

            /**
             * step1: 统计
             */
            Map<String, Map<String, RouteTopo>> topoTemp = new HashMap<>(8);

            LOGGER.info("statisticByZuulList start...");

            for (ZuulInfo zinfo : zuulist) {

                String status = zinfo.getZuulStatus();
                String instance = zinfo.getZuulInstanceId();

                LOGGER.info("statisticByZuulList status:{}", status);
                LOGGER.info("statisticByZuulList instance:{}", instance);

                if (("Dead").equals(status)) {
                    LOGGER.info(">ZuulInstanceId:{},zinfo status is :", zinfo.getZuulInstanceId(), status);
                    continue;
                }
                String url = "http://" + instance;

                LOGGER.info("statisticTopoTask url:{}", url);
                try {
                    String rst = handler.executeHttpGetCmd(url, GatewayConstant.ADMINOPTKEY.GTP.getValue());

                    LOGGER.info(">>> remoteCall rst:" + rst);

                    if (StringHelper.isEmpty(rst)) {
                        LOGGER.info("> rst is empty >");
                        continue;
                    }

                    Map<String, Object> response = JsonHelper.toObject(rst, Map.class);
                    Map<String, String> requestValue = (Map<String, String>) response.get("request");
                    Map<String, Map> responseValue = (Map<String, Map>) response.get("response");
                    // transfer responseVlaue --> Map<String, RouteTopo>
                    Map<String, RouteTopo> transferTopo = new HashMap<>(8);

                    LOGGER.info("> start transfer");
                    transfter(transferTopo, responseValue);
                    LOGGER.info("> end transfer");
                    String zuulGroupName = requestValue.get(GatewayConstant.GWGROUPNAME);

                    LOGGER.info(">  zuulgroupName >{}", zuulGroupName);

                    Map<String, RouteTopo> routeTopoMap = topoTemp.get(zuulGroupName);
                    if (null == routeTopoMap) {
                        // 第一级（网关组）不存在
                        if (null != transferTopo) {
                            topoTemp.put(zuulGroupName, transferTopo);
                        }
                        LOGGER.info("> start transferTopo:{}", transferTopo);
                    }
                    else {
                        // 第二级（网关组下路由）合并
                        LOGGER.info("> start merge");
                        merge(routeTopoMap, transferTopo);
                        LOGGER.info("> end merge");
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Exception, url: " + url, e);
                }

            }

            /**
             * step2: 交换
             * 
             */
            Swap(topoTemp);

            LOGGER.info(">>> remoteCall rst2 finish");
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(">>> get executeHttpCmd Exception:" + e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void transfter(Map<String, RouteTopo> dest, Map<String, Map> source) throws InterruptedException {

        for (String key : source.keySet()) {
            Map routeTopo = source.get(key);
            RouteTopo topoDest = new RouteTopo();
            topoDest.setRouteId((String) routeTopo.get("routeId"));
            topoDest.setGroupName((String) routeTopo.get("groupName"));

            // [应用:127.0.0.1:8080]
            topoDest.setClientNodes(new HashSet<>((List) routeTopo.get("clientNodes")));
            topoDest.setZuulNodes(new HashSet<>((List) routeTopo.get("zuulNodes")));
            topoDest.setUpstreamNodes(new HashSet<>((List) routeTopo.get("upstreamNodes")));

            // 记录延迟
            topoDest.setMaxDelay((int) routeTopo.get("maxDelay"));
            topoDest.setMinDelay((int) routeTopo.get("minDelay"));
            topoDest.setLastRequestTime((long) routeTopo.get("lastRequestTime"));

            // 记录异常
            topoDest.setExceptionCause(((Map) routeTopo.get("exceptionCause")));

            FIFOCache<String, String> queueCache = topoDest.getRecentDelays();
            Map<String, String> recentDelay = ((HashMap<String, Map<String, String>>) routeTopo.get("recentDelays"))
                    .get("map");

            if (null != recentDelay && recentDelay.size() > 0) {
                for (String d : recentDelay.keySet()) {
                    queueCache.put(d, recentDelay.get(d));
                }
            }
            // Link 状态计算
            long lastTimeStamp = topoDest.getLastRequestTime();

            // [{dest=10.10.168.19:8080, source=应用:127.0.0.1:8080}, {dest=10.10.168.19:8099, source=10.10.168.19:8080}]
            List<Map> list = (List) routeTopo.get("link");

            for (Map link : list) {
                String sourceValue = (String) link.get("source");
                String destValue = (String) link.get("dest");

                Link l = new Link(sourceValue, destValue);

                Object lastRequestTime = link.get("lastRequestTime");

                if (null != lastRequestTime) {
                    String lrt = String.valueOf(lastRequestTime);

                    long requestTime = Long.parseLong(lrt);

                    l.setLastRequestTime(requestTime);

                }
                topoDest.addLink(l);
            }

            dest.put(key, topoDest);
        }
    }

    private void merge(Map<String, RouteTopo> routeTopoMap, Map<String, RouteTopo> newTopo)
            throws InterruptedException {

        for (String routeid : newTopo.keySet()) {

            RouteTopo routetopo = routeTopoMap.get(routeid);

            if (null == routetopo) {
                // 第二级（网关组下的路由）不存在
                routeTopoMap.put(routeid, newTopo.get(routeid));
            }
            else {
                // 开始合并相同路由下数据流
                RouteTopo routeTopoNew = newTopo.get(routeid);

                // 合并节点
                routetopo.getClientNodes().addAll(routeTopoNew.getClientNodes());
                routetopo.getZuulNodes().addAll(routeTopoNew.getZuulNodes());
                routetopo.getUpstreamNodes().addAll(routeTopoNew.getUpstreamNodes());

                // 合并Link
                Set<Link> linkList = routeTopoNew.getLink();
                for (Link l : linkList) {
                    routetopo.addLink(l);
                }

                // 合并Metric
                if (routeTopoNew.getMaxDelay() > routetopo.getMaxDelay()) {
                    routetopo.setMaxDelay(routeTopoNew.getMaxDelay());
                }
                if (routeTopoNew.getMinDelay() < routetopo.getMinDelay()) {
                    routetopo.setMinDelay(routeTopoNew.getMinDelay());
                }
                if (routeTopoNew.getLastRequestTime() > routetopo.getLastRequestTime()) {
                    routetopo.setLastRequestTime(routeTopoNew.getLastRequestTime());
                }

                // 合并exceptionCuse
                Map ex = routeTopoNew.getExceptionCause();
                if (null != ex) {
                    routetopo.getExceptionCause().putAll(ex);
                }

                // 合并Delay

                FIFOCache<String, String> q = routeTopoNew.getRecentDelays();
                FIFOCache<String, String> all = routetopo.getRecentDelays();
                if (null != all) {

                    Map<String, String> mapper = q.getMap();

                    for (String key : mapper.keySet()) {

                        all.put(key, mapper.get(key));

                        LOGGER.debug("> all size:{}", all.getMap().size());
                    }
                }
            }
        }
    }

    public void Swap(Map<String, Map<String, RouteTopo>> topoTemp) {

        topoAll.set(topoTemp);
    }

    public Map<String, Map<String, RouteTopo>> getTopoAll() {

        return this.topoAll.get();
    }
}
