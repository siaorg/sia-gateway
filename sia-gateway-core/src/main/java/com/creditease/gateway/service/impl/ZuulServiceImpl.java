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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.creditease.gateway.constant.GatewayConstant.ZuulState;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.repository.ZuulRepository;
import com.creditease.gateway.service.ZuulService;

/**
 * ZUUL网关服务
 * 
 * @author peihua
 * 
 */

public class ZuulServiceImpl implements ZuulService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ZuulServiceImpl.class);

	@Autowired
	ZuulRepository zuulRepository;

	@Override
	public void registerZuul() throws Exception {
		try {
			zuulRepository.initZuulInfo();

		} 
		catch (DuplicateKeyException e) {
 
			throw e;
		}
		catch (Exception ex) {
 
			throw ex;
		}
	}

	@Override
	public void updateZuul(ZuulState stat, String zuulGourpName) {

		try {
			zuulRepository.updateZuulInfo(stat,zuulGourpName);

		} catch (Exception e) {

			new GatewayException(ExceptionType.CoreException,e);
		}

	}

	@Override
	public boolean registerFilter(CompInfo info) {

		boolean rst = false;

		CompInfo comp = zuulRepository.queryComp(info.getCompFilterName());

		if (comp == null) {
			rst = zuulRepository.insertFilterComp(info);
		} else {
			LOGGER.info(
					"registerFilter exists same filterName, as the CompInfo Name conflict:" + info.getCompFilterName());
		}

		return rst;

	}

}
