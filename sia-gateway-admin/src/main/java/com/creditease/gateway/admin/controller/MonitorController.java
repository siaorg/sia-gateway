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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;

/**
 * 网关监控管理
 * 
 * @Author: yongbiao
 * 
 * @Date: 2019-06-12 10:48
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController extends BaseAdminController {

    private static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    private static final String UPTIME = "uptime";
    private static final String MEM = "mem";
    private static final String MEM_FREE = "mem.free";
    private static final String HEAP = "heap";
    private static final String HEAP_COMMITTED = "heap.committed";
    private static final String HEAP_INIT = "heap.init";
    private static final String HEAP_USED = "heap.used";
    private static final String NON_HEAP_COMMITTED = "nonheap.committed";
    private static final String NON_HEAP_INIT = "nonheap.init";
    private static final String NON_HEAP_USED = "nonheap.used";
    private static final String NON_HEAP = "nonheap";
    private static final String PROCESSORS = "processors";
    private static final String SYSTEM_LOAD_AVERAGE = "systemload.average";
    private static final String THREADS_PEAK = "threads.peak";
    private static final String THREADS_DAEMON = "threads.daemon";
    private static final String THREADS_TOTAL_STARTED = "threads.totalStarted";
    private static final String THREADS = "threads";
    private static final String CLASSES = "classes";
    private static final String CLASSES_LOADED = "classes.loaded";
    private static final String CLASSES_UNLOADED = "classes.unloaded";
    private static final String GC_PARNEW_COUNT = "gc.parnew.count";
    private static final String GC_PARNEW_TIME = "gc.parnew.time";
    private static final String GC_CONCURRENTMARKSWEEP_COUNT = "gc.concurrentmarksweep.count";
    private static final String GC_CONCURRENTMARKSWEEN_TIME = "gc.concurrentmarksweep.time";
    private static final String KEEP_ALIVE_COUNT = "keepAliveCount";
    private static final String MAX_CONNECTIONS = "maxConnections";

    @Value("${monitorAddress}")
    private String monitorAddress;

    @Autowired
    private ZuulHandler handler;

    @Autowired
    private DiscoveryService discoveryService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/simple", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getZuulSimple(@RequestParam("ipports") List<String> ipPorts) {

        if (ipPorts == null || ipPorts.isEmpty()) {
            logger.warn("Parameter of ipports is null!");

            return null;
        }

        logger.info("getZuulSimple, ipports:{}", JsonHelper.toString(ipPorts));

        Map<String, Map<String, Object>> result = new HashMap<>(8);
        for (String ipPort : ipPorts) {
            Map<String, Object> map = new LinkedHashMap<>(6);
            try {
                String json = handler.executeHttpCmd("http://" + ipPort + "/metrics");
                Map<String, Object> metrics = JsonHelper.toObject(json, Map.class);

                map.put(UPTIME, metrics.get(UPTIME));
                map.put(PROCESSORS, metrics.get(PROCESSORS));
                map.put(SYSTEM_LOAD_AVERAGE, metrics.get(SYSTEM_LOAD_AVERAGE));
                map.put(MEM, metrics.get(MEM));
                map.put(MEM_FREE, metrics.get(MEM_FREE));

                // 新版本才支持/keepAliveCount接口，所以先赋值为0，防止访问旧版本时无该接口导致无数据
                map.put(KEEP_ALIVE_COUNT, 0);
                map.put(MAX_CONNECTIONS, 0);
            }
            catch (Exception e) {
                logger.warn("Get cpu and memory failed, url: {}/metrics", ipPort);
            }

            try {
                String json = handler.executeHttpCmd("http://" + ipPort + "/keepAliveCount");
                Map<String, Object> jsonMap = JsonHelper.toObject(json, Map.class);
                if (jsonMap != null) {
                    map.putAll(jsonMap);
                }
            }
            catch (Exception e) {
                logger.warn("Get connection count failed, url: {}/keepAliveCount", ipPort);
            }

            if (!map.isEmpty()) {
                result.put(ipPort, map);
            }
        }

        return JsonHelper.toString(result);
    }

    @RequestMapping(value = "/jvm", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getZuulJvm(@RequestParam("ipport") String ipPort) {

        if (StringHelper.isEmpty(ipPort)) {
            logger.warn("getZuulJvm, ipport is empty!");
            return null;
        }

        logger.info("getZuulJvm, ipport:{}", ipPort);

        String json = handler.executeHttpCmd("http://" + ipPort + "/metrics");

        Map<String, Object> metrics = JsonHelper.toObject(json, Map.class);
        Map<String, Object> result = new LinkedHashMap<>(18);

        result.put(MEM, metrics.get(MEM));
        result.put(MEM_FREE, metrics.get(MEM_FREE));

        result.put(HEAP, metrics.get(HEAP));
        result.put(HEAP_INIT, metrics.get(HEAP_INIT));
        result.put(HEAP_USED, metrics.get(HEAP_USED));
        result.put(HEAP_COMMITTED, HEAP_COMMITTED);

        result.put(NON_HEAP, metrics.get(NON_HEAP));
        result.put(NON_HEAP_INIT, metrics.get(NON_HEAP_INIT));
        result.put(NON_HEAP_USED, metrics.get(NON_HEAP_USED));
        result.put(NON_HEAP_COMMITTED, metrics.get(NON_HEAP_COMMITTED));

        result.put(UPTIME, metrics.get(UPTIME));
        result.put(PROCESSORS, metrics.get(PROCESSORS));
        result.put(SYSTEM_LOAD_AVERAGE, metrics.get(SYSTEM_LOAD_AVERAGE));

        result.put(CLASSES, metrics.get(CLASSES));
        result.put(CLASSES_LOADED, metrics.get(CLASSES_LOADED));
        result.put(CLASSES_UNLOADED, metrics.get(CLASSES_UNLOADED));

        result.put(THREADS, metrics.get(THREADS));
        result.put(THREADS_DAEMON, metrics.get(THREADS_DAEMON));
        result.put(THREADS_PEAK, metrics.get(THREADS_PEAK));
        result.put(THREADS_TOTAL_STARTED, metrics.get(THREADS_TOTAL_STARTED));

        result.put(GC_PARNEW_COUNT, metrics.get(GC_PARNEW_COUNT));
        result.put(GC_PARNEW_TIME, metrics.get(GC_PARNEW_TIME));
        result.put(GC_CONCURRENTMARKSWEEP_COUNT, metrics.get(GC_CONCURRENTMARKSWEEP_COUNT));
        result.put(GC_CONCURRENTMARKSWEEN_TIME, metrics.get(GC_CONCURRENTMARKSWEEN_TIME));

        return JsonHelper.toString(result);
    }

    @RequestMapping(value = "/env", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getZuulEnv(@RequestParam("ipport") String ipPort) {

        if (StringHelper.isEmpty(ipPort)) {
            logger.warn("getZuulEnv, ipport is empty!");
            return null;
        }

        logger.info("getZuulEnv, ipport:{}", ipPort);

        String json = handler.executeHttpCmd("http://" + ipPort + "/env");

        Map<String, Object> env = JsonHelper.toObject(json, Map.class);
        Map<String, Map<String, Object>> result = new HashMap<>(2);

        for (String key : env.keySet()) {
            if (key.startsWith("applicationConfig:")) {
                result.put(key, (Map) env.get(key));
            }
        }

        return JsonHelper.toString(result);
    }

    @RequestMapping(value = "/getLogUrl", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    @ResponseBody
    public String getLogUrl(@RequestParam("ipport") String ipPort) {

        try {
            if (StringUtils.isEmpty(monitorAddress)) {
                logger.warn("ipPort of {} is empty!", monitorAddress);

                return null;
            }

            String url = "http://" + monitorAddress + "/monitor/logfile?ipport=" + ipPort;

            logger.info("getLogUrl, url:[{}]", url);

            Message resp = new Message(url);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writeValueAsString(resp);
        }
        catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return returnErrorMsg(e.getLocalizedMessage(), Message.ResponseCode.SERVER_ERROR_CODE);
        }
    }
}
