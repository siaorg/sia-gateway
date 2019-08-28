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


package com.creditease.gateway.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.RouteRibbonHolder;
import com.creditease.gateway.message.Message;

/**
 *
 * 灰度部署
 *
 * @author peihua
 *
 */

@Service
public class RouteRibbonService extends BaseAdminService {

    private static final String BRSTRAGEGY = "Greenblue";

    private static final String CANARY = "Canary";

    public RouteRibbonHolder queryRouteRibbon(String serviceId, String routeid) throws Exception {

        List<String> versionList = zuulDiscovery.getServiceVersions(serviceId);

        /**
         *
         * new RouteRibbonHolder(routid,"1.0", "1.0;1.2",currentTime, "Greenblue");
         *
         **/
        if (versionList == null || versionList.size() <= 0) {

            return null;

        }
        else {

            StringBuffer allvesr = new StringBuffer();

            for (String v : versionList) {
                allvesr.append(v + ";");
            }

            RouteRibbonHolder holder = compDBRepository.queryrouteVerion(serviceId, routeid);

            String allversionStr = null;

            if (allvesr != null) {
                allversionStr = allvesr.toString();

                allversionStr = allversionStr.substring(0, allversionStr.lastIndexOf(";"));
            }

            if (holder == null) {
                holder = new RouteRibbonHolder(serviceId, null, null, allversionStr, null, "GreenBlue");
            }
            else {
                holder.setAllVersions(allversionStr);
            }

            return holder;
        }

    }

    /**
     *
     * 保存蓝绿部署设置
     */
    public RouteRibbonHolder saveBRRouteRibbon(String serviceid, String routid, String version) throws Exception {

        RouteRibbonHolder holder = compDBRepository.queryrouteVerionByRouteId(routid);

        List<String> versionlist = zuulDiscovery.getServiceVersions(serviceid);

        StringBuffer allvesr = new StringBuffer();

        for (String v : versionlist) {
            allvesr.append(v + ";");
        }

        /**
         * 更新数据库
         *
         */
        RouteRibbonHolder hd;

        boolean rst = false;

        if (holder == null) {

            hd = new RouteRibbonHolder(routid, serviceid, version, allvesr.toString(), null, BRSTRAGEGY);
            rst = compDBRepository.addRouteRibbonHolder(hd);

        }
        else {

            rst = compDBRepository.udpateRouteRibbon(serviceid, routid, version);
            holder.setCurrentVersion(version);

            hd = holder;
        }

        if (!rst) {
            LOGGER.info("saveRouteRibbon 数据库更新结果:{}", rst);
            return null;
        }

        /**
         * 通知GW刷新
         */
        List<String> zuulList = zuulDiscovery.getServiceList(getZuulGroupName(routid));

        /**
         *
         * Step3: 调用所有ZUUL-Instnace的/refresh接口同步DB到LocalCache完成同步
         */
        for (String path : zuulList) {
            String url = "http://" + path;
            Map<String, String> map = new HashMap<String, String>(8);
            map.put("routid", routid);
            map.put("version", version);
            Message msg = new Message(map);
            String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.SWR.getValue(), msg);

            LOGGER.info("refreshRoute rst:{}", result);
        }

        return hd;
    }

    /**
     *
     * 保存金丝雀部署设置
     */
    public RouteRibbonHolder saveCanaryRouteRibbon(String serviceid, String routid, String context) throws Exception {

        RouteRibbonHolder holder = compDBRepository.queryrouteVerionByRouteId(routid);

        List<String> versionlist = zuulDiscovery.getServiceVersions(serviceid);

        StringBuffer allvesr = new StringBuffer();

        for (String v : versionlist) {
            allvesr.append(v + ";");
        }

        /**
         * 更新数据库
         *
         */
        RouteRibbonHolder hd;

        boolean rst = false;

        if (holder == null) {

            hd = new RouteRibbonHolder(routid, serviceid, null, allvesr.toString(), null, CANARY);

            hd.setContext(context);
            rst = compDBRepository.addRouteRibbonHolder(hd);

        }
        else {

            rst = compDBRepository.udpateCanaryRibbon(serviceid, routid, context, CANARY);

            hd = holder;
        }

        if (!rst) {
            LOGGER.info(" saveRouteRibbon 数据库更新结果:{}", rst);
            return null;
        }

        /**
         * 通知GW刷新
         */

        try {
            List<String> zuulList = zuulDiscovery.getServiceList(getZuulGroupName(routid));
            /**
             *
             * Step3: 调用所有ZUUL-Instnace的/refresh接口同步DB到LocalCache完成同步
             */
            for (String path : zuulList) {
                String url = "http://" + path;
                Map<String, String> map = new HashMap<String, String>(8);
                map.put("routid", routid);
                map.put("version", null);
                Message msg = new Message(map);
                String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.SWR.getValue(), msg);

                LOGGER.info("refreshRoute rst:{}", result);
            }
        }
        catch (Exception e) {
            LOGGER.info("saveRouteRibbon notify failed:{}" + e.getCause());
        }
        return hd;
    }
}
