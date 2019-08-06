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


package com.creditease.gateway.discovery;

import java.util.*;
import java.util.stream.Collectors;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.database.BaseRepository;
import com.creditease.gateway.domain.EurekaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;

/**
 * 服務实例发现机制
 *
 * @author peihua
 */

@Component
public class DiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired(required = false)
    private BaseRepository baseRepository;

    public List<ServiceInstance> getServiceInstanceList() {

        List<ServiceInstance> result = new ArrayList<>();

        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            result.addAll(discoveryClient.getInstances(service));
        }

        return result;
    }

    public Map<String, List<String>> getServiceListByIp(String ip) {

        List<String> names = baseRepository.getZuulGroupName(ip);
        if (!names.isEmpty()) {
            EurekaInfo info = baseRepository.getEurekaInfo(names.get(0));
            if (info != null) {
                return getServiceListByIpV2(ip, info.getEurekaUrls());
            }
        }

        Map<String, List<String>> result = new LinkedHashMap<>(8);

        List<String> services = discoveryClient.getServices();
        for (String serviceId : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            for (ServiceInstance instance : instances) {
                if (!instance.getHost().equals(ip)) {
                    continue;
                }

                if (result.get(instance.getServiceId()) == null) {
                    List<String> list = new ArrayList<>();
                    list.add(instance.getHost() + StringHelper.MAOHAO_SEPARATOR + instance.getPort());

                    result.put(instance.getServiceId(), list);
                } else {
                    result.get(instance.getServiceId()).add(instance.getHost()
                            + StringHelper.MAOHAO_SEPARATOR + instance.getPort());
                }
            }
        }

        return result;
    }

    public List<String> getServiceList(String serviceId) {

        if (serviceId.toUpperCase().endsWith(GatewayConstant.ZUUL_POSTFIX)) {
            EurekaInfo info = baseRepository.getEurekaInfo(serviceId);
            if (info != null) {
                return getServiceListV2(info.getEurekaUrls(), serviceId);
            }
        }

        return discoveryClient.getInstances(serviceId).stream().
                map(service -> service.getHost() + ":" + service.getPort()).collect(Collectors.toList());
    }

    public Map<String, Map<String, String>> getServiceStatus() {

        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            if (instances == null || instances.size() < 1) {
                continue;
            }

            Map<String, String> map = new LinkedHashMap<>(instances.size());
            for (ServiceInstance instance : instances) {
                map.put(instance.getHost() + ":" + instance.getPort(),
                        ((EurekaDiscoveryClient.EurekaServiceInstance) instance).getInstanceInfo().getStatus().toString());
            }

            result.put(service, map);
        }

        return result;
    }

    public Map<String, String> getServiceStatus(String serviceId) {

        if (serviceId.toUpperCase().endsWith(GatewayConstant.ZUUL_POSTFIX)) {
            EurekaInfo info = baseRepository.getEurekaInfo(serviceId);
            if (info != null) {
                return getServiceStatusV2(info.getEurekaUrls(), serviceId);
            }
        }

        Map<String, String> result = new HashMap<>(8);

        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        for (ServiceInstance instance : list) {
            result.put(instance.getHost() + ":" + instance.getPort(),
                    ((EurekaDiscoveryClient.EurekaServiceInstance) instance).getInstanceInfo().getStatus().toString());
        }

        return result;
    }

    public List<String> getServiceVersions(String serviceId) {

        if (serviceId.toUpperCase().endsWith(GatewayConstant.ZUUL_POSTFIX)) {
            EurekaInfo info = baseRepository.getEurekaInfo(serviceId);
            if (info != null) {
                return getServiceVersionsV2(info.getEurekaUrls(), serviceId);
            }
        }

        return discoveryClient.getInstances(serviceId).stream().map(service -> service.getMetadata().get("version"))
                .collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes"})
    public String getServiceInstanceListV2(String eurekaUrls) {

        List<String> urls = Arrays.asList(eurekaUrls.split(StringHelper.DOHAO_SEPARATOR));
        String json = "";

        for (String url : urls) {
            try {
                json = restTemplate.getForObject(url + "/apps", String.class);
                break;
            } catch (Exception e) {
                LOGGER.error("request failed, url:[" + url + "/apps]", e);
            }
        }

        return json;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Map> getServiceInstanceListV2(String eurekaUrls, String serviceId) {

        List<String> urls = Arrays.asList(eurekaUrls.split(StringHelper.DOHAO_SEPARATOR));
        String json = "";

        for (String url : urls) {
            try {
                json = restTemplate.getForObject(url + "/apps/" + serviceId, String.class);
                break;
            } catch (Exception e) {
                LOGGER.error("request failed, url:[" + url + "/apps/" + serviceId + "]");
            }
        }

        if (StringHelper.isEmpty(json)) {
            return Collections.emptyList();
        }

        Map map = JsonHelper.toObject(json, Map.class);
        Map application = (Map) map.get("application");

        return (List<Map>) application.get("instance");
    }

    public Map<String, List<String>> getServiceListByIpV2(String ip, String eurekaUrls) {

        String json = getServiceInstanceListV2(eurekaUrls);
        if (StringHelper.isEmpty(json)) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> result = new LinkedHashMap<>(8);
        Map jsonMp = JsonHelper.toObject(json, Map.class);
        List<Map> list = (List<Map>) ((Map) jsonMp.get("applications")).get("application");

        for (Map application : list) {
            List<Map> instances = (List<Map>) application.get("instance");
            for (Map<String, String> instance : instances) {
                if (instance.get("hostName").equals(ip)) {
                    if (result.get(application.get("name")) == null) {
                        List<String> instanceList = new ArrayList(8);
                        instanceList.add(instance.get("instanceId"));

                        result.put((String) application.get("name"), instanceList);
                    } else {
                        result.get(application.get("name")).add(instance.get("instanceId"));
                    }
                }
            }
        }

        return result;
    }

    public Map<String, Map<String, String>> getServiceStatusV2(String eurekaUrls) {

        Map<String, Map<String, String>> result = new HashMap<>(8);

        String json = getServiceInstanceListV2(eurekaUrls);
        if (StringHelper.isEmpty(json)) {
            return Collections.emptyMap();
        }

        Map jsonMp = JsonHelper.toObject(json, Map.class);
        List<Map> list = (List<Map>) ((Map) jsonMp.get("applications")).get("application");

        for (Map application : list) {
            Map<String, String> map = new LinkedHashMap<>(8);
            List<Map> instances = (List<Map>) application.get("instance");

            for (Map<String, String> instance : instances) {
                map.put(instance.get("instanceId"), instance.get("status"));
            }

            result.put((String) application.get("name"), map);
        }

        return result;
    }

    public Map<String, String> getServiceStatusV2(String eurekaUrls, String serviceId) {

        Map<String, String> result = new HashMap<>(8);

        List<Map> instances = getServiceInstanceListV2(eurekaUrls, serviceId);
        if (instances.isEmpty()) {
            return result;
        }

        for (Map<String, String> instance : instances) {
            result.put(instance.get("instanceId"), instance.get("status"));
        }

        return result;
    }

    public List<String> getServiceListV2(String eurekaUrls, String serviceId) {

        List<String> result = new ArrayList<>();

        List<Map> instances = getServiceInstanceListV2(eurekaUrls, serviceId);
        for (Map instance : instances) {
            result.add((String) instance.get("instanceId"));
        }

        return result;
    }

    public List<String> getServiceVersionsV2(String eurekaUrls, String serviceId) {

        List<String> result = new ArrayList<>();

        List<Map> instances = getServiceInstanceListV2(eurekaUrls, serviceId);
        for (Map instance : instances) {
            Map metadata = (Map) instance.get("metadata");
            result.add((String) metadata.get("version"));
        }

        return result;
    }

}
