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


package com.creditease.gateway.filter.common;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.creditease.gateway.filter.abs.AbstractGatewayFilter;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.service.AuthService;
import com.netflix.zuul.context.RequestContext;

/**
 * 安全认证AccessFilter
 * 
 * @author peihua
 * 
 */
@Component
public class AuthAuthenticateFilter extends AbstractGatewayFilter {

    @Autowired
    AuthService authservice;

    @Override
    public void process(RequestContext ctx, String routeid) {

        logger.debug("开始安全认证服务,routeid:{}", routeid);

        HttpServletRequest request = ctx.getRequest();
        logger.info("send {} request to {}", request.getMethod(), request.getRequestURL().toString());

        String accessToken = request.getHeader("Authorization");

        int returncode = authservice.checkAuthToken(accessToken, routeid);

        switch (returncode) {

            case 401:
                logger.info(">认证不通过，routeid:{}", routeid);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(ResponseCode.LIMIT_ERROR_CODE.getCode());
                ctx.setResponseBody("Authorization token is empty! ");

                break;

            case 402:
                logger.info(">>>认证不通过，routeid：" + routeid);
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(ResponseCode.TOKEN_TIMEOUT_CODE.getCode());
                ctx.setResponseBody("Authorization token is wrong or expired! ");

                break;

            case 200:

                logger.info(">认证通过，routeid：{}", routeid);
                break;

            default:
                logger.info(">returncode not match：{}", returncode);
                break;
        }

        return;
    }

    @Override
    public String getFilterType() {

        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int getFilterOrder() {

        return 200;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

    @Override
    public String getCompName() {

        return "AUTH";
    }

}
