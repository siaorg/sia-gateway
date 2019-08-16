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

package com.creditease.gateway.admin.aspect;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.creditease.gateway.admin.context.ApplicationContextProvider;
import com.creditease.gateway.admin.event.GatewayAuditEvent;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.helper.NetworkHelper;

/**
 * @description: 审计功能实现类
 * @author: guohuixie2
 * @create: 2019-06-03 14:24
 **/

@Aspect
@Component
public class ApiGatewayAuditAspect {

	public static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayAuditAspect.class);

	public static final String CODE = "200";

	private static final int SUCCESS = 1;

	private static final int FAILED = 2;

	private static final List<String> ignoreMethod = Lists.newArrayList(
			"/getSumAPIaccess",
			"/getRegisterRouteNo",
			"/getgwAlarmCount",
			"/monitor/simple",
			"/getgwAlarmList",
			"/queryZuulListInfo",
			"/queryAdminListInfo",
			"/getAPIHealthyArray",
			"/getAPICountArray",
			"/queryAuditInfos");

	@Autowired
	private AuthInterceptor authInterceptor;

	@Autowired
	ApplicationContextProvider applicationContextProvider;

	/**
	 * 定义切点Pointcut
	 */
	@Pointcut("execution(public * com.creditease.gateway.admin.controller..*.*(..))")
	public void excudeService() {

	}

	/**
	 * 环形通知 记录所有对controller的请求
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "excudeService()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Object resultObj = null;
		try {
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
			HttpServletRequest request = sra.getRequest();

			/**
			 * Time of occurrence
			 */
			long startTimeStamp = System.currentTimeMillis();
			Date startTime = new Date(startTimeStamp);

			/**
			 * execute controller method
			 */
			resultObj = pjp.proceed();

			/**
			 * ignore some query methods
			 */
			if (ignoreMethod.contains(request.getRequestURI())) {
				return resultObj;
			}

			/**
			 * get result code
			 */
			int status = getProcessResult(resultObj);

			/**
			 * Execution time，The unit is milliseconds
			 */
			long timeLoss = System.currentTimeMillis() - startTimeStamp;

			/**
			 * Get request parameters
			 */
			String params = getRequestParams(request.getQueryString(), pjp.getArgs());
			LOGGER.debug("params={}", params);

			/**
			 * construct GatewayAuditObj
			 */
			GatewayAuditEvent gatewayAuditEvent = applicationContextProvider.getBean(GatewayAuditEvent.class);
			constructAuditObj(gatewayAuditEvent, request, startTime, timeLoss, params, status);
			LOGGER.debug(" >>>> gatewayAuditObj = [{}]", JSON.toJSONString(gatewayAuditEvent));

			/**
			 * Persistent data
			 */
			gatewayAuditEvent.emit();

		} catch (Throwable throwable) {
			LOGGER.error("> An Throwable occurred in the audit recording...", throwable);
		}
		return resultObj;
	}

	private int getProcessResult(Object resultObj) throws Exception {
		int status = FAILED;
		try {
			/**
			 * 1：成功 2：失败或异常
			 */
			if (null != resultObj) {

				String resultJson = resultObj.toString();
				if (!"String".equals(resultObj.getClass().getSimpleName())) {
					resultJson = JSON.toJSONString(resultObj);
				} else if (!resultJson.startsWith("\\{")) {
					/**
					 * controller返回非json字符串 为成功
					 */
					return SUCCESS;
				}
				JSONObject jsonObject = JSONObject.parseObject(resultJson);
				Object code = jsonObject.get("code");
				if (!ObjectUtils.isEmpty(code) && CODE.equals(code.toString())) {
					status = SUCCESS;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return status;
	}

	private String getRequestParams(String queryString, Object[] args) {
		StringBuffer params = new StringBuffer();
		params.append("requestParams = [ ");
		if (!StringUtils.isEmpty(queryString)) {
			params.append(queryString);
		}
		String bodyStr = "";
		if (args.length > 0) {
			try {
				bodyStr = JSON.toJSONString(args);
			} catch (Exception e) {
				LOGGER.error("> 解析post请求 body 体失败！", e);
			}
		}
		params.append(bodyStr);
		params.append(" ]");
		return params.toString();
	}

	private void constructAuditObj(GatewayAuditEvent gatewayAuditEvent, HttpServletRequest request, Date startTime,
			long timeLoss, String params, int status) {
		gatewayAuditEvent.setUrl(request.getRequestURI());
		gatewayAuditEvent.setMethod(request.getMethod());
		gatewayAuditEvent.setIp(NetworkHelper.getIpAdrress(request));
		gatewayAuditEvent.setUserName(authInterceptor.getCurrentUser());
		gatewayAuditEvent.setStartTime(startTime);
		gatewayAuditEvent.setTimeLoss(timeLoss);
		gatewayAuditEvent.setParams(params);
		gatewayAuditEvent.setStatus(status);
		gatewayAuditEvent.setZuulGroupName(authInterceptor.getZuulGroupName());
	}
}
