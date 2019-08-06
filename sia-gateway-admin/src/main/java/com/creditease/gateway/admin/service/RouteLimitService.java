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


package com.creditease.gateway.admin.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.message.Message;

/**
 * 限流服务
 * 
 * @author peihua
 * 
 * 
 */

@Service
public class RouteLimitService extends BaseAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteLimitService.class);

    public String queryRouteLimit(String routeid) throws Exception {

        String limit = compDBRepository.queryroutelimit(routeid);

        return limit;
    }

    public void saveRouteLimit(String routeid, String ratelimit) throws Exception {

        try {
            // 存在执行update,不存在执行insert
            if (compDBRepository.isExist(routeid)) {

                compDBRepository.updateRouteLimit(routeid, ratelimit);
            }
            else {

                compDBRepository.addRouteRatelimit(routeid, ratelimit);
            }

            /**
             * 通知GW刷新
             */
            List<String> zuulList = zuulDiscovery.getServiceList(super.getZuulGroupName(routeid));

            /**
             *
             * Step3: 调用所有ZUUL-Instnace的/refresh接口同步DB到LocalCache完成同步
             */
            for (String path : zuulList) {
                String url = "http://" + path;
                String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.RRL.getValue(), new Message());

                LOGGER.info(" saveRouteLimit rst:{}", result);
            }
        }
        catch (Exception e) {
            LOGGER.error("saveRouteLimit error, please check!{}", e.getMessage());
            throw e;
        }

    }

}
