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


package com.creditease.gateway.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.creditease.gateway.repository.RouteRepository;
import com.creditease.gateway.service.RateLimitService;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.context.RequestContext;

/**
 * 限流服务
 * 
 * @author peihua
 * 
 * */

public class RateLimitServiceImpl implements RateLimitService {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(RateLimitServiceImpl.class);

	private Object mutex = new Object();
	
	private static Map<String,RateLimiter> routlimitMap = Maps.newConcurrentMap();
	
	private static final int DEFAULTLIMIT = 100;
	
	@Autowired
	RouteRepository routerepo;

	@Override
	public void setRoutlimitMap(List<Map<String, Object>> routlimit) {
		
		try {
			synchronized(mutex)
			{
				routlimitMap.clear();
				
				for(Map<String,Object> obj: routlimit)
				{
					String routeid = (String)obj.get("routeid");
					String ratelimit = (String)obj.get("ratelimit");
					try {
						int rl= Integer.valueOf(ratelimit);
						
						if(rl<0)
						{
							rl = DEFAULTLIMIT;
						}
						
						routlimitMap.putIfAbsent(routeid, RateLimiter.create(rl));
						
					} catch (NumberFormatException e) {
						
						routlimitMap.put(routeid, RateLimiter.create(DEFAULTLIMIT));
					}
				}
				
			}
		} catch (Exception e) {
			LOGGER.error(">setRoutlimitMap 报错:"+e.getCause());
		}
	}

	@Override
	public void runRateLimit(RequestContext ctx, String routeid) {
		
		if (routeid == null) {

			URL routeHost = ctx.getRouteHost();

			if (routeHost != null) {
				
				String url = routeHost.toString();
				
				routlimitMap.putIfAbsent(url, RateLimiter.create(DEFAULTLIMIT));
			}

		}

		RateLimiter rateLimiter = routlimitMap.get(routeid);
		
		LOGGER.info("routeid:"+routeid+"  rate:"+rateLimiter.getRate());
		

		if (!rateLimiter.tryAcquire()) {

			HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;

			try {
				ctx.getResponse().setContentType(MediaType.TEXT_PLAIN_VALUE);

				ctx.getResponse().setStatus(httpStatus.value());

				ctx.getResponse().getWriter().append(httpStatus.getReasonPhrase());

				ctx.setSendZuulResponse(false);

			} catch (IOException e) {

				LOGGER.error(">runRateLimit 报错:"+e.getMessage());
			}
		}
	}

	@Override
	public boolean refreshRateLimtMap() {
		
		List<Map<String, Object>> results = routerepo.refreshRouteRateLimit();

		setRoutlimitMap(results);
		
		return true;
	}
}
