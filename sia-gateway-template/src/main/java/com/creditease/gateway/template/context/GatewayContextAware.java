package com.creditease.gateway.template.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author peihua
 * 
 */
@Component
public class GatewayContextAware implements ApplicationContextAware {

	private  final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private static Map<String, HashSet<String>> compRouteMap = new ConcurrentHashMap<String, HashSet<String>>();
	
	private GatewayContextAware context;

	public static RestTemplate loadbalanceRestTemplate;

	public static RestTemplate restTemplate;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		loadbalanceRestTemplate = applicationContext.getBean("loadbalanceRestTemplate", RestTemplate.class);
		restTemplate = applicationContext.getBean("restTemplate", RestTemplate.class);
		LOGGER.info(">>>>> loadbalanceRestTemplate is null [{}]", null == loadbalanceRestTemplate);
		LOGGER.info(">>>>> restTemplate is null [{}]", null == restTemplate);
		setContext(applicationContext.getBean(GatewayContextAware.class));
	}

	/**
	 * key: 組件名字 Value: routeID-Set
	 * 
	 */
	public static Map<String, HashSet<String>> getCompRouteMap() {
		return compRouteMap;
	}

	public static void setCompRouteMap(Map<String, HashSet<String>> map) {
		compRouteMap = map;
	}

	public GatewayContextAware getContext() {
		return context;
	}

	public void setContext(GatewayContextAware context) {
		this.context = context;
	}

}
