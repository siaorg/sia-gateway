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


package com.creditease.gateway.service.wblist;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.helper.IpUtils;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;
import com.netflix.zuul.context.RequestContext;

/**
 * @author peihua
 * 
 *         BlackWhiteListFilterProcess description: 黑白名单IP策略
 * 
 *         网段支持实例：
 * 
 *         IpUtils.ipIsValid("192.168.1.1-192.168.2.100", "192.168.1.54")
 */
public class FilterbyipProcessor extends AbstractbwListDispatch {

    public static final Logger LOGGER = LoggerFactory.getLogger(FilterbyipProcessor.class);

    @Override
    public void process(RequestContext ctx, BwfilterObj obj, String routeid) {

        boolean ifPass = false;

        HttpServletRequest req = ctx.getRequest();

        String path = req.getServletPath();
        path = path.substring(1, path.indexOf("/", 1));

        String ip = IpUtils.getIpAddr(req);

        // case:关闭状态
        if (!obj.getEnabled()) {
            ifPass = true;
            return;
        }

        // case:名单list为空
        if (StringHelper.isEmpty(obj.getList())) {
            ifPass = true;
            return;
        }

        // 黑名单情况
        if (GatewayConstant.BWLISTTYPE.BLACKTYPE.getValue().equals(obj.getType())) {

            if (obj.getList().contains(ip) || isMatchIPSection(obj.getList(), ip)) {

                this.adaptorRseponse(ctx, routeid, ip, GatewayConstant.BWLISTTYPE.BLACKTYPE.getValue());

            }
            else {
                ifPass = true;
            }
        }
        // 白名单情况
        else if (GatewayConstant.BWLISTTYPE.WHITETYPE.getValue().equals(obj.getType())) {

            String ipstrings = obj.getList();
            if (ipstrings.contains(StringHelper.HENXIAN_SEPARATOR)) {

                if (!isMatchIPSection(ipstrings, ip)) {

                    this.adaptorRseponse(ctx, routeid, ip, GatewayConstant.BWLISTTYPE.WHITETYPE.getValue());
                }
                else {

                    ifPass = true;

                }
            }

            else if (!obj.getList().contains(ip)) {

                this.adaptorRseponse(ctx, routeid, ip, GatewayConstant.BWLISTTYPE.WHITETYPE.getValue());

            }
            else {

                ifPass = true;
            }
        }
        else {
            LOGGER.info(">>>>> obj Type not support :" + obj.getType());
        }

        ctx.set(GatewayConstant.BWSTRATEGY.IP.getValue(), ifPass);

    }

    /**
     * 组装响应报文
     * 
     * @param ctx
     * @param serviceId
     * @param ip
     * @param type
     */
    private void adaptorRseponse(RequestContext ctx, String serviceId, String ip, String type) {

        Message msg = Message.fail(type + "名单访问限制" + "当前请求系统：" + serviceId + ",限制ip:" + ip,
                ResponseCode.NO_AUTH_CODE.getCode());

        ctx.set("isSuccess", false);
        ctx.setSendZuulResponse(false);
        ctx.setResponseBody(JsonHelper.toString(msg));
        ctx.getResponse().setContentType("application/json; charset=utf-8");
    }

    public boolean isMatchIPSection(String ipListString, String ip) {

        String[] list = ipListString.split(",");

        for (String ipsection : list) {
            if (ipsection.contains("-")) {
                boolean rst = IpUtils.ipIsValid(ipsection, ip);

                if (rst) {
                    return rst;
                }
            }
        }
        return false;
    }

}
