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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.stereotype.Component;

import com.creditease.gateway.domain.RibbonnRule;
import com.creditease.gateway.ribbon.RibbonRuleFactory;
import com.creditease.gateway.ribbon.RibbonServerSource;
import com.creditease.gateway.ribbon.RibbonUpdaterFactory;
import com.creditease.gateway.ribbon.RibbonUpdaterInterface;
import com.creditease.gateway.ribbon.updater.RibbonUpdaterStatic;
import com.creditease.gateway.ribbon.updater.ZuulListOfServer;
import com.netflix.loadbalancer.Server;

/**
 * ListofserverLocator路由定位器
 * 
 * @author peihua
 */

@Component
public class ZuulListofServerLocator {

    public final static Logger logger = LoggerFactory.getLogger(ZuulListofServerLocator.class);

    private static final String DEFAULTSCHEMA = "http";
    private static final String URLREGEX = ",";
    private static final String PORTSEPRATOR = ":";

    @Autowired
    SpringClientFactory factory;

    @Autowired
    RibbonRuleFactory rulefactry;

    @Autowired
    private RibbonUpdaterFactory locatorfactgory;

    public void initializerListofServer(List<ZuulListOfServer> lofservers) {

        logger.info("> initializerListofServer init >");

        populateListofserverRoute(lofservers);
    }

    public void populateListofserverRoute(List<ZuulListOfServer> lofservers) {

        synchronized (this) {
            for (ZuulListOfServer lofs : lofservers) {
                populateSingleServerRoute(lofs);
            }
        }

    }

    public void populateSingleServerRoute(ZuulListOfServer lofservers) {

        RibbonUpdaterInterface updater = locatorfactgory
                .getLoctorUpdater(RibbonServerSource.LISTOFSERERROUNDROBIN.toString());

        Server[] servArray = buildServerList(lofservers.getServerArray());

        if (null == servArray) {
            return;
        }
        StaticServerList<Server> newServersList = new StaticServerList<>(servArray);

        if ((updater instanceof RibbonUpdaterStatic) && (null != newServersList)) {

            RibbonUpdaterStatic obj = (RibbonUpdaterStatic) ((RibbonUpdaterStatic) updater).initliztion(
                    lofservers.getServiceId(), rulefactry.getIRule(RibbonnRule.ROUNDROUBIN), newServersList);
            obj.setFactory(factory).updateRibbonServerList();
        }
    }

    public Server[] buildServerList(String url) {

        Server[] servers;
        try {
            String[] urls = url.split(URLREGEX);

            servers = new Server[urls.length];

            int i = 0;
            for (String ipport : urls) {

                String[] ipports = ipport.split(PORTSEPRATOR);

                String ip = ipports[0];

                String port = ipports[1];

                servers[i] = new Server(DEFAULTSCHEMA, ip, Integer.parseInt(port));

                i++;
            }
        }
        catch (NumberFormatException e) {

            logger.error(">NumberFormatException>{}", e.getMessage());

            return null;

        }
        catch (Exception e) {

            logger.error(">NumberFormatException>{}", e.getMessage());

            return null;
        }
        return servers;

    }

}
