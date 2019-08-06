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


package com.creditease.gateway.filter.abs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.template.context.GatewayContextAware;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * 过滤器抽象类
 * 
 * @author peihua
 * 
 */
public abstract class AbstractGatewayFilter extends ZuulFilter {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractGatewayFilter.class);

    private static final String COMMONFILTERFLAG = "COMMON-";

    private static final String PROXY_KEY = "proxy";

    private static final String ERRORPATH = "/error";

    @Override
    public String filterType() {

        return getFilterType();
    }

    @Override
    public int filterOrder() {

        return getFilterOrder();
    }

    @Override
    public boolean shouldFilter() {

        return isEnabled();
    }

    @Override
    public Object run() {

        String routeid = null;

        try {
            RequestContext ctx = RequestContext.getCurrentContext();

            routeid = (String) ctx.get(PROXY_KEY);

            if (checkRul(routeid)) {

                process(ctx, routeid);

            }
            return null;

        }
        catch (Exception e) {

            logger.error(">处理Routid：{}，出现异常：{}", routeid, e.getMessage());
            invokeException(e.getMessage(), 0, e.getMessage());
        }
        return null;
    }

    /***
     * 
     * 路由&組件 Bind規則查看
     * 
     */
    protected boolean checkRul(String routeid) {

        String compFilterName = this.getClass().getSimpleName();

        int index = getCompName().indexOf(COMMONFILTERFLAG);

        if (index > 0 || index == 0) {
            logger.debug(">>>The Filter Scope is Common , route id is ::" + routeid);
            return true;
        }

        HashSet<String> routidSet = GatewayContextAware.getCompRouteMap().get(compFilterName);

        boolean rst = false;

        if (routidSet != null && routidSet.contains(routeid)) {
            rst = true;
        }
        return rst;
    }

    public void invokeException(String sMessage, int nStatusCode, String errorCause) {

        try {
            Class<?> cls = Class.forName("com.creditease.gateway.excpetion.GatewayException");
            Method setMethod = cls.getDeclaredMethod("GatewayException", String.class, int.class, String.class);

            setMethod.invoke(cls.newInstance(), sMessage, nStatusCode, errorCause);

        }
        catch (Exception e) {
            logger.error(">>>> 预警异常：：" + e.getMessage());
        }
    }

    /**
     * 取得Filter的类型
     * 
     * @param null
     * @return String
     * 
     */
    public abstract String getFilterType();

    /**
     * 取得Filter的Order
     * 
     * @param null
     * @return int
     * 
     */
    public abstract int getFilterOrder();

    /**
     * Filter的使能设置
     * 
     * @param null
     * @return boolean
     * 
     */
    public abstract boolean isEnabled();

    /**
     * Filter的业务处理逻辑
     * 
     * @param ctx
     * @param routeid
     * @return void
     * 
     */
    public abstract void process(RequestContext ctx, String routeid);

    /**
     * Filter的名称
     * 
     * @param null
     * @return String
     * 
     */
    public abstract String getCompName();

    public String getRealPath(RequestContext ctx) {

        HttpServletRequest request = ctx.getRequest();
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        try {
            Field request1 = requestClass.getDeclaredField("request");
            request1.setAccessible(true);
            Object o = request1.get(request);
            Field coyoteRequest = o.getClass().getDeclaredField("request");
            coyoteRequest.setAccessible(true);
            Object o1 = coyoteRequest.get(o);
            Request rqst = (org.apache.catalina.connector.Request) o1;

            String realpath = rqst.getRequestPathMB().toString();
            logger.error(">>>> remote error realpath:" + realpath);
            return realpath;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ERRORPATH;
    }
}
