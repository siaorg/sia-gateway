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

import java.util.Map;
import com.creditease.gateway.service.StatisticService;
import com.google.common.collect.Maps;

/**
 * 统计组件 
 * 
 * @author peihua
 * 
 * */

public class StatisticServiceImpl implements StatisticService {
	
	private Map<String, Integer> map = Maps.newConcurrentMap();
	
	
	/**
	 * 计数操作
	 * 
	 * */
	@Override
	public void increament(String counterName)
	{
		Integer value = map.get(counterName);
		
		if(value!=null)
		{
			Integer added = Integer.sum(value.intValue(), 1);
			
			map.put(counterName, added);
			
		}else
		{
			map.put(counterName, 1);
		}
	}
	
	/**
	 * 获得計數
	 * 
	 * */
	@Override
	public int getCounte(String counterName)
	{
		try {
			Integer value = map.get(counterName);
			
			return value.intValue();
			
		} catch (Exception e) {
			
			return 0;
		}
	}
}
