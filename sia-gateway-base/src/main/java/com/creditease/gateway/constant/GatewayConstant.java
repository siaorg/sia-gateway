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


package com.creditease.gateway.constant;

/**
 * 
 * @author peihua
 * 
 */

public class GatewayConstant {

    /**
     * Gateway-常量
     */
    public static String routeL1CacheKey = "route.cache.key";

    public static final String APISUMCOUNT = "APIACCESS";

    public static final String APIFAILCOUNT = "APIFAIL";

    public static final String ROLE_ADMIN = "admin";

    public static final String ZUUL_POSTFIX = "-GATEWAY-CORE";

    public static final String API_GATEWAY_CORE = "API-GATEWAY-CORE";

    public static final String PATH_CONFLICT = "path_UNIQUE";

    public static final String ROUTEID_CONFLICT = "PRIMARY";

    public static final String GWGROUPNAME = "GWGROUPNAME";

    public static final String ROUTENAME = "ROUTENAME";

    public static final String BROWSER = "浏览器:";

    public static final String APPLICATION = "应用:";

    public static final String TOTAL = "Total";

    public static final String ZERO = "0";

    public static final String ONE = "1.00";

    public static final String MONITORURL = "http://%s/hystrix/monitor?delay=%s&title=%s&stream=http://%s/hystrix.stream";

    /**
     * 
     * 返回码常亮
     * 
     **/

    public static final int OK = 0;

    public static final int CLIENT_ERROR = 1;

    public static final int SERVER_ERROR = 2;

    public static final int PATH_CONFLICT_ERROR = 3;

    public static final int ROUTIE_CONFLICT_ERROR = 4;

    /**
     * 
     * BWSTRATEGY description:黑白名单策略
     *
     */
    public static enum ADMINOPTKEY {

        /**
         * SR：Switchroute RR：Refreshroute UFC：Updatefiltercache DFC:Deletefiltercache CPR:CompRemove
         * BRR：Bindrouterefresh RWH：RouteWatch GWS：GWStatistic RTS：RouteStatistic SWR：Switchroute RRL：RouteRateLimit
         */
        SR("Switchroute"), RR("Refreshroute"), UFC("Updatefiltercache"), DFC("Deletefiltercache"), CPR("CompRemove"), BRR("Bindrouterefresh"), RWH("RouteWatch"), GWS("GWStatistic"), RTS("RouteStatistic"), SWR("Switchroute"), RRL("RouteRateLimit"), GWR("GatewayRefresh"), TPS("TOPOStatistic"), GTP("getTopology"), GURL("getUrlRecordList");

        private String value;

        ADMINOPTKEY(String value) {
            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    public static final String ADMINOPTPARMKEY = "admin.request.parme.key";

    public enum OptEnum {
        COUNTARRAY(1), HEALTHARRAY(2); // 调用构造函数来构造枚举项

        private int value = 0;

        private OptEnum(int value) { // 必须是private的，否则编译错误
            this.value = value;
        }

        public int value() {

            return this.value;
        }
    }

    public static enum ZuulState {
        /**
         * DONE:下线状态 RUNNING：运行状态 DEAD： 挂掉状态
         */
        DONE, RUNNING, DEAD
    }

    public enum LinkState {
        /**
         * GREEN:最近有访问， 访问正常 GRAY：最近无访问 RED：最近有访问， 访问异常
         */
        GREEN, GRAY, RED
    }

    public static enum FilterEnable {
        /**
         * ok:Filter 正常 notok：Filter 不正常
         */
        ok, notok
    }

    /**
     * 路由/Filter-常量
     */
    public static enum Routestrategy {

        /**
         * SERVICEID： 路由ID
         * 
         * URL： 路由URL
         */

        SERVICEID, URL
    }

    /**
     * DB-常量
     */
    public static final String DATASOURCENAME = "apigateway";

    public static final String DATASOURCEPREFIX = "spring.datasource.skytrain";

    public static final String JDBCTEMPLATENAME = "gatewayJdbcTemplate";

    /**
     * Redis-常量
     */

    public static final String REDISTEMPLATENAME = "gatewayRedisTemplate";
    public static final String STRINGREDISTEMPLATENAME = "gatewayStringRedisTemplate";

    /**
     * ZK-常量
     */
    public static final String ZK_SEPARATOR = "/";
    public static final String ZK_ROOT = "/";
    public static final String ZK_KEY_SPLIT = ":";
    public static final String ZK_DEFAULT_VALUE = "";

    /**
     * ZK-用户权限
     */
    public static final String ALLAUTH = "SIA:SkyWorld";
    public static final String CREATEAUTH = "guest:guest";
    public static final String DIGEST = "digest";

    /**
     * 
     * BWSTRATEGY description:黑白名单策略
     *
     */
    public static enum BWSTRATEGY {

        /**
         * IP: IP策略 API： API策略
         */
        IP("IP"), API("API");

        private String value;

        BWSTRATEGY(String value) {
            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    public static final String ZUULGROUPNAMEKEY = "zuulGroupName";
    public static final String GLOBALBLACKID = "globalBlackServiceId";

    /**
     * 
     * BWLISTTYPE description: 名单类型
     *
     */
    public static enum BWLISTTYPE {

        /**
         * BLACKTYPE:黑名单 WHITETYPE：白名单
         */
        BLACKTYPE("black"), WHITETYPE("white");

        private String value;

        BWLISTTYPE(String value) {
            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    /**
     * 网关节点状态
     */
    public enum GatewayStatusEnum {

        /**
         * RUNNING：运行状态 DEAD: 停止状态
         * 
         */
        RUNNING("RUNNING"), DEAD("DEAD");

        private String value;

        GatewayStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    /**
     *
     * 发送邮件结果
     */
    public enum GatewayPostResultEnum {
        SUCCESS(1, "邮件发送成功！"), FAIL(2, "出现异常！"), ARRIVE_PEAK_VALUE(3, "邮件发送达到峰值！"), CALLBACK_FUNCTION(4, "执行回调函数！");

        private int key;
        private String value;

        GatewayPostResultEnum(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {

            return key;
        }

        public String getValue() {

            return value;
        }
    }

}
