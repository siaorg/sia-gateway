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


package com.creditease.gateway.service;

import com.creditease.gateway.domain.EurekaInfo;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.repository.EurekaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Author: yongbiao
 * @Date: 2019-07-03 17:03
 */
@Service
public class EurekaService {

    private static final Logger logger = LoggerFactory.getLogger(EurekaService.class);

    private static final String EUREKA_URL_KEY_START = "eureka.client.serviceUrl";
    private static final String EUREKA_URL_KEY_START_BAK = "eureka.client.service-url";
    private static final String DEFAULT_EUREKA_URL_KEY = "eureka.client.serviceUrl.defaultZone";

    @Value("${spring.application.name}")
    private String groupName;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private ContextRefresher contextRefresher;

    @Autowired
    private EurekaClientConfigBean eurekaClientConfigBean;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Autowired
    private EurekaRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public List<String> getCurrentEurekaUrls(String zuulGroupName) {

        EurekaInfo info = repository.getEurekaInfo(zuulGroupName);
        if (info == null) {
            Map<String, String> currentEurekaUrls = eurekaClientConfigBean.getServiceUrl();

            return new ArrayList(currentEurekaUrls.values());
        }

        return Arrays.asList(info.getEurekaUrls().split(StringHelper.DOHAO_SEPARATOR));
    }

    public Message setZuulEurekaUrls(String zuulGroupName, String urls) {

        List<String> errorUrls = testEurekaUrls(urls.split(StringHelper.DOHAO_SEPARATOR));
        if (!errorUrls.isEmpty()) {
            String str = JsonHelper.toString(errorUrls);
            logger.info("invalid eureka urls:{}", str);

            return new Message("如下注册中心地址无效：" + str, Message.ResponseCode.PARAM_ERROR_CODE.getCode());
        }

        if (hasOnlineRoute(zuulGroupName)) {
            logger.info("There are ONLINE routes, cannot set zuul eurekaUrls! zuulGroupName:{}", zuulGroupName);

            return new Message("存在状态为发布的路由，不能设置注册中心地址!", Message.ResponseCode.PARAM_ERROR_CODE.getCode());
        }

        EurekaInfo info = repository.getEurekaInfo(zuulGroupName);

        if (!updateEurekaUrls(info, zuulGroupName, urls)) {
            return new Message("更新数据库失败！", Message.ResponseCode.PARAM_ERROR_CODE.getCode());
        }

        return new Message(notifyZuulUpdateEurekaUrls(zuulGroupName, "/setEurekaUrls"));
    }

    public Message resetZuulEurekaUrls(String zuulGroupName) {

        if (hasOnlineRoute(zuulGroupName)) {
            logger.info("There are online routes, cannot reset zuul eurekaUrls! zuulGroupName:{}", zuulGroupName);

            return new Message("存在状态为发布的路由，不能重置注册中心地址！",
                    Message.ResponseCode.PARAM_ERROR_CODE.getCode());
        }

        EurekaInfo info = repository.getEurekaInfo(zuulGroupName);

        if (info == null) {
            logger.warn("there are no eurekaUrls in repository!");
        } else {
            repository.deleteEurekaUrls(zuulGroupName);
        }

        return new Message(notifyZuulUpdateEurekaUrls(zuulGroupName, "/resetEurekaUrls"));
    }

    public boolean setLocalEurekaUrls() {

        try {
            EurekaInfo info = repository.getEurekaInfo(groupName);
            if (info == null) {
                logger.warn("No eurekaUrls in repository.");

                return false;
            }

            String urls = info.getEurekaUrls();

            if (sameAsLocalEurekaUrls(urls.split(StringHelper.DOHAO_SEPARATOR))) {
                return true;
            }

            String eurekaUrlKey = getEurekaUrlKey();
            environmentManager.setProperty(eurekaUrlKey, urls);
            Set<String> set = contextRefresher.refresh();

            logger.info("EnvironmentManager setProperty key:[{}], value:[{}], refresh:{}", eurekaUrlKey, urls,
                    JsonHelper.toString(set));

            return true;
        } catch (Exception e) {
            logger.error("set eureka url failed!", e);

            return false;
        }
    }

    public void resetLocalEurekaUrls() {

        Map<String, Object> map = environmentManager.reset();
        Set<String> set = contextRefresher.refresh();

        logger.info("EnvironmentManager reset:{}, refresh:{}", JsonHelper.toString(map), JsonHelper.toString(set));
    }

