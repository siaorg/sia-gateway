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

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.RouteRibbonHolder;

/**
 * 用于Ribbon的DB读取服务(蓝绿部署)
 * 
 * @author peihua
 **/

@Repository
public class RibbonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RibbonRepository.class);

    private static final String selectRibbonService = "select * from gateway_ribbon_map ";

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate gatewayJdbcTemplate;

    public HashMap<String, RouteRibbonHolder> refreshRibbonRule() {

        try {
            HashMap<String, RouteRibbonHolder> ribbonHolder = new HashMap<String, RouteRibbonHolder>(16);

            LOGGER.info(">RibbonRepository refreshRibbonRule is invoked !");

            List<RouteRibbonHolder> results = gatewayJdbcTemplate.query(selectRibbonService,

                    new BeanPropertyRowMapper<>(RouteRibbonHolder.class));

            for (RouteRibbonHolder holder : results) {
                String routeid = holder.getRouteid();

                routeid = routeid.toUpperCase();

                ribbonHolder.put(routeid, holder);
            }

            return ribbonHolder;

        }
        catch (DataAccessException e) {

            LOGGER.error(">refreshRibbonRule error:{}", e.getMessage());
            return null;
        }

    }

}
