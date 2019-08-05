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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.filter.dynamic.GatewayClassLoaderFactory;
import com.creditease.gateway.helper.IoHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.repository.RouteRepository;
import com.creditease.gateway.service.RouteOptService;
import com.creditease.gateway.template.context.GatewayContextAware;

/**
 * 路由组件服务
 * 
 * @author peihua
 * 
 * */

public class RouteOptServiceImpl implements RouteOptService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteOptServiceImpl.class);
	
    @Autowired
	RouteRepository routeRepository;
    
	@Override
	public boolean routeBind() {
    	
    	try {
			List<CompInfo> compList = routeRepository.selectRouteid();
			
			Map<String,HashSet<String>> compRouteMap = GatewayContextAware.getCompRouteMap();
			
			compRouteMap.clear();
			
			LOGGER.debug("> routeBind step1:"+compRouteMap);
			
			for(CompInfo compinfo:compList)
			{
				String routeidArray = compinfo.getRouteidList();
				
				if(!StringHelper.isEmpty(routeidArray))
				{
					String [] array = routeidArray.split(";");
					
					List<String> list = Arrays.asList(array);
					
					HashSet<String> routidSet = new HashSet<String>();
					
					for(String routeid:  list)
					{
						routidSet.add(routeid);
					}
					
					compRouteMap.put(compinfo.getCompFilterName(), routidSet);	
				}
				
				
			}
			
			LOGGER.debug("> routeBind step2:"+compRouteMap);
			
			for(String key:compRouteMap.keySet())
			{
				LOGGER.debug("> routeBind comp:"+key);
				
				HashSet<String> set = compRouteMap.get(key);
				
				Object[] array= set.toArray();
				
				for(Object value: array)
				{
					LOGGER.debug("##### routeid:"+value);
				}
			}
			
		} catch (Exception e) {
			
			LOGGER.error(">路由组件绑定服务错误："+e.getMessage());
			
			return false;
		}
    	
    	return true;
    
	}

	@Override
	public boolean removeComp(String filterName) {
		
		try {
			boolean rst = GatewayClassLoaderFactory.instance().removeFilterComponent(filterName);
			
			Map<String, File> filterPathMap  = GatewayClassLoaderFactory.instance().getFilterPathMap();
			
			File file = filterPathMap.get(filterName);
			
			if(file!= null)
			{
				IoHelper.deleteFile(file.getAbsolutePath());
			}
			
			return rst;
			
		} catch (Exception e) {
			
			LOGGER.error("> Exception:"+e.getCause());
		}
		return false;
	}

}
