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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.service.AuthService;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * 全局黑名单限制过滤器
 * 
 * @author peihua
 */

@Component
public class GlobalBlackListFilter extends AbstractGatewayFilter {

    @Autowired
    protected AuthService bws;

    @Override
    public void process(RequestContext ctx, String routeid) {

        bws.doGlobalbwListFilter(ctx, routeid);
    }

    @Override
    public String getFilterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return 99;
    }

    @Override
    public boolean isEnabled() {

        RequestContext ctx = RequestContext.getCurrentContext();
        Object success = ctx.get("isSuccess");
        return success == null ? true : Boolean.parseBoolean(success.toString());
    }

    @Override
    public String getCompName() {

        return "COMMON-GWBLIST";
    }

}
