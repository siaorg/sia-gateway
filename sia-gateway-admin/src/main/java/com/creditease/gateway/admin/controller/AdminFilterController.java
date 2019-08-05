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

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.FilterService;
import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 黑白名单管理
 * 
 * @author peihua
 */

@RestController
public class AdminFilterController extends BaseAdminController {

	public static final Logger LOGGER = LoggerFactory.getLogger(AdminFilterController.class);

	@Autowired
	FilterService filterService;

	@RequestMapping(value = "/addWBList2Route", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String addWBList2Route(@RequestBody String req) throws IOException {

		try {
			LOGGER.info("addWBList2Route by user:{}", authCheckor.getCurrentUser());

			BwfilterObj rs = JsonHelper.toObject(req, BwfilterObj.class);
			String zuulGroupName = rs.getGroupId();
			String id = rs.getRouteid();
			boolean rst = filterService.addWhiteList2Route(zuulGroupName, id, rs);

			Message msg = null;
			if (rst) {
				msg = new Message(rst, ResponseCode.SUCCESS_CODE.getCode());
			} else {
				msg = new Message(rst, ResponseCode.SERVER_ERROR_CODE.getCode());
			}
			return JsonHelper.toString(msg);
		} catch (Exception e) {
			LOGGER.error("exception:{}", e.getLocalizedMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg("Admin黑白名单新增异常" + e.getMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 修改路由黑/白名单
	 *
	 * @param
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateWBList2Route", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String updateWBList2Route(@RequestBody String req) throws IOException {

		Message msg = null;
		try {
			LOGGER.info("updateWBList2Route by user:{}", authCheckor.getCurrentUser());

			BwfilterObj rs = JsonHelper.toObject(req, BwfilterObj.class);
			String zuulGroupName = rs.getGroupId();
			String routeid = rs.getRouteid();

			boolean rst = filterService.updateWBList2Route(zuulGroupName, routeid, rs);

			if (rst) {
				msg = new Message(rst, ResponseCode.SUCCESS_CODE.getCode());
			} else {
				msg = new Message(rst, ResponseCode.SERVER_ERROR_CODE.getCode());
			}
			msg.setCode(ResponseCode.SUCCESS_CODE.getCode());
			msg.setResponse(rst);

			return JsonHelper.toString(msg);

		} catch (Exception e) {
			LOGGER.error("updateWBList2Route exception:{}", e.getLocalizedMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg("Admin黑白名单新增异常" + e.getMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 根據zuulGroupName查詢所有白名單
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/queryWBList", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String queryWhiteList(@RequestBody String req) throws IOException {

		try {
			LOGGER.info("QueryWhiteList by user:{}", authCheckor.getCurrentUser());

			Message msg = new Message();
			msg.setCode(ResponseCode.SUCCESS_CODE.getCode());

			@SuppressWarnings("unchecked")
			Map<String, String> rs = JsonHelper.toObject(req, Map.class);

			String groupId = rs.get("groupId");
			String routeid = rs.get("routeid");
			Object rst = filterService.queryWhiteList(groupId, routeid);

			if (rst != null) {
				BwfilterObj obj = JsonHelper.toObject(rst.toString(), BwfilterObj.class);
				msg.setResponse(obj);
			}
			return JsonHelper.toString(msg);

		} catch (Exception e) {
			LOGGER.error("queryWhiteList exception:{}", e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg("Admin黑白名单新增异常" + e.getMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	@RequestMapping(value = "/deleteWBList2Route", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String deleteWBList2Route(@RequestBody String req) throws IOException {

		try {
			LOGGER.info("DeleteWBList2Route by user:", authCheckor.getCurrentUser());
			BwfilterObj rs = JsonHelper.toObject(req, BwfilterObj.class);
			String zuulGroupName = rs.getGroupId();
			String routeid = rs.getRouteid();

			boolean rst = filterService.deleteWBList2Route(zuulGroupName, routeid, rs);
			Message msg = null;
			if (rst) {
				msg = new Message(rst, ResponseCode.SUCCESS_CODE.getCode());
			} else {
				msg = new Message(rst, ResponseCode.SERVER_ERROR_CODE.getCode());
			}
			return JsonHelper.toString(msg);

		} catch (Exception e) {
			LOGGER.error("Exception:{}", e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg("Admin黑白名单新增异常" + e.getMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}
}
