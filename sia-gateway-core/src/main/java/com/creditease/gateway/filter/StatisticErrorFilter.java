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

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.helper.DateTimeHelper;
import com.creditease.gateway.service.StatisticService;
import com.creditease.gateway.service.impl.UrlAnalysisService;
import com.creditease.gateway.topology.Event;
import com.creditease.gateway.topology.intercept.ThreadInterceptProcessor;
import com.netflix.zuul.context.RequestContext;

/**
 * 统一异常处理Filter, 统计错误统计个数
 * 
 * @author peihua
 */

@Component
public class StatisticErrorFilter extends AbstractGatewayFilter {

    @Value("${zuul.statistic.enabled}")
    private String statisticEnabled;

    @Value("${spring.application.name}")
    private String sagGroupName;

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    private String dateFormat = "yyyy-MM-dd HH";

    @Autowired
    protected StatisticService sts;

    @Autowired
    UrlAnalysisService urlser;

    @Override
    public void process(RequestContext ctx, String routeid) {

        Throwable throwable = ctx.getThrowable();

        logger.debug("开始异常统计处理");

        ctx.set("异常返回码:", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("异常返回信息:", throwable.getCause());

        String date = DateTimeHelper.dateFormat(new Date(), dateFormat);

        /**
         * 整体异常流量计数统计
         */
        sts.increment(GatewayConstant.APIFAILCOUNT + "-" + date);

        logger.debug("网关错误计数:{}", sts.getCount(GatewayConstant.APIFAILCOUNT + "-" + date));

        /**
         * 绑定统计组件后执行UrlAnalysisService
         * 
         */
        if (urlser.checkRul(routeid)) {
            Event t = ThreadInterceptProcessor.getContext().getEvent();
            // 开始统计详细URL调用信息
            String path = super.getRealPath(ctx);
            long startTime = t.getStartTime();
            long endTime = System.currentTimeMillis();
            long span = endTime - startTime;
            new UrlRecord(sagGroupName, instanceId, routeid, path, span, endTime).emit();
        }
    }

    @Override
    public String getFilterType() {

        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return 0;
    }

    @Override
    public boolean isEnabled() {

        return Boolean.parseBoolean(statisticEnabled);
    }

    @Override
    public String getCompName() {

        return "COMMON-ERROR";
    }

}
