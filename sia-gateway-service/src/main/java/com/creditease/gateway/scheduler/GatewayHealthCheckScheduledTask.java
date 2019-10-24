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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.service.SchedulerService;
import com.creditease.gateway.service.abstractlist.AbstractPostAlarmInfo;
import com.creditease.gateway.service.abstractlist.EmailAlarmPostImpl;
import com.creditease.gateway.service.repository.SchedulerRepository;
import com.creditease.gateway.vo.AlarmEmailVO;

/**
 * 网关定时健康监测
 * 
 * 网关统计的是每15分钟执行
 * 
 * @author guohuoxie2
 * 
 */

@Component
public class GatewayHealthCheckScheduledTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayHealthCheckScheduledTask.class);

    @Autowired
    SchedulerService sts;

    @Autowired
    SchedulerRepository schedulerepository;

    @Autowired
    private DiscoveryService zuuldisc;

    @Autowired
    EmailAlarmPostImpl emailAlarmPost;

    /**
     * 网关core节点做定时健康检测
     * 
     * @throws Exception
     * 
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void healthCheckCronTask() {

        LOGGER.info(">开始网关健康监测: time:{}" + new Date(System.currentTimeMillis()));
        List<ZuulInfo> listdb = new ArrayList<ZuulInfo>();

        try {
            listdb = schedulerepository.queryZuulList();

        }
        catch (Exception e) {

            LOGGER.error("healthCheckCronTask > Exception occured>{} ", e.getCause().getMessage());
        }
        for (ZuulInfo zinfo : listdb) {

            try {
                // 如果状态为DEAD,说明该网关发邮件已达到峰值
                if ("DEAD".equals(zinfo.getZuulStatus())) {
                    continue;
                }

                String zuulInstanceId = zinfo.getZuulInstanceId();
                String zuulGroupName = zinfo.getZuulGroupName();
                List<String> zuulListEureka;

                zuulListEureka = zuuldisc.getServiceList(zuulGroupName);

                if (!zuulListEureka.contains(zuulInstanceId)) {

                    AlarmEmailVO alarmEmailVO = constructAlarmEmailVO(zuulInstanceId, zuulGroupName);

                    GatewayConstant.GatewayPostResultEnum result = emailAlarmPost.post(alarmEmailVO);
                    if (result.getKey() == GatewayConstant.GatewayPostResultEnum.CALLBACK_FUNCTION.getKey()) {
                        schedulerepository.updateZuulInfoStatusByID(alarmEmailVO.getPrimary(),
                                GatewayConstant.GatewayStatusEnum.DEAD.getValue());
                    }
                }
            }
            catch (Exception e) {

                LOGGER.error("> healthCheckCronTask exception:" + e);
            }
        }
    }

    private AlarmEmailVO constructAlarmEmailVO(String zuulInstanceId, String zuulGroupName) {

        String content = "网关系统core节点异常，网关组名称:[ " + zuulGroupName + " ],机器IP：[ " + zuulInstanceId + " ]，请检查！";
        AlarmEmailVO alarmEmailVO = new AlarmEmailVO();
        alarmEmailVO.setContent(content);
        alarmEmailVO.setApplicationName(zuulGroupName);
        alarmEmailVO.setPrimary(zuulInstanceId);
        alarmEmailVO.setSubject(AbstractPostAlarmInfo.SUBJECT);
        alarmEmailVO.setFlag(true);
        return alarmEmailVO;
    }

    /**
     * 每天凌晨执行一次：清除压制缓存
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeAlarmCache() {

        try {
            LOGGER.info("邮件预警的压制缓存清除任务开始...EmailAlarmService.ALARM_COUNT_MAP: [ {} ]",
                    JsonHelper.toString(AbstractPostAlarmInfo.SUCCESS_POST_COUNT_MAP));
            AbstractPostAlarmInfo.SUCCESS_POST_COUNT_MAP.clear();
        }
        catch (Exception e) {
            LOGGER.error("邮件预警的压制缓存进行清除失败！", e);
        }

    }

}
