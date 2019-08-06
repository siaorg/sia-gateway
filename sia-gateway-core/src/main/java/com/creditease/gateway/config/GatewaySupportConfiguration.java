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


package com.creditease.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.creditease.gateway.alarm.AlarmService;
import com.creditease.gateway.service.AuthService;
import com.creditease.gateway.service.LogService;
import com.creditease.gateway.service.RateLimitService;
import com.creditease.gateway.service.RouteOptService;
import com.creditease.gateway.service.RouteRibbonService;
import com.creditease.gateway.service.StatisticService;
import com.creditease.gateway.service.ZuulService;
import com.creditease.gateway.service.impl.AuthServiceImpl;
import com.creditease.gateway.service.impl.LogServiceImpl;
import com.creditease.gateway.service.impl.RateLimitServiceImpl;
import com.creditease.gateway.service.impl.RouteOptServiceImpl;
import com.creditease.gateway.service.impl.RouteRibbonServiceImpl;
import com.creditease.gateway.service.impl.StatisticServiceImpl;
import com.creditease.gateway.service.impl.ZuulServiceImpl;

/**
 * 网关的主要服务配置
 * 
 * @author peihua
 **/

@Configuration
public class GatewaySupportConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RouteOptService routoptService() {

        return new RouteOptServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogService logService() {

        return new LogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {

        return new AuthServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RouteRibbonService ribbonoptService() {

        return new RouteRibbonServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public StatisticService statisticService() {

        return new StatisticServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RateLimitService ratelimitService() {

        return new RateLimitServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AlarmService monitorService() {

        return new AlarmService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZuulService zuulService() {

        return new ZuulServiceImpl();
    }

}
