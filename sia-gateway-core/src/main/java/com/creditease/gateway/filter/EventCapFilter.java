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


package com.creditease.gateway.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PROXY_KEY;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.topology.Event;
import com.creditease.gateway.topology.intercept.ThreadContext;
import com.creditease.gateway.topology.intercept.ThreadInterceptProcessor;
import com.netflix.zuul.context.RequestContext;

/**
 * 拓扑管理拦截
 * 
 * @author peihua
 */

@Component
public class EventCapFilter extends AbstractGatewayFilter {

    @Value("${spring.application.name}")
    private String sagGroupName;

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    @Override
    public String getFilterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return PRE_DECORATION_FILTER_ORDER + 2;
    }

    protected static final String SEND_FORWARD_FILTER_RAN = "sendForwardFilter.ran";

    @Override
    public void process(RequestContext ctx, String routeid) {

    	try {
    		 routeid = (String) ctx.get(PROXY_KEY);
    		 Event e = new Event(routeid);

    		 e.setGroupName(sagGroupName);
    		 long currentTime = System.currentTimeMillis();
    		 e.setStartTime(currentTime);
    		 // 有可能是个IP或者域名

    		 String host = ctx.getRequest().getHeader("Host");
    		 e.setInstanceId(instanceId);

    		 String userAgent = ctx.getRequest().getHeader("User-Agent");
    		 if (userAgent.contains("Mozilla") || userAgent.contains("Chrome") || userAgent.contains("Windows")) {  
    			 e.setClientNodes(GatewayConstant.BROWSER + host);
    		 }
    		 else {   
    			 e.setClientNodes(GatewayConstant.APPLICATION + host);
    		 }
    		 ThreadInterceptProcessor.putContext(new ThreadContext(e));
    	
    	}catch (Exception e) {           
    		logger.error(">EventCapFilter exception:{}", e.getMessage());  
    	}
    }

    @Override
    public String getCompName() {

        return "COMMON-TOPO";
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
