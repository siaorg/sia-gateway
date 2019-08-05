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


package com.creditease.gateway.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.domain.RouteObj;
import com.creditease.gateway.domain.RouteStrategy;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.ribbon.updater.ZuulListOfServer;

/**
 * 用于Route的DB读取服务
 * 
 * @author peihua
 **/

@Repository
public class RouteRepository {

    @Value("${spring.application.name}")
    private String groupName;

    @Value("${zuul.super.enabled}")
    private String superorNot;

    private List<ZuulListOfServer> lofservers = new CopyOnWriteArrayList<ZuulListOfServer>();

    public List<ZuulListOfServer> getLofservers() {

        return lofservers;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRepository.class);

    /**
     * 查詢ZUUL-route路由配置係你先
     */
    private static final String SELECTROUTE = "select * from gateway_route where enabled = true ";

    /**
     * 查詢所有路由组件绑定信息
     */
    private static final String SELECTROUTECOMP = "select * from gateway_component ";

    /**
     * 查詢所有路由组件绑定限流
     */
    private static final String SELECTROUTELIMITRATE = "select * from gateway_route_ratelimit ";

    enum RouteStatus {
        /**
         * EDIT: 路由编辑状态 ONLIE:路由发布 DOWNLINE： 路由下线
         */
        EDIT, ONLINE, DOWNLINE
    }

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate gatewayJdbcTemplate;

    /***
     * 
     * 数据中加载路由信息
     * 
     */
    public Map<String, ZuulRoute> locateRoutesFromDB() {

        LOGGER.info("> 从数据库加载路由信息到网关...");

        lofservers.clear();

        Map<String, ZuulRoute> routes = new LinkedHashMap<>();

        List<RouteObj> results = gatewayJdbcTemplate.query(SELECTROUTE,

                new BeanPropertyRowMapper<>(RouteObj.class));

        for (RouteObj result : results) {

            /**
             * 第一层过滤：根据zuulGroupName过滤
             * 
             */
            String zuulGroupName = result.getZuulGroupName();

            if (groupName.toLowerCase().equals(zuulGroupName.toLowerCase()) == false) {
                /**
                 * 如果是超級网关，不做过滤
                 */
                if (GatewayConstant.API_GATEWAY_CORE.equals(groupName) && (Boolean.parseBoolean(superorNot))) {
                    LOGGER.debug("> 超级网关加载, routeid:" + result.getId());
                }
                else {
                    LOGGER.debug("> 非本网关路由, routeid:" + result.getId());
                    continue;
                }
            }

            /**
             * 第二层过滤：RouteStatus必须是发布状态才会加载进来
             * 
             **/
            if (StringHelper.isEmpty(result.getPath()) || StringHelper.isEmpty(result.getRouteStatus())
                    || (!result.getRouteStatus().equals(RouteStatus.ONLINE.toString()))) {

                LOGGER.debug("###############" + "非有效路由" + "################");
                LOGGER.debug("> 路由:" + result.getId() + " Status:" + result.getRouteStatus());
                LOGGER.debug("> 路由:" + result.getId() + " path:" + result.getPath());
                continue;
            }

            /**
             * 第三层过滤：将策略是LISTOFSERVER的路由导出
             */
            if (result.getStrategy().equals(RouteStrategy.LISTOFSERVER.toString())) {

                ZuulListOfServer lofserver = new ZuulListOfServer(result.getServiceId(), result.getUrl());
                lofservers.add(lofserver);

                result.setUrl(null);
            }

            /**
             * Fix serviceid&url Conflict issue
             */
            if (result.getStrategy().equals(RouteStrategy.SERVICEID.toString())) {
                result.setUrl(null);
            }

            ZuulRoute zuulRoute = new ZuulRoute();

            try {
                BeanUtils.copyProperties(result, zuulRoute);

            }
            catch (Exception e) {

                new GatewayException(ExceptionType.CoreException, e);
                LOGGER.error(">加载路由异常，Exception is:", e.getMessage());
            }

            routes.put(zuulRoute.getPath(), zuulRoute);
        }

        return routes;
    }

    /***
     * 
     * 加載組件信息
     * 
     */
    public List<CompInfo> selectRouteid() {

        List<CompInfo> results = gatewayJdbcTemplate.query(SELECTROUTECOMP,

                new BeanPropertyRowMapper<>(CompInfo.class));
        return results;
    }

    /***
     * 
     * 刷新路由限流配置
     * 
     */
    public List<Map<String, Object>> refreshRouteRateLimit() {

        try {

            LOGGER.info(">RouteRepository refreshRouteRateLimit is invoked !");

            List<Map<String, Object>> results = gatewayJdbcTemplate.queryForList(SELECTROUTELIMITRATE);

            LOGGER.info(">RouteRepository results：!" + results);

            return results;

        }
        catch (DataAccessException e) {

            LOGGER.error(">refreshRibbonRule error:" + e.getCause());
            return null;
        }

    }

}
