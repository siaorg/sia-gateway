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


package com.creditease.gateway.route.locator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.localcache.LocalCacheManager;
import com.creditease.gateway.repository.RouteRepository;

/**
 * 自定义Locator路由定位器
 * 
 * @author peihua
 * 
 * 
 **/

public class ZuulRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    public final static Logger logger = LoggerFactory.getLogger(ZuulRouteLocator.class);

    private ZuulProperties properties;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    ZuulListofServerLocator listofserverLocator;

    private StringBuffer routeBuffer;

    public StringBuffer getRouteBuffer() {

        return routeBuffer;
    }

    private AtomicBoolean refreshCalled = new AtomicBoolean(false);

    public ZuulRouteLocator(String servletPath, ZuulProperties properties) {

        super(servletPath, properties);
        this.properties = properties;

        logger.debug("servletPath:{}", servletPath);
    }

    @Override
    public void refresh() {

        logger.debug("RefreshableRouteLocator refresh is invoked !!!");

        doRefresh();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ZuulRoute> locateRoutes() {

        try {
            logger.debug(">locateRoutes is invoked >!!!");
            LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
            /**
             * Step1: 从application.properties中加载路由信息
             */
            routesMap.putAll(super.locateRoutes());
            /**
             * Step2: 从Cache加载Load路由信息
             */
            LinkedHashMap<String, ZuulRoute> routesMapfromCache = (LinkedHashMap<String, ZuulRoute>) LocalCacheManager.cacheMgr
                    .get(GatewayConstant.routeL1CacheKey);

            if ((routesMapfromCache != null) && (refreshCalled.get() == false)) {
                logger.debug(">Load RouteMap from local cache..");
                routesMap.putAll(routesMapfromCache);
            }
            else {
                logger.info(">Load RouteMap from database..");
                /**
                 * Step3: Cache missing,如果routeLfdbEnabled为True=》从DB加载路由信息
                 */
                Map<String, ZuulRoute> routeMapFromDB = routeRepository.locateRoutesFromDB();

                // 在Cache中创建key
                if (!LocalCacheManager.cacheMgr.exists(GatewayConstant.routeL1CacheKey)) {
                    LocalCacheManager.cacheMgr.enableL1Cache(GatewayConstant.routeL1CacheKey);
                }
                else {
                    // 加載DB中Route信息前需要先清空KEY对应Route的缓存
                    LocalCacheManager.cacheMgr.del(GatewayConstant.routeL1CacheKey);
                }
                // 同步DB-Route信息到Cache中
                LocalCacheManager.cacheMgr.put(GatewayConstant.routeL1CacheKey, routeMapFromDB);
                routesMap.putAll(routeMapFromDB);

                // 更新listofserverLocator中的
                if (routeRepository.getLofservers().size() > 0) {
                    listofserverLocator.populateListofserverRoute(routeRepository.getLofservers());
                }

                // 最后：將refreshCalled标志位重置为Flase
                if (refreshCalled.get()) {
                    refreshCalled.compareAndSet(true, false);
                }
                logger.info("cache value: " + (LinkedHashMap<String, ZuulRoute>) LocalCacheManager.cacheMgr
                        .get(GatewayConstant.routeL1CacheKey));
            }
            /**
             * Step4: 根据routesMap优化输出路由映射信息
             */
            LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
            routeBuffer = new StringBuffer();
            for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
                String path = entry.getKey();
                // Prepend with slash if not already present.
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                if (StringUtils.hasText(this.properties.getPrefix())) {
                    path = this.properties.getPrefix() + path;
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                }
                values.put(path, entry.getValue());
                if (logger.isDebugEnabled()) {
                    printRoute(path, entry);
                }
                routeBuffer.append(entry.getValue().getId());
                routeBuffer.append(";");
            }
            if (logger.isInfoEnabled()) {
                logger.info(">locateRoutes id List:{}", routeBuffer.toString());
            }
            return values;

        }
        catch (Exception e) {

            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

    public AtomicBoolean getRefreshCalled() {

        return refreshCalled;
    }

    public void setRefreshCalled(AtomicBoolean refreshCalled) {

        this.refreshCalled = refreshCalled;
    }

    private void printRoute(String path, Map.Entry<String, ZuulRoute> entry) {

        logger.info("------------------ start --------------------------- ");

        logger.info("path is" + path);

        logger.info("id is: " + entry.getValue().getId());

        logger.info("serviceId is:" + entry.getValue().getServiceId());

        logger.info("url is:" + entry.getValue().getUrl());

        logger.info("getRoute is: " + entry.getValue().getRoute(null));

        logger.info("------------------ end --------------------------- ");

    }

    public static void main(String args[]) {

        PathMatcher pathMatcher = new AntPathMatcher();

        String adjustedPath = "/downstream/test/p44444444444";

        String pattern = "/mutiRe/**";

        if (pathMatcher.match(pattern, adjustedPath)) {
            System.out.println("ok");
        }
        else {
            System.out.println("no");
        }
    }

}
