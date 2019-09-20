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


package com.creditease.gateway.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CounterInfo;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.helper.DateTimeHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.service.SchedulerService;
import com.creditease.gateway.service.repository.SchedulerRepository;

/**
 * 网关統計任務
 * 
 * 网关统计的是每小时0分执行
 * 
 * @author peihua
 * 
 */

@Component
public class GatewayMonitorScheduledTask {

    @Autowired
    SchedulerService sts;

    @Autowired
    SchedulerRepository schedulerepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayMonitorScheduledTask.class);

    String dateFormat = "yyyy-MM-dd HH";
    @Autowired
    ZuulHandler handler;

    /**
     * 路由统计
     * 
     * @throws Exception
     * 
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void statisticCronTask() {

        LOGGER.info(">开始网关GatewayMonitorScheduledTask: time:{}", new Date(System.currentTimeMillis()));

        List<ZuulInfo> zuulist = new ArrayList<ZuulInfo>();

        try {
            zuulist = sts.getZuulList();

        }
        catch (Exception e) {

            LOGGER.error("> GatewayMonitorScheduledTask Exception:{}", e.getCause());
        }
        for (ZuulInfo zinfo : zuulist) {

            try {
                String status = zinfo.getZuulStatus();

                if (("Dead").equals(status)) {
                    LOGGER.info("ZuulInstanceId:" + zinfo.getZuulInstanceId() + "zinfo status is :" + status);
                    continue;
                }

                String instatnce = zinfo.getZuulInstanceId();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

                String dateTime = DateTimeHelper.dateFormat(calendar.getTime(), dateFormat);
                String counterKeysuccess = GatewayConstant.APISUMCOUNT + "-" + dateTime;

                String url = "http://" + instatnce;
                String rst = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.GWS.getValue(), new Message());

                LOGGER.info(">>>> GatewayMonitorScheduledTask remoteCall rst:" + rst);

                String sumcount = rst.substring(1, rst.indexOf("-"));
                String failcount = rst.substring(rst.indexOf("-") + 1, rst.length() - 1);
                CounterInfo counterapi = new CounterInfo(instatnce, counterKeysuccess, Integer.parseInt(sumcount),
                        dateTime);

                /**
                 * step1: 統計總共請求次數
                 * 
                 */
                boolean rs = schedulerepository.insertCounter(counterapi, zinfo.getZuulGroupName());

                /**
                 * step2: 統計失敗請求次數
                 * 
                 */
                String counterKeyfail = GatewayConstant.APIFAILCOUNT + "-" + dateTime;
                CounterInfo counterfail = new CounterInfo(instatnce, counterKeyfail, Integer.parseInt(failcount),
                        dateTime);

                boolean rf = schedulerepository.insertCounter(counterfail, zinfo.getZuulGroupName());

                LOGGER.info("网关统计成功结果:rs{}, 网关失败访问统计结果rf:{}", rs, rf);

                LOGGER.info("网关计数rs:{},网关失败访问计数rf:", sumcount, failcount);

            }
            catch (NumberFormatException e) {

                LOGGER.error("GatewayMonitorScheduledTask NumberFormatException:" + e);

            }
            catch (Exception e) {

                LOGGER.error("GatewayMonitorScheduledTask Exception:" + e);
            }
        }

    }

}
