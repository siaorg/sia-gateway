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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.domain.RouteRibbonHolder;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.repository.RibbonRepository;
import com.creditease.gateway.ribbon.context.RibbonFilterContextHolder;
import com.creditease.gateway.service.RouteRibbonService;
import com.netflix.zuul.context.RequestContext;

/**
 * 蓝绿部署服务
 * 
 * @author peihua
 * 
 * */

public class RouteRibbonServiceImpl implements RouteRibbonService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteRibbonServiceImpl.class);
	
	private static final String VERSION = "version";
	
	private Object mutex = new Object();
	
	public Map<String, RouteRibbonHolder> ribbonHolder = new HashMap<String, RouteRibbonHolder>();

	@Override
	public Map<String, RouteRibbonHolder> getRibbonHolder() {
		return ribbonHolder;
	}

	@Autowired
	RibbonRepository ribbonresp;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setRibbonHolder(HashMap<String, RouteRibbonHolder> ribbonholder) {
		
		synchronized(mutex)
		{
			ribbonHolder.clear();
			this.ribbonHolder = (HashMap) ribbonholder.clone();
		}

	}
	
	@Override
	public boolean routeRibbonRefresh() {
		
		try {
			LOGGER.info("> SwitchRouteController invoked!");

			/**
			 * 更新数据库
			 * */
			HashMap<String, RouteRibbonHolder> result = ribbonresp.refreshRibbonRule();

			setRibbonHolder(result);
			
			return true;
		} catch (Exception e) {

			LOGGER.error("> routeRibbonRefresh: "+e.getMessage());
			
			return false;
		}
		
				
	}

	@Override
	public void runRibbon(RequestContext ctx, String routeid) {
		
		RouteRibbonHolder routeribbonholder;
		
		synchronized(mutex)
		{
			 routeid = routeid.toUpperCase();
			
			 routeribbonholder = ribbonHolder.get(routeid);		
		}
		
		String strategy = routeribbonholder.getStrategy();
		
		switch(strategy)
		{
	     	/***
		    * 
		    * 藍綠部署
		    * 
		    * */
		   case "Greenblue":
			
			   String crrentVersion = routeribbonholder.getCurrentVersion();

			   LOGGER.info(">routeid:[{}],crrentVersion:[{}]", routeid,	crrentVersion);
			   if(crrentVersion!=null&&!StringHelper.isEmpty(crrentVersion))
			   {
					RibbonFilterContextHolder.getCurrentContext().add("version", crrentVersion);
			   }
			   
			   break;
			   
	       /***
		   * 
		   * 金丝雀部署
		   * 
		   * */
		   case "Canary":
			   
			   HttpServletRequest request = ctx.getRequest();
			   
			   String versionValue = null;
			   /**
			    * step1:取HTTP头信息参数
			    * */
			   if(StringHelper.isEmpty(versionValue))
			   {
				   String context = routeribbonholder.getContext();
				   
				   versionValue = request.getHeader(context);
			   }
			   
			   /**
			    * step2:取HTTP头信息参数
			    * */
			   if(StringHelper.isEmpty(versionValue))
			   {
				   versionValue = request.getHeader(VERSION);
			   }
			   
			   
			   if (!StringHelper.isEmpty(versionValue))
			   {
					LOGGER.info(">runRibbon, Version Value is " + versionValue);
					
					if(versionValue!=null&&!StringHelper.isEmpty(versionValue)) 
					{	
						RibbonFilterContextHolder.getCurrentContext().add("version", versionValue);
					}
			   }				   
			   break;
		
		  default:
		      
			  LOGGER.info("> no such strategy:"+strategy);
		}			
	}
}
