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


package com.creditease.gateway.service.abstractlist;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.service.repository.AlarmRepository;
import com.creditease.gateway.vo.AlarmEmailVO;
import com.google.common.collect.Maps;

/**
 * @description: 网关邮件发送抽象类
 * @author: guohuixie2
 * @create: 2019-02-21 16:00
 **/
public abstract class AbstractPostAlarmInfo {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractPostAlarmInfo.class);

    public static final String SUBJECT = "智能路由网关预警";
    public static final String SPLIT_SYMBOL = ",";

    public static final int INIT_VALUE = 1;
    public static final int EMPTY_VALUE = 0;

    public static Map<String, Integer> SUCCESS_POST_COUNT_MAP = Maps.newConcurrentMap();

    @Value("${SKYTRAIN_DEFAULT_EMAIL}")
    public String siaEmailAddress;

    @Value("${ALARM_MAX_COUNT}")
    private int alarmMaxCount;

    @Autowired
    private AlarmRepository alarmRepository;

    public GatewayConstant.GatewayPostResultEnum post(AlarmEmailVO alarmEmailVO) {

        Map<String, Object> map = null;
        try {

            // 兼容旧版本
            if (alarmEmailVO.getInstance() != null) {
                alarmRepository.reportAlarm(alarmEmailVO);
            }

            // true : 未达到峰值
            if (pressCheckRule(alarmEmailVO)) {

                String alarmEmailAddress = this.getEmailByZuulGroupName(alarmEmailVO.getApplicationName());
                alarmEmailVO.setMailto(Arrays.asList(alarmEmailAddress.split(",")));

                // 组装邮件报文
                map = this.initEmailTemplate(alarmEmailVO);

                // 发送邮件
                ResponseEntity<String> result = this.postAction(JsonHelper.toString(map));

                LOGGER.info("发送结果：[ {} ]", null == result ? "result is null !" : result.toString());

                // 记录成功发生次数
                afterPost(alarmEmailVO.getPrimary());

            } else if (alarmEmailVO.getFlag()) {
                return GatewayConstant.GatewayPostResultEnum.CALLBACK_FUNCTION;
            } else {
                return GatewayConstant.GatewayPostResultEnum.ARRIVE_PEAK_VALUE;
            }
        } catch (Exception e) {
            LOGGER.error("网关发送预警邮件失败！邮件报文内容：[{}]", JsonHelper.toString(map), e);
        }

        return GatewayConstant.GatewayPostResultEnum.SUCCESS;
    }

    boolean pressCheckRule(AlarmEmailVO alarmEmailVO) throws Exception {

        LOGGER.info("pressCheckRule, alarm applicationName:{}, instance:{}, primary:{}",
                alarmEmailVO.getApplicationName(), alarmEmailVO.getInstance(), alarmEmailVO.getPrimary());

        String primary = alarmEmailVO.getPrimary();
        int alarmCount = SUCCESS_POST_COUNT_MAP.containsKey(primary) ? SUCCESS_POST_COUNT_MAP.get(primary)
                : EMPTY_VALUE;

        if (alarmCount < alarmMaxCount) {
            return true;
        } else {
            return false;
        }
    }

    void afterPost(String primary) {

        try {
            if (!SUCCESS_POST_COUNT_MAP.containsKey(primary)) {
                SUCCESS_POST_COUNT_MAP.put(primary, INIT_VALUE);
            } else {
                SUCCESS_POST_COUNT_MAP.put(primary, SUCCESS_POST_COUNT_MAP.get(primary) + INIT_VALUE);
            }
        } catch (Exception e) {
            LOGGER.error("邮件发生后，记录success count fail。。。", e);
        }
    }

    /**
     * 组装发送邮件的模板
     * <p>
     * { "subject": "this is a alarm email", "mailto ": ["alice@someone.cn", "bob@someone.cn"], "content":
     * "this is the message body", 邮件主体内容 "primary": "this is an unique key", "elapse": 1800 压轴时间
     * <p>
     * String subject, String alarmEmailAddress, String content, String primary }
     */
    public Map<String, Object> initEmailTemplate(AlarmEmailVO alarmEmailVO) {

        Map<String, Object> map = Maps.newHashMap();
        map.put("subject", SUBJECT);
        map.put("content", alarmEmailVO.getContent());
        map.put("primary", alarmEmailVO.getPrimary());
        map.put("mailto", alarmEmailVO.getMailto());
        return map;
    }

    /**
     * 获取收件人邮箱地址
     *
     * @param zuulGroupName ：网关组名称
     * @return String
     * @throws Exception
     */
    public abstract String getEmailByZuulGroupName(String zuulGroupName) throws Exception;

    /**
     * 邮件发送
     *
     * @param str 邮件发送报文
     * @return response
     * @throws Exception
     */
    abstract ResponseEntity<String> postAction(String str) throws Exception;

}
