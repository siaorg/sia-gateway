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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.creditease.gateway.cache.FilterCacheManager;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.service.AuthService;
import com.creditease.gateway.service.wblist.FilterbwListStrategyManager;
import com.netflix.zuul.context.RequestContext;

/**
 * 路由（全局）黑白名单服务
 * 
 * @author peihua
 * 
 */

public class AuthServiceImpl implements AuthService {

    public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private FilterCacheManager filterCacheManager;

    @Autowired
    protected FilterbwListStrategyManager dispatch;

    @Autowired
    protected ZuulHandler handler;

    @Value("${spring.gateway.admin.name}")
    private String adminName;

    @Autowired
    DiscoveryService zuuldisc;

    @Override
    public void doGlobalbwListFilter(RequestContext ctx, String routeid) {

        BwfilterObj obj = filterCacheManager.getRouteId(GatewayConstant.GLOBALBLACKID);

        if (obj != null) {

            /**
             * step 1：获取当前过滤策略
             * 
             */
            String strategy = obj.getStrategy();
            /**
             * step 2：执行对应process
             */
            dispatch.doProcess(strategy, ctx, obj);
        }

        Boolean ifPass = (Boolean) ctx.get(GatewayConstant.BWSTRATEGY.IP.getValue());

        ctx.set("isSuccess", ifPass);

    }

    @Override
    public void doRoutebwListFilter(RequestContext ctx, String routeid) {

        BwfilterObj obj = filterCacheManager.getRouteId(routeid);

        if (obj != null) {

            /**
             * step1: 獲取策略
             */
            String strategy = obj.getStrategy();

            /**
             * step2：执行对应process
             * 
             */
            dispatch.doProcess(strategy, ctx, obj);

            LOGGER.debug("> 路由黑白名单策略处理完成，routeid：" + routeid);

        }

        Boolean ifPass = (Boolean) ctx.get(GatewayConstant.BWSTRATEGY.IP.getValue());

        ctx.set("isSuccess", ifPass);

    }

    @Override
    public int checkAuthToken(String accessToken, String routeid) {

        if (accessToken == null) {
            LOGGER.warn("Authorization token is empty");
            return ResponseCode.LIMIT_ERROR_CODE.getCode();
        }
        else {

            /**
             * compare current token with AuthServer's token
             * 
             */
            String rst = "false";

            try {

                List<String> adminList = zuuldisc.getServiceList(adminName);

                for (String path : adminList) {

                    String url = "http://" + path + "/oauth/checkToken";

                    Map<String, String> parme = new HashMap<String, String>(8);

                    parme.put("routeid", routeid);
                    parme.put("accessToken", accessToken);

                    Message msg = new Message(parme);
                    String result = handler.executeHttpCmd(url, msg);

                    LOGGER.info("> checkAuthToken step1 rst:" + result);
                    Message response = JsonHelper.toObject(result, Message.class);
                    Object resp = response.getResponse();
                    rst = String.valueOf(resp);

                    LOGGER.info("> checkAuthToken step2 rst:{}", rst);

                    break;
                }

            }
            catch (Exception e) {

                LOGGER.error(e.getMessage());

                return ResponseCode.TOKEN_TIMEOUT_CODE.getCode();
            }

            /**
             * step3: 如果返回错误， 说明AUTH-token过期， 返回402异常
             */
            if (!StringHelper.TRUEFLAG.equals(rst)) {
                return ResponseCode.TOKEN_TIMEOUT_CODE.getCode();
            }
            else {
                return ResponseCode.SUCCESS_CODE.getCode();
            }
        }
    }

    @Override
    public boolean updateBWList(Message msg) {

        boolean rst = false;
        try {
            if (msg == null) {
                LOGGER.error(">黑白名单服务 更新缓存失敗  msg is：{}", msg);
            }

            String bwfilterobjStr = msg.getRequest().get(GatewayConstant.ADMINOPTPARMKEY);
            BwfilterObj obj = JsonHelper.toObject(bwfilterobjStr, BwfilterObj.class);

            rst = filterCacheManager.updateServiceId(obj);
            msg.setCode(ResponseCode.SUCCESS_CODE.getCode());
            msg.setResponse(rst);

            LOGGER.info("> 黑白名单服务 更新缓存成功! rst：{}", rst);

        }
        catch (Exception e) {

            msg.setCode(ResponseCode.SERVER_ERROR_CODE.getCode());
            String errMsg = ">>>gateway更新FilterL1Cahe：异常" + e.getMessage();
            LOGGER.error(errMsg, e);
        }

        return rst;
    }

    @Override
    public boolean deleteBWList(Message msg) {

        boolean rst = false;
        try {

            if (msg == null) {
                LOGGER.error("> 黑白名单服务 删除缓存失敗  msg is：" + msg);
            }
            String bwfilterobjStr = msg.getRequest().get(GatewayConstant.ADMINOPTPARMKEY);

            BwfilterObj obj = JsonHelper.toObject(bwfilterobjStr, BwfilterObj.class);
            rst = filterCacheManager.deleteServiceId(obj);
            msg.setCode(ResponseCode.SUCCESS_CODE.getCode());
            msg.setResponse(rst);

            LOGGER.info("> 黑白名单服务 更新缓存成功! rst：{}", rst);

        }
        catch (Exception e) {

            msg.setCode(ResponseCode.SERVER_ERROR_CODE.getCode());
            String errMsg = ">gateway更新FilterL1Cahe：异常" + e.getMessage();
            LOGGER.error(errMsg, e);
        }

        return rst;
    }

}
