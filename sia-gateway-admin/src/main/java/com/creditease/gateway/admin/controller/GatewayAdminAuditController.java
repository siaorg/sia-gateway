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


package com.creditease.gateway.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.domain.AuditQuery;
import com.creditease.gateway.admin.service.AuditService;
import com.creditease.gateway.message.Message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @description: 网关监控系统操作审计接口类
 * @author: guohuixie2
 * @create: 2019-06-05 15:15
 **/

@Api("网关监控系统操作审计接口")
@RestController
public class GatewayAdminAuditController {

    public static final Logger LOGGER = LoggerFactory.getLogger(GatewayAdminAuditController.class);

    @Autowired
    private AuditService auditService;

    @ApiOperation(value = "按条件搜索操作审计信息")
    @RequestMapping(value = "/queryAuditInfos", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Message queryAuditInfos(@RequestBody AuditQuery auditQuery) {

        return auditService.queryAuditInfos(auditQuery);
    }

}
