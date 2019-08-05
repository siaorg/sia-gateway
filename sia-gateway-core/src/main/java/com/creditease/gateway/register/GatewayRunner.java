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


package com.creditease.gateway.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.creditease.gateway.alarm.AlarmService;
import com.creditease.gateway.cache.FilterCacheManager;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.repository.RouteRepository;
import com.creditease.gateway.route.locator.ZuulListofServerLocator;
import com.creditease.gateway.service.RateLimitService;
import com.creditease.gateway.service.RouteRibbonService;
import com.creditease.gateway.service.ZuulService;
import com.creditease.gateway.service.impl.UrlAnalysisService;

/**
 * Service初始化
 *
 * @author peihua
 **/
@Component
public class GatewayRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GatewayRunner.class);

    @Value("${spring.application.name}")
    private String zuulGroupName;

    @Value("${zuul.filter.url.maxqueueSize}")
    private int maxqueuSize;

    @Autowired
    FilterCacheManager filterCacheManager;

    @Autowired
    ZuulService zuulservice;

    @Autowired
    RateLimitService rls;

    @Autowired
    RouteRibbonService rrs;

    @Autowired
    AlarmService alarmservice;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    ZuulListofServerLocator listOfServerLocator;

    @Autowired
    UrlAnalysisService urlanalysisservice;

    /**
     * 初始化FilterCacheManager
     */
    @Override
    public void run(String... args) {

        logger.info(">FilterCacheManager开始初始化初始化");

        filterCacheManager.initbwListCache();

        registerZuul();

        rls.refreshRateLimtMap();

        rrs.routeRibbonRefresh();

        routeRepository.locateRoutesFromDB();

        listOfServerLocator.initializerListofServer(routeRepository.getLofservers());

        urlanalysisservice.init(maxqueuSize);
    }

    public void registerZuul() {

        try {
            alarmservice.setZuulGroupName(zuulGroupName);
            zuulservice.registerZuul();
        }
        catch (DuplicateKeyException e) {

            logger.error(">ZuulRunner,DuplicateKeyException:{}", e.getMessage());
            zuulservice.updateZuul(GatewayConstant.ZuulState.RUNNING, zuulGroupName);
        }
        catch (Exception e) {

            logger.error(e.getMessage());
        }
    }

}
