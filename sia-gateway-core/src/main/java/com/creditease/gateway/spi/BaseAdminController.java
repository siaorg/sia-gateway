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


package com.creditease.gateway.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.service.AuthService;
import com.creditease.gateway.service.RateLimitService;
import com.creditease.gateway.service.RouteOptService;
import com.creditease.gateway.service.RouteRibbonService;
import com.creditease.gateway.service.StatisticService;

/**
 * 网关南向接口服务
 * 
 * @author peihua
 * 
 * */

public class BaseAdminController {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(BaseAdminController.class);

    @Autowired
    protected RouteRibbonService ribbonoptservice;
    
    @Autowired
    protected RateLimitService ratelimitservice;
    
    @Autowired
    protected RouteOptService routeoptservice;
    
    @Autowired
    protected StatisticService statisticservice;
    
    @Autowired
    protected AuthService bwlistservice;
}

