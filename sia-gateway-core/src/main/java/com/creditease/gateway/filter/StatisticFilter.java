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


package com.creditease.gateway.filter;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.helper.DateTimeHelper;
import com.creditease.gateway.service.StatisticService;
import com.netflix.zuul.context.RequestContext;

/**
 * 网关统计功能
 * 
 * @author peihua
 */
@Component
public class StatisticFilter extends AbstractGatewayFilter {

    @Value("${zuul.statistic.enabled}")
    private String statisticEnabled;

    private String dateFormat = "yyyy-MM-dd HH";

    @Autowired
    protected StatisticService sts;

    @Override
    public void process(RequestContext ctx, String routeid) {

        logger.debug("开始计数统计处理");

        /**
         * 統計當前日期
         */
        String date = DateTimeHelper.dateFormat(new Date(), dateFormat);

        /**
         * 网关流量计数统计
         */
        sts.increment(GatewayConstant.APISUMCOUNT + "-" + date);

        logger.debug("网关计数:{}", sts.getCount(GatewayConstant.APISUMCOUNT + "-" + date));

    }

    @Override
    public String getFilterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public boolean isEnabled() {

        return Boolean.parseBoolean(statisticEnabled);
    }

    @Override
    public String getCompName() {

        return "COMMON-STATISTIC";
    }

}
