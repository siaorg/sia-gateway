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


package com.creditease.gateway.filter.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.service.RateLimitService;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * 统计、限流 功能：路由綁定后才能生效
 * 
 * @author peihua
 */

@Component
public class RateLimitFilter extends AbstractGatewayFilter {

    @Value("${zuul.ratelimit.enabled}")
    private String ratelimitEnabled;

    @Autowired
    protected RateLimitService rls;

    @Override
    public String getFilterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public boolean isEnabled() {

        return Boolean.parseBoolean(ratelimitEnabled);
    }

    @Override
    public void process(RequestContext ctx, String routeid) {

        logger.debug("开始路由流量限流处理");

        rls.runRateLimit(ctx, routeid);
    }

    @Override
    public String getCompName() {

        return "RATELIMIT";
    }
}
