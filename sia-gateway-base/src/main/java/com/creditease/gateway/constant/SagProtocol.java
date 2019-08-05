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
 * CORE&Admin SPI Protocol
 * 
 * @author peihua
 **/

public interface SagProtocol {

    //后端调用协议
    public String UPDATEFILTERCACHE = "/sagOptUpdatefiltercache";
    public String DELETEFILTERCACHE = "/sagOptDeletefiltercache";
    public String ADMINOPTCONFIGVIEW = "/sagAdminOptConfigView";
    public String BINDROUTEREFRESH = "/sagOptBindrouterefresh";
    public String COMPREMOVE = "/sagOptCompRemove";
    public String GWSTATISTIC = "/sagOptGWStatistic";
    public String ROUTESTATISTIC = "/sagOptRouteStatistic";
    public String ROUTERATELIMIT = "/sagOptRouteRateLimit";
    public String REFRESHROUTE = "/sagOptRefreshroute";
    public String SWITCHROUTE = "/sagOptSwitchroute";
    public String ROUTEWATCH = "/sagOptRouteWatch";
    public String HEALTHMONITOR = "/gantry/healthMonitor";
    public String GATEWAYREFRESH = "/GatewayRefresh";
    public String GETTOPOLOGY = "/sagOptgetTopology";
    public String GETURLRECORDLIST = "/sagOptgetUrlRecordList";
    
    //前端调用协议
    public String DOWNLOADFILELIST = "/sagDownLoadFileList";
    public String FILEREMOVE = "/sagFileRemove";
    public String FILEDOWNLOAD = "/sagFiledownload";
}
