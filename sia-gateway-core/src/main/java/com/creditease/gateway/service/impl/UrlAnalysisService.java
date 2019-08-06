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

import java.util.HashSet;

import org.springframework.stereotype.Component;
import com.creditease.gateway.cache.LRUCache;
import com.creditease.gateway.domain.UrlRecordAggregate;
import com.creditease.gateway.filter.StatisticFilter;
import com.creditease.gateway.filter.UrlRecord;
import com.creditease.gateway.template.context.GatewayContextAware;

/**
 * URL统计分析
 * 
 * @author peihua
 * 
 * */

@Component
public class UrlAnalysisService  {
	
	private LRUCache<String,UrlRecordAggregate> urlAggregate ;
	
	public LRUCache<String, UrlRecordAggregate> getUrlAggregate() {
		return urlAggregate;
	}

	public void setUrlAggregate(LRUCache<String, UrlRecordAggregate> urlAggregate) {
		this.urlAggregate = urlAggregate;
	}

	public void init(int size)
	{
		urlAggregate = new LRUCache<String,UrlRecordAggregate>(size); 

	}
	
	public void processEvent(UrlRecord r) {
		
		String url  = r.getUrlPath();
		
		UrlRecordAggregate urlaggre = urlAggregate.get(url);
		
		if(null == urlaggre)
		{
			urlaggre = new UrlRecordAggregate(url,r.getInstanceId(),r.getRouteid(),r.getGroupName(),r.getLastInvokeTime());
			urlaggre.increSumCount();
			urlaggre.spanCompute(r.getSpan());
			
			urlAggregate.put(url, urlaggre);
			
		}else
		{
			UrlRecordAggregate agree = urlAggregate.get(url);
			
			agree.increSumCount();
			
			agree.spanCompute(r.getSpan());
			
		}
	}

	public boolean checkRul(String routeid) {

		String compFilterName = StatisticFilter.class.getSimpleName();

		HashSet<String> routidSet = GatewayContextAware.getCompRouteMap().get(compFilterName);

		boolean rst = false;

		if (routidSet != null && routidSet.contains(routeid)) {
			rst = true;
		} 
		return rst;
	}
}
