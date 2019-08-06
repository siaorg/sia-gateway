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


package com.creditease.gateway.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creditease.gateway.alarm.AlarmService;
import com.creditease.gateway.annotation.CatchExcept;

/**
 * 异常拦截处理
 * 
 * @author peihua
 *
 * @param <K>
 * @param <V>
 */
@Aspect
@Component
public class ApiGatewayExceptionAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(ApiGatewayExceptionAspect.class);

    @Autowired
    protected AlarmService alarmservice;

    @Pointcut("@annotation(catchExcept)")
    public void reportzuulAlarm(CatchExcept catchExcept) {

    }

    @Before(value = "reportzuulAlarm(catchExcept)")
    public void deBefore(JoinPoint joinPoint, CatchExcept catchExcept) throws Throwable {

        LOGGER.info("apiGatewayException:{}" ,catchExcept);
    }

    /**
     * @param result
     */
    @AfterReturning(returning = "result", pointcut = "reportzuulAlarm(catchExcept)")
    public void afterReturning(JoinPoint joinPoint, CatchExcept catchExcept, Object result) {

        alarmservice.reportAlarm(result.toString());

    }

    @AfterThrowing(pointcut = "reportzuulAlarm(catchExcept)", throwing = "ex")
    public void throwss(JoinPoint joinPoint, Throwable ex, CatchExcept catchExcept) throws Exception {

    }
}
