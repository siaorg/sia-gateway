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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.domain.QueryObj;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.admin.service.RouteService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.RouteObj;
import com.creditease.gateway.domain.RouteObjExtend;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 路由管理
 * 
 * @author peihua
 */

@RestController
public class AdminRouteController extends BaseAdminController {

	public static final Logger LOGGER = LoggerFactory.getLogger(AdminRouteController.class);

	@Autowired
	RouteService routeService;

	@Autowired
	AdminService adminService;

	/**
	 * 添加一个路由
	 */
	@RequestMapping(value = "/addRoute", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String addRoute(@RequestBody RouteObj routeobj) throws IOException {

		LOGGER.info(">添加一个路由 user:{}", authCheckor.getCurrentUser());
		ObjectMapper mapper = new ObjectMapper();
		Message msg = null;
		try {
			int r = routeService.inspectRepeat(routeobj) ? GatewayConstant.PATH_CONFLICT_ERROR
					: routeService.addRoute(routeobj);
			switch (r) {

			case GatewayConstant.PATH_CONFLICT_ERROR:
				msg = new Message(r, ResponseCode.PATH_CONFLICT_CODE.getCode());
				break;
			case GatewayConstant.ROUTIE_CONFLICT_ERROR:
				msg = new Message(r, ResponseCode.ROUTEID_CONFLICT_CODE.getCode());
				break;
			case GatewayConstant.CLIENT_ERROR:
				msg = new Message(r, ResponseCode.PARAM_ERROR_CODE.getCode());
				break;
			case GatewayConstant.SERVER_ERROR:
				msg = new Message(r, ResponseCode.SERVER_ERROR_CODE.getCode());
				break;
			case GatewayConstant.OK:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;

			default:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			}
			LOGGER.info("return rst:" + r);

			return mapper.writeValueAsString(msg);

		} catch (Exception e) {

			LOGGER.error("> addRoute Excetion:" + e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}

	}

	/**
	 * 返回路由列表
	 */
	@RequestMapping(value = "/getRouteList", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String getRouteList() {

		try {

			LOGGER.info(">getRouteList by user:" + authCheckor.getCurrentUser());

			List<RouteObj> routeList = routeService.getRouteList();

			Map<String, List<String>> filterComp = routeService.queryRouteCompRelations();

			List<RouteObjExtend> routeExtendList = routeService.getRouteData(routeList, filterComp);

			Message msg = new Message(routeExtendList);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);
		} catch (Exception e) {

			LOGGER.error("》getRouteList Excetion:{}", e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}

	}

	/**
	 * 根据路由ID查询路由
	 */
	@RequestMapping(value = "/getRouteByRouteID", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String getRouteByRouteID(@RequestBody QueryObj queryobj) {

		try {
			RouteObj filterRoute = routeService.queryRouteByid(queryobj);

			Message msg = new Message(filterRoute);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);

		} catch (Exception e) {
			LOGGER.error("getRouteByRouteID Excetion:{}", e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 根据路由ID查询路由绑定组件
	 */
	@RequestMapping(value = "/getRouteCompByRouteID", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String getRouteCompByRouteID(@RequestBody QueryObj queryobj) {

		try {
			Map<String, List<String>> filterComp = routeService.queryRouteCompRelations();
			List<String> filterRoute = filterComp.get(queryobj.getRouteid());

			Message msg = new Message(filterRoute);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);
		} catch (Exception e) {
			LOGGER.error("getRouteCompByRouteID Excetion:{}", e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 根据條件查询路由
	 */
	@RequestMapping(value = "/getRouteByPath", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String getRouteByPath(@RequestBody QueryObj queryobj) {

		try {
			List<RouteObj> filterRoute = routeService.queryRoute(queryobj);

			Map<String, List<String>> filterComp = routeService.queryRouteCompRelations();

			List<RouteObjExtend> routeExtendList = routeService.getRouteData(filterRoute, filterComp);

			Message msg = new Message(routeExtendList);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);

		} catch (Exception e) {

			LOGGER.error(">getRouteByPath Excetion:" + e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 编辑路由信息
	 */
	@RequestMapping(value = "/updateRoute", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String updateRoute(@RequestBody RouteObj obj) {

		try {
			int r = routeService.updateRoute(obj);

			Message msg = null;

			switch (r) {
			case GatewayConstant.CLIENT_ERROR:
				msg = new Message(r, ResponseCode.PARAM_ERROR_CODE.getCode());
				break;
			case GatewayConstant.SERVER_ERROR:
				msg = new Message(r, ResponseCode.SERVER_ERROR_CODE.getCode());
				break;
			case GatewayConstant.OK:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			default:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			}

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);
		} catch (Exception e) {

			LOGGER.error(">updateRoute Excetion:" + e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 删除路由
	 */
	@RequestMapping(value = "/deleteRoute", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String deleteRoute(@RequestBody RouteObj obj) {

		try {
			int r = routeService.deleteRoute(obj);

			Message msg = null;

			switch (r) {
			case GatewayConstant.CLIENT_ERROR:
				msg = new Message(r, ResponseCode.PARAM_ERROR_CODE.getCode());
				break;
			case GatewayConstant.SERVER_ERROR:
				msg = new Message(r, ResponseCode.SERVER_ERROR_CODE.getCode());
				break;
			case GatewayConstant.OK:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			default:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			}

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);

		} catch (Exception e) {

			LOGGER.error(">>> Excetion:" + e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 发布功能
	 *
	 * @return
	 */
	@RequestMapping("/publisheRoute")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String publisheRoute(@RequestBody RouteObj obj) {

		try {
			int r = routeService.publisheRoute(obj);
			Message msg = null;

			switch (r) {
			case GatewayConstant.CLIENT_ERROR:
				msg = new Message(r, ResponseCode.PARAM_ERROR_CODE.getCode());
				break;
			case GatewayConstant.SERVER_ERROR:
				msg = new Message(r, ResponseCode.SERVER_ERROR_CODE.getCode());
				break;
			case GatewayConstant.OK:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			default:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);
		} catch (Exception e) {

			LOGGER.error("Excetion:{}", e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 下线功能-->给Zuul发信号， ZUUL去DB取数据更新Cache
	 *
	 * @return
	 */
	@RequestMapping("/downRoute")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String downRoute(@RequestBody RouteObj obj) {

		try {

			int r = routeService.downRoute(obj);
			Message msg = null;

			switch (r) {
			case GatewayConstant.CLIENT_ERROR:
				msg = new Message(r, ResponseCode.PARAM_ERROR_CODE.getCode());
				break;
			case GatewayConstant.SERVER_ERROR:
				msg = new Message(r, ResponseCode.SERVER_ERROR_CODE.getCode());
				break;
			case GatewayConstant.OK:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			default:
				msg = new Message(r, ResponseCode.SUCCESS_CODE.getCode());
				break;
			}

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);
		} catch (Exception e) {

			LOGGER.error("downRoute Excetion:{}", e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);
			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 根据角色对应网关集群组名进行权限过滤
	 *
	 * @return
	 */
	@RequestMapping("/selectAuth")
	@ResponseBody
	public String selectAuth(HttpServletRequest request) {

		List<String> roleNames = null;
		List<String> results = new ArrayList<>();

		try {
			roleNames = authCheckor.getCurrentUserRoles();

			if (!roleNames.contains(GatewayConstant.ROLE_ADMIN)) {
				for (String roleName : roleNames) {
					results.add(roleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX);
				}
			} else {
				results.add(GatewayConstant.ROLE_ADMIN);
			}

			Message msg = new Message(results);

			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(msg);

		} catch (Exception e) {
			LOGGER.error(">>>>> selectAuth TASK Exception :{} ", e);
		}
		return null;

	}

	/**
	 * 加载所有apiName应用名称
	 */
	@RequestMapping(value = "/getApplicationNameList", produces = "application/json;charset=UTF-8")
	@CrossOrigin(methods = { RequestMethod.GET, RequestMethod.POST }, origins = "*")
	@ResponseBody
	public String getApplicationNameList() {

		try {
			List<Map<String, String>> applicationNameMaps = routeService.queryApplicationNameList();

			Message msg = new Message(applicationNameMaps);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(msg);

		} catch (Exception e) {
			LOGGER.error(">>> Exception:{}", e.getMessage());

			new GatewayException(ExceptionType.AdminException, e);

			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

	/**
	 * 下载路由list文件，json格式
	 */
	@RequestMapping(value = "/downloadRouteFile")
	public ResponseEntity<InputStreamResource> downloadRouteFile(
			@RequestParam("routeIdList") List<String> routeIdList) {

		try {
			List<RouteObj> list = routeService.getRouteList(routeIdList);

			// 去除id属性，routeStatus设为EDIT状态
			List<Map<String, Object>> routeList = new LinkedList<>();
			for (RouteObj obj : list) {
				Map<String, Object> map = new LinkedHashMap<>(12);
				map.put("routeid", obj.getRouteid());
				map.put("path", obj.getPath());
				map.put("serviceId", obj.getServiceId());
				map.put("url", obj.getUrl());
				map.put("stripPrefix", obj.isStripPrefix());
				map.put("retryable", obj.getRetryable());
				map.put("apiName", obj.getApiName());
				map.put("enabled", obj.getEnabled());
				map.put("zuulGroupName", obj.getZuulGroupName());
				map.put("routeStatus", RouteService.RouteStatus.EDIT.toString());
				map.put("strategy", obj.getStrategy());

				routeList.add(map);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", "GatewayRouteList.json");
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			byte[] body = JsonHelper.toString(routeList).getBytes("UTF-8");
			InputStreamResource in = new InputStreamResource(new ByteArrayInputStream(body));

			LOGGER.info("Download route file success!");

			return new ResponseEntity<>(in, headers, HttpStatus.CREATED);

		} catch (Exception e) {
			LOGGER.error("Download route file failed!" + e.getMessage());
			new GatewayException(ExceptionType.AdminException, e);

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadRouteFile", method = { RequestMethod.POST })
	@ResponseBody
	public String uploadRouteFile(@RequestParam("file") MultipartFile file,
			@RequestParam("currentRoleName") String currentRoleName) {

		try {
			byte[] bytes = file.getBytes();

			if (bytes == null || bytes.length <= 0 || StringHelper.isEmpty(currentRoleName)) {
				LOGGER.warn("File:{} or currentRoleName:{} is empty!", file.getOriginalFilename(), currentRoleName);

				return new ObjectMapper().writeValueAsString(new Message("file is empty!"));
			}
			/*
			 * API-GATEWAY-CORE可导入已存在的group的路由 非API-GATEWAY-CORE只能导入本group的路由
			 */
			Set<String> set;
			String zuulGroupName = currentRoleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX;
			if (GatewayConstant.API_GATEWAY_CORE.equals(zuulGroupName)) {
				set = adminService.getAllGroupSet();
			} else {
				set = new HashSet<>();
				set.add(zuulGroupName);
			}

			List<String> list = JsonHelper.toObject(new String(bytes, "UTF-8"), List.class);

			List<String> failRouteIds = new LinkedList<>();
			List<RouteObj> newRouteList = new LinkedList<>();
			List<String> newIdList = new LinkedList<>();

			for (Object obj : list) {
				RouteObj route = new RouteObj();

				BeanUtils.populate(route, (Map<String, ?>) obj);
				if (!set.contains(route.getZuulGroupName())) {
					failRouteIds.add(route.getRouteid());
					continue;
				}

				newRouteList.add(route);
				newIdList.add(route.getRouteid());
			}

			Map<String, RouteObj> existsRoutes = failRouteIds.size() == list.size() ? Collections.EMPTY_MAP
					: routeService.getRouteMap(newIdList);

			List<String> successRouteIds = new LinkedList<>();
			for (RouteObj route : newRouteList) {
				if (existsRoutes.containsKey(route.getRouteid())) {
					failRouteIds.add(route.getRouteid());
					continue;
				}

				int r = routeService.inspectRepeat(route) ? GatewayConstant.PATH_CONFLICT_ERROR
						: routeService.addRoute(route);

				if (r == GatewayConstant.OK) {
					successRouteIds.add(route.getRouteid());
				} else {
					failRouteIds.add(route.getRouteid());
				}
			}

			LOGGER.info("Upload route file success!");

			Map<String, Object> result = new LinkedHashMap<>(2);
			result.put("success", successRouteIds);
			result.put("fail", failRouteIds);

			return new ObjectMapper().writeValueAsString(new Message(result));

		} catch (Exception e) {
			LOGGER.error("Upload route File to Gateway Admin Fail.");

			return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
		}
	}

}
