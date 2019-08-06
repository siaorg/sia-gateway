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


package com.creditease.gateway.topology;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.stereotype.Component;

import com.creditease.gateway.domain.topo.Link;
import com.creditease.gateway.domain.topo.RouteTopo;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.route.locator.ZuulRouteLocator;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
 * 拓扑管理
 * 
 * @author peihua
 * 
 */
@Component
public class TopologyManager {

    protected final static Logger logger = LoggerFactory.getLogger(TopologyManager.class);

    @Autowired
    private SpringClientFactory factory;

    private static final int MAXEXP = 1000;

    @Autowired
    ZuulRouteLocator zuulRouteLocator;

    Map<String, RouteTopo> routeTopoMap = new ConcurrentHashMap<String, RouteTopo>();

    public Map<String, RouteTopo> getRouteTopoMap() {

        return routeTopoMap;
    }

    public void processEvent(Event t) {

        logger.debug(">TopologyManager event routeid:{}", t.getRouteId());

        getForwardURL(t);
        String routeid = t.getRouteId();

        Link linkin = new Link(t.getClientNodes(), t.getInstanceId());

        linkin.setLastRequestTime(t.getStartTime());

        if (null == routeTopoMap.get(routeid)) {

            RouteTopo routetopo = new RouteTopo();
            build(routetopo, t, linkin);
            routeTopoMap.put(routeid, routetopo);
        }
        else {
            RouteTopo routeTopo = routeTopoMap.get(routeid);
            build(routeTopo, t, linkin);
        }
    }

    public void build(RouteTopo routetopo, Event t, Link linkIn) {

        routetopo.updateSpanTime(t.getPath(), (int) t.getSpan(), t.getStartTime());

        if (null != t.getCause()) {
            Throwable cause = t.getCause();
            String value = cause.getCause().getMessage();

            Map<String, String> excpt = routetopo.getExceptionCause();
            if (excpt.size() > MAXEXP) {
                routetopo.getExceptionCause().clear();
            }
            routetopo.getExceptionCause().put("" + t.getStartTime(), value);
        }

        routetopo.setRouteId(t.getRouteId());
        routetopo.setGroupName(t.getGroupName());
        routetopo.getClientNodes().add(t.getClientNodes());
        routetopo.getZuulNodes().add(t.getInstanceId());
       
        routetopo.addLink(linkIn);

        String urlArray = t.getForwardURL();

        if (!StringHelper.isEmpty(urlArray)) {
            String[] array = urlArray.split(";");
            for (String remoteaddr : array) {
                routetopo.getUpstreamNodes().add(remoteaddr);

                Link linkout = new Link(t.getInstanceId(), remoteaddr);
                linkout.setLastRequestTime(t.getStartTime());

                routetopo.addLink(linkout);
            }
        }
    }

    public void getForwardURL(Event t) {

        Route route = zuulRouteLocator.getMatchingRoute(t.getPath());

        if (null == route) {
            return;
        }
        String location = route.getLocation();
        if (location.startsWith("http://")) {
            t.setForwardURL(location);
        }
        else {
            ILoadBalancer balancer = factory.getLoadBalancer(route.getLocation());
            if (null != balancer) {
                List<Server> servers = balancer.getAllServers();

                StringBuffer sbf = new StringBuffer();
                for (Server s : servers) {
                    sbf.append(s.getHostPort() + ";");
                }
                t.setForwardURL(sbf.toString());
            }
        }
    }

}
