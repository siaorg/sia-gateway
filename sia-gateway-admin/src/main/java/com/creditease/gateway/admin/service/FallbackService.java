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

import com.creditease.gateway.admin.domain.FallbackQuery;
import com.creditease.gateway.admin.domain.common.PaginateList;
import com.creditease.gateway.admin.repository.AdminDbRepository;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 
 * 熔断管理service
 * @author peihua
 **/

@Service
public class FallbackService extends BaseAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FallbackService.class);

	@Autowired
	AdminDbRepository fallbackrep;
    /**
     * 按条件搜索操作审计信息
     */
    @SuppressWarnings("rawtypes")
	public Message queryFallbackInfos(FallbackQuery fallbackQuery){
    	
    	LOGGER.info("getSearchKeyword:{} ",fallbackQuery.getSearchKeyword());
    	
        try {
            if(ObjectUtils.isEmpty(fallbackQuery)){
                return Message.buildInValidParamResult();
            }
            PaginateList gatewayFallbackObjs = fallbackrep.queryFallbackList(fallbackQuery);
            return Message.buildSuccessResult(gatewayFallbackObjs);
        } catch (Exception e) {
            return Message.buildExceptionResult();
        }
    }
}
