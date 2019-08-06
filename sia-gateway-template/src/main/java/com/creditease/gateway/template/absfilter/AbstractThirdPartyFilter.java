package com.creditease.gateway.template.absfilter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.annotation.FilterAnnotation;
import com.creditease.gateway.annotation.ReflectionHelper;
import com.creditease.gateway.template.context.GatewayContextAware;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * 第三方业务过滤器抽象类
 * 
 * @author peihua
 * 
 */

public abstract class AbstractThirdPartyFilter extends ZuulFilter {

    protected final static Logger logger = LoggerFactory.getLogger(AbstractThirdPartyFilter.class);

    private static final String PROXY_KEY = "proxy";

    Map<String, Object> mAnnoInfos = new HashMap<String, Object>();

    @Override
    public String filterType() {

        mAnnoInfos = ReflectionHelper.getAnnotationAllFieldValues(getClassValue(), FilterAnnotation.class);

        for (String key : mAnnoInfos.keySet()) {

            logger.debug("key:{}", key);

            logger.debug("value:{}", mAnnoInfos.get(key));
        }

        String typevalue = (String) mAnnoInfos.get("type");

        return typevalue;
    }

    @Override
    public int filterOrder() {

        int order = Integer.parseInt(String.valueOf(mAnnoInfos.get("order")));
        return order;
    }

    @Override
    public boolean shouldFilter() {

        boolean isenabled = Boolean.parseBoolean(String.valueOf(mAnnoInfos.get("isenabled")));

        return isenabled;
    }

    @Override
    public Object run() {

        String routeid = null;

        try {
            RequestContext ctx = RequestContext.getCurrentContext();

            routeid = (String) ctx.get(PROXY_KEY);

            logger.debug(">开始处理> routeid:{}", routeid);

            if (checkRul(routeid)) {

                process(ctx, routeid);

            }
            else {
                logger.info("AbstractGatewayFilter routeid:{}is not bind to this component.", routeid);
            }

            return null;

        }
        catch (RuntimeException e) {

            logger.error("处理Routid：{}出现异常：{}", routeid, e.getMessage());

            invokeException(e.getMessage(), 0, e.getMessage());

        }
        catch (Exception e) {

            logger.error("处理Routid：{}出现异常：{}", routeid, e.getMessage());

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

        HashSet<String> routidSet = GatewayContextAware.getCompRouteMap().get(compFilterName);

        boolean rst = false;

        if (routidSet != null && routidSet.contains(routeid)) {

            rst = true;

            logger.debug("Route bind to component success. checkRul rst:{}", rst);

        }
        else {
            logger.debug("Routeid:{} is Not bind to compFilterName:{}", routeid, compFilterName);
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

            logger.info("预警异常：{}", e.getMessage());

        }
    }

    /**
     * getClassValue
     *
     * @param NULL
     * @return Class
     */
    public abstract Class<?> getClassValue();

    /**
     * process run filter Logic
     *
     * @param ctx,
     * @param routeid
     * 
     * @return void
     */
    public abstract void process(RequestContext ctx, String routeid);

}