    public boolean sameAsLocalEurekaUrls(String[] newEurekaUrls) {

        Map<String, String> currentEurekaUrls = eurekaClientConfigBean.getServiceUrl();

        logger.info("current eureka urls:[{}], new eurekaUrls:[{}]", JsonHelper.toString(currentEurekaUrls),
                JsonHelper.toString(newEurekaUrls));

        if (currentEurekaUrls.size() != newEurekaUrls.length) {
            return false;
        }

        for (String currentEurekaUrl : currentEurekaUrls.values()) {
            boolean flag = false;
            for (String newEurekaUrl : newEurekaUrls) {
                if (newEurekaUrl.startsWith(currentEurekaUrl) || currentEurekaUrl.startsWith(newEurekaUrl)) {
                    flag = true;
                }
            }

            if (!flag) {
                return false;
            }
        }

        return true;
    }

    public String getEurekaUrlKey() {

        String eurekaUrlKey = "";

        Iterator<PropertySource<?>> iterator = configurableEnvironment.getPropertySources().iterator();
        while (iterator.hasNext()) {
            PropertySource source = iterator.next();
            if (source.getName().startsWith("applicationConfig") && source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
                for (String name : enumerable.getPropertyNames()) {
                    if (name.startsWith(EUREKA_URL_KEY_START) || name.startsWith(EUREKA_URL_KEY_START_BAK)) {
                        eurekaUrlKey = name;
                        logger.info("eureka url key in config:[{}]", eurekaUrlKey);
                        break;
                    }
                }
            }
        }

        if (StringHelper.isEmpty(eurekaUrlKey)) {
            logger.warn("cannot find eureka url key in config, so will use default:[{}]", DEFAULT_EUREKA_URL_KEY);

            return DEFAULT_EUREKA_URL_KEY;
        }

        return eurekaUrlKey;
    }

    public List<String> testEurekaUrls(String[] eurekaUrls) {

        List<String> errorUrls = new ArrayList<>(eurekaUrls.length);

        for (String url : eurekaUrls) {
            String statusUrl = url + "/status";

            try {
                restTemplate.getForEntity(statusUrl, String.class);
            } catch (Exception e) {
                logger.warn("request:[{}] error! Eureka Server url:[{}] may invalid", statusUrl, url);
                errorUrls.add(url);
            }
        }

        return errorUrls;
    }

    public boolean updateEurekaUrls(EurekaInfo info, String zuulGroupName, String urls) {

        EurekaInfo newInfo = new EurekaInfo(zuulGroupName, urls, true);
        try {
            if (info == null) {
                repository.insertEurekaUrls(newInfo);

                logger.info("insert to repository success:{}", JsonHelper.toString(newInfo));
            } else if (!newInfo.getEurekaUrls().startsWith(info.getEurekaUrls()) &&
                    !info.getEurekaUrls().startsWith(newInfo.getEurekaUrls())) {
                repository.updateEurekaUrls(newInfo);

                logger.info("update to repository success, eurekaInfo old:{}, new:{}", JsonHelper.toString(info),
                        JsonHelper.toString(newInfo));
            } else {
                logger.info("new eurekaInfo equals to current eurekaInfo:{}", JsonHelper.toString(info));
            }
        } catch (Exception e) {
            logger.error("insert or update repository error!", e);

            return false;
        }

        return true;
    }

    public Map<String, Object> notifyZuulUpdateEurekaUrls(String zuulGroupName, String path) {

        Map<String, Object> result = new HashMap<>(8);
        List<String> list = repository.queryZuulInstances(zuulGroupName);

        for (String ipPort : list) {
            try {
                String coreUrl = "http://" + ipPort + path;
                Boolean response = restTemplate.postForObject(coreUrl, null, Boolean.class);

                result.put(ipPort, response);
            } catch (Exception e) {
                logger.warn("updateEurekaUrl failed! request: [http://{}{}]", ipPort, path);
                result.put(ipPort, false);
            }
        }

        logger.info("notify zuul update eurekaUrls, path:[{}], result:{}", path, JsonHelper.toString(result));

        return result;
    }

    public boolean hasOnlineRoute(String zuulGroupName) {

        return repository.queryOnlineRouteCount(zuulGroupName) > 0;
    }
}
