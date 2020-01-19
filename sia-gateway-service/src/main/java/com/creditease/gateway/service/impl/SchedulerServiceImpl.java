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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.service.SchedulerService;
import com.creditease.gateway.service.repository.SchedulerRepository;

/**
 * 
 * @author peihua
 * 
 */

public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    private DiscoveryService zuuldisc;

    @Override
    public int getCounter(String counterKeysuccess) throws Exception {

        return 0;
    }

    /**
     * 取得ZUUL节点信息狀態 service
     * 
     */
    @Override
    public List<ZuulInfo> getZuulList() throws Exception {

        List<ZuulInfo> listdb = schedulerRepository.queryZuulList();
        for (ZuulInfo zuul : listdb) {
            String zuulName = zuul.getZuulGroupName();
            List<String> zuulListEureka = zuuldisc.getServiceList(zuulName);
            zuul.setZuulStatus("Dead");

            for (String zInstanceId : zuulListEureka) {
                if (zuul.getZuulInstanceId().equals(zInstanceId)) {
                    zuul.setZuulStatus("Running");
                }
            }
        }

        return listdb;
    }

    @Override
    public String getStatisicBindRouteIdlist() throws Exception {

        return schedulerRepository.queryRouteIdList();
    }
}
