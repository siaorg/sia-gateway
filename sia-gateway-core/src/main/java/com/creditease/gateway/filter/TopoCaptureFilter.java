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

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.service.impl.UrlAnalysisService;
import com.creditease.gateway.topology.Event;
import com.creditease.gateway.topology.intercept.ThreadInterceptProcessor;
import com.netflix.zuul.context.RequestContext;

/**
 *  拓扑管理
 *  @author peihua
 */

@Component
public class TopoCaptureFilter extends AbstractGatewayFilter {

    @Autowired
    UrlAnalysisService urlser;

    private static final String ERRORPATH = "/error";

    @Override
    public void process(RequestContext ctx, String routeid) {

        Event t = ThreadInterceptProcessor.getContext().getEvent();
        try {

            String path = ctx.getRequest().getRequestURI();
            if (ERRORPATH.equals(path)) {
                Throwable cause = ctx.getThrowable();

                t.setCause(cause);
                t.setPath(super.getRealPath(ctx));
            }
            else {
                t.setPath(path);
            }

            t.setEndTime(System.currentTimeMillis());
            t.setSpan(t.getEndTime() - t.getStartTime());

            t.emit();
            if (urlser.checkRul(routeid)) {
                new UrlRecord(t).emit();
            }
        }
        catch (Exception e) {
            logger.error(">TopoCaptureFilter:"+e.getMessage());
        }

    }

    @Override
    public String getFilterType() {

        return POST_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return 3;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

    @Override
    public String getCompName() {

        return "COMMON-TOPO";
    }

}
