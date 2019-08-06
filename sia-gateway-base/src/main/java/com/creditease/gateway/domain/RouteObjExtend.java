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


package com.creditease.gateway.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由界面顯示
 * 
 * @author peihua
 **/

public class RouteObjExtend {

    private String routeid;
	/**
	 * 
	 * ZUUL會用這個字段
	 * 
     * The ID of the route (the same as its map key by default).
     */
    private String id;

    private String path;

    private String serviceId;

    private String url;

    private boolean stripPrefix;

    private Boolean retryable;

    private String apiName;

    private Boolean enabled;
    
    private String zuulGroupName;

	private String routeStatus;

    private String strategy;

    private List<String> component;


	public RouteObjExtend(RouteObj obj , List<String> comp)
    {
    	id = obj.getId();
    	
    	routeid = obj.getRouteid();
    			
    	path=  obj.getPath();
    	
    	serviceId = obj.getServiceId();
    	
    	url=obj.getUrl();
    	
    	retryable = obj.getRetryable();
    	
    	apiName = obj.getApiName();
    	
    	enabled = obj.getEnabled();

    	zuulGroupName = obj.getZuulGroupName();
    	
    	routeStatus =  obj.getRouteStatus();
    	
    	strategy =  obj.getStrategy();
    	
    	component = new ArrayList<String>();
    	
    	stripPrefix = obj.isStripPrefix();
    	
    	if(comp!=null)
    	{
    		component.addAll(comp);
    	}
    	
    }

    public List<String> getComponent() {
		return component;
	}


	public void setComponent(List<String> component) {
		this.component = component;
	}
	
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString()
    {
		return apiName;
    	
    }
    
    public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
    
    public String getZuulGroupName() {
		return zuulGroupName;
	}

	public void setZuulGroupName(String zuulGroupName) {
		this.zuulGroupName = zuulGroupName;
	}

	public String getRouteStatus() {
		return routeStatus;
	}

	public void setRouteStatus(String routeStatus) {
		this.routeStatus = routeStatus;
	}
	

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String getRouteid() {
		return routeid;
	}

	public void setRouteid(String routeid) {
		this.routeid = routeid;
	}
	  

	
}
