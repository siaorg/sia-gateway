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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.http.wrapper.HttpServletRequestWrapper;
import com.creditease.gateway.http.wrapper.HttpServletResponseWrapper;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.api.BaseMqProducer;
import com.creditease.gateway.message.api.domain.MqMessage;
import com.creditease.gateway.service.LogService;
import com.netflix.zuul.context.RequestContext;

/**
 * 日志转发KAFKA服务
 *
 * @author peihua
 */

public class LogServiceImpl implements LogService {

    public static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired
    private BaseMqProducer producer;

    @Override
    public void recordRequest(RequestContext ctx, String routeId) {

        try {
            HttpServletRequest request = ctx.getRequest();

            HttpServletRequestWrapper requestWrapper;
            if (request instanceof HttpServletRequestWrapper) {
                requestWrapper = (HttpServletRequestWrapper) request;
            } else {
                requestWrapper = new HttpServletRequestWrapper(request);
            }

            Map<String, Object> requestMap = new LinkedHashMap<>(5);
            requestMap.put("logtime", System.currentTimeMillis());
            requestMap.put("routeid", routeId);
            requestMap.put("uri", request.getRequestURI());
            requestMap.put("parameter", JsonHelper.toString(request.getParameterMap()));
            requestMap.put("body", requestWrapper.getStringBody());

            producer.send(new MqMessage(routeId, JsonHelper.toString(requestMap)));
        } catch (IOException e) {
            LOGGER.error(">>>recordRequest error", e);
        }
    }

    @Override
    public void recordResponse(RequestContext ctx, String routeId) {

        try {
            HttpServletResponse response = ctx.getResponse();

            HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
            String responseBody = responseWrapper.getResponseData(responseWrapper.getCharacterEncoding());

            Map<String, Object> responseMap = new LinkedHashMap<>(3);
            responseMap.put("logtime", System.currentTimeMillis());
            responseMap.put("routeid", routeId);
            responseMap.put("body", responseBody);

            producer.send(new MqMessage(routeId, JsonHelper.toString(responseMap)));
        } catch (Exception e) {
            LOGGER.error(">recordResponse error", e);
        }
    }
}
