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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.creditease.gateway.repository.ZuulRepository;
import com.creditease.gateway.service.impl.UrlAnalysisService;
import com.creditease.gateway.topology.TopologyManager;

/**
 * 上下文管理类
 * 
 * @author peihua
 * 
 * */
@Component
public class GatewayContextMgt implements ApplicationContextAware {

	private static TopologyManager topomgt;
	
	private static ZuulRepository repo;
	
	private static UrlAnalysisService urlser;
	
	public static TopologyManager getTopoMgt() {
		return topomgt;
	}
	
	public static ZuulRepository getZuulRepo() {
		return repo;
	}
	
	public static UrlAnalysisService getUrlAnalysis() {
		return urlser;
	}
	
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	
    	topomgt = applicationContext.getBean(TopologyManager.class);
    	
    	repo = applicationContext.getBean(ZuulRepository.class);
    	
    	urlser = applicationContext.getBean(UrlAnalysisService.class);
    }
}
