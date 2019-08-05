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

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.netflix.zuul.context.RequestContext;
/**
 * 
 * Debug日志开启功能
 * 
 * @author peihua
 */

public class ZuulDebugFilter extends AbstractGatewayFilter {

	@Value("${zuul.debug.enabled}")
	private String debugEnabled;

	@Override
	public String getFilterType() {		
		
		return FilterConstants.POST_TYPE;
	}

	@Override
	public int getFilterOrder() {
		 return 999999;
	}

	@Override
	public boolean isEnabled() {
	    return Boolean.parseBoolean(debugEnabled);	  
	}

	@Override
	public void process(RequestContext ctx, String routeid) {

		@SuppressWarnings("unchecked")
		final List<String> routingDebug = (List<String>) RequestContext.getCurrentContext().get("routingDebug");
		routingDebug.forEach(System.out::println);
	}

	@Override
	public String getCompName() {
		return "COMMON-DEBUG";
	}
  
}
