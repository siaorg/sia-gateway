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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.creditease.gateway.admin.domain.AuditQuery;
import com.creditease.gateway.admin.domain.common.PaginateList;
import com.creditease.gateway.admin.event.GatewayAuditEvent;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.message.Message;

/**
 * @description: admin审计业务实现类
 * @author: guohuixie2
 * @create: 2019-06-04 15:13
 **/

@Service
public class AuditService extends BaseAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    /**
     * 审计信息落库
     */
    public Message recordAuditInfo(GatewayAuditEvent gatewayAuditEvent) {

        try {
            int i = adminDBRepository.recordAuditInfo(gatewayAuditEvent);
            if (i < 1) {
                LOGGER.error("Data insertion failed...");
                return Message.buildExceptionResult("Data insertion failed");
            }
            return Message.buildSuccessResult();
        }
        catch (Exception e) {
            LOGGER.error("An abnormality occurred in the audit information...", e);
            return Message.buildExceptionResult(e);
        }
    }

    /**
     * 按条件搜索操作审计信息
     */
    public Message queryAuditInfos(AuditQuery auditQuery) {

        try {
            if (ObjectUtils.isEmpty(auditQuery)) {
                return Message.buildInValidParamResult();
            }
            PaginateList gatewayAuditObjs = adminDBRepository.queryAuditInfos(auditQuery);
            return Message.buildSuccessResult(gatewayAuditObjs);
        }
        catch (Exception e) {
            return Message.buildExceptionResult();
        }
    }
}
