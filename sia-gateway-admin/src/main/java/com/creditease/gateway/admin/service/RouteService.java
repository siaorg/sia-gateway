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

import com.creditease.gateway.admin.domain.QueryObj;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.domain.RouteObj;
import com.creditease.gateway.domain.RouteObjExtend;
import com.creditease.gateway.helper.StringHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 路由管理服务
 * 
 * @author peihua
 *
 *
 */

@Service
public class RouteService extends BaseAdminService {

	public enum RouteStatus {

		/**
		 * EDIT:编辑状态 ONLINE：发布状态 DOWNLINE：下线状态
		 */
		EDIT, ONLINE, DOWNLINE
	}

	/********************************* 路由服务Service ***************************************************/

	/**
	 * 新建路由服务：Route service enabled: false 表示该路由还未生效
	 */
	public int addRoute(RouteObj routeobj) throws Exception {

		routeobj.setRouteStatus(RouteStatus.EDIT.toString());

		int rst = dbRepository.addRoute(routeobj);

		return rst;

	}

	/**
	 * 返回路由列表所有路由信息
	 *
	 */
	public List<RouteObj> getRouteList() throws Exception {

		return dbRepository.getRouteList();
	}

	/**
	 * 根据路由ids返回路由信息，routeIds为空则返回所有路由列表信息
	 */
	public List<RouteObj> getRouteList(List<String> routeIdList) throws Exception {

		return dbRepository.getRouteListByIds(routeIdList);
	}

	/**
	 * 根据routeIdList返回路由信息，routeIds为空则返回所有路由信息
	 */
	public Map<String, RouteObj> getRouteMap(List<String> routeIdList) throws Exception {

		List<RouteObj> list = getRouteList(routeIdList);
		if (list == null) {
			return null;
		}

		Map<String, RouteObj> map = new HashMap<>(8);
		for (RouteObj route : list) {
			map.put(route.getRouteid(), route);
		}

		return map;
	}

	/**
	 * 根据routeid 模糊查询路由
	 *
	 */
	public RouteObj queryRouteByid(QueryObj qo) throws Exception {

		RouteObj rst = dbRepository.queryRouteByid(qo);
		return rst;

	}

	/**
	 * 根据routeid 查询路由绑定COMP
	 *
	 */
	public Map<String, List<String>> queryRouteCompRelations() throws Exception {
		return dbRepository.queryRouteCompRelations();
	}

	/**
	 * 根据Path/routeid/url 模糊 关键字查询路由
	 *
	 */
	public List<RouteObj> queryRoute(QueryObj qo) throws Exception {

		List<RouteObj> rst = dbRepository.queryRouteListbyPath(qo);
		return rst;

	}

	/**
	 * 更新管理路由
	 *
	 */
	public int updateRoute(RouteObj route) throws Exception {

		int rst = dbRepository.updateRoute(route);

		return rst;

	}

	/**
	 * 删除路由
	 *
	 */
	public int deleteRoute(RouteObj obj) {

		try {
			if ((RouteStatus.ONLINE.toString().equals(obj.getRouteStatus())) == true) {
				LOGGER.info("the route is under publish status");
				return GatewayConstant.CLIENT_ERROR;

			} else {
				int rst = dbRepository.deleteRoute(obj);

				if (rst > 0) {
					this.deleteRouteCompReleations(obj);
				}
				return rst;
			}
		} catch (Exception e) {
			LOGGER.error("删除路由异常，routeId: {},请检查 ！", obj.getRouteid(), e);
			return GatewayConstant.SERVER_ERROR;
		}
	}

	/**
	 * 发布路由
	 *
	 */
	public int publisheRoute(RouteObj route) throws Exception {

		/**
		 * step1:根據routeId更新Route的状态
		 *
		 */
		try {
			route.setRouteStatus(RouteStatus.ONLINE.toString());

			int rst = dbRepository.updateRoutebyStatus(route);

			LOGGER.info("publisheRoute step1 rst:" + rst);

			/**
			 * step2:根據zuulName取得ZUUL-Instnace
			 *
			 */

			List<String> zuulList = zuulDiscovery.getServiceList(super.getZuulGroupName(route.getRouteid()));

			/**
			 *
			 * Step3: 调用所有ZUUL-Instnace的/refresh接口同步DB到LocalCache完成同步
			 */
			for (String path : zuulList) {
				String url = "http://" + path;
				String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.RR.getValue(), null);

				LOGGER.info("refreshRoute rst:" + result);
			}

			return GatewayConstant.OK;

		} catch (Exception e) {

			LOGGER.error(">>> Exception:" + e.getCause());

			return GatewayConstant.SERVER_ERROR;
		}

	}

	/**
	 * 下綫路由
	 *
	 */
	public int downRoute(RouteObj route) throws Exception {
		/**
		 * step1:根據routeId更新Route的状态
		 *
		 */
		try {
			route.setRouteStatus(RouteStatus.DOWNLINE.toString());

			int rst = dbRepository.updateRoutebyStatus(route);

			LOGGER.info("downRoute step1 rst:" + rst);

			/**
			 * step2:根據zuulName取得ZUUL-Instnace
			 *
			 */

			if (rst != 0) {

				LOGGER.info("downRoute step2 failed:" + rst);

				return rst;
			}

			List<String> zuulList = zuulDiscovery.getServiceList(super.getZuulGroupName(route.getRouteid()));

			/**
			 *
			 * Step3: 调用所有ZUUL-Instnace的/refresh接口同步DB到LocalCache完成同步
			 */
			for (String path : zuulList) {
				String url = "http://" + path;

				String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.RR.getValue(), null);

				LOGGER.info("refreshRoute rst:" + result);
			}

			return GatewayConstant.OK;

		} catch (Exception e) {

			LOGGER.error(">>> Exception:" + e.getCause());

			return GatewayConstant.SERVER_ERROR;
		}
	}

	/**
	 * 加载所有apiName应用名称
	 */
	public List<Map<String, String>> queryApplicationNameList() throws Exception {
		try {
			List<String> apiNames = dbRepository.queryApplicationNameList();

			Map<String, String> nameMaps;
			List<Map<String, String>> applicationNameList = new ArrayList<>();
			for (String apiName : apiNames) {
				nameMaps = Maps.newHashMap();

				if (StringHelper.isEmpty(apiName)) {
					nameMaps.put("name", "空值");
					nameMaps.put("value", "empty");
					applicationNameList.add(0, nameMaps);
				} else {
					nameMaps.put("name", apiName);
					nameMaps.put("value", apiName);
					applicationNameList.add(nameMaps);
				}
			}

			return applicationNameList;
		} catch (Exception e) {
			LOGGER.error("RouteService ---> queryApplicationNameList fail!", e);
			throw e;
		}
	}

	/**
	 * 校验路由path的重复性
	 */
	public boolean inspectRepeat(RouteObj routeobj) throws Exception {
		try {
			return dbRepository.inspectRepeat(routeobj);
		} catch (Exception e) {
			LOGGER.error("添加路由前校验异常，zuulGroupName : {} , path: {} ,请检查！", routeobj.getZuulGroupName(),
					routeobj.getPath(), e);
			throw e;
		}
	}

	public List<RouteObjExtend> getRouteData(List<RouteObj> routeList, Map<String, List<String>> filterComp) {
		List<RouteObjExtend> routeExtendList = new ArrayList<>();

		if (CollectionUtils.isEmpty(routeList)) {
			return routeExtendList;
		}
		routeExtendList = routeList.stream().map(x -> {
			String routeId = x.getRouteid();
			RouteObjExtend routeExtend = new RouteObjExtend(x, filterComp.get(routeId));
			return routeExtend;
		}).collect(Collectors.toList());

		return routeExtendList;
	}

	public void deleteRouteCompReleations(RouteObj routeObj) throws Exception {
		try {
			String routeId = routeObj.getRouteid();
			// 获得所有上传组件信息
			List<CompInfo> compInfos = dbRepository.getCompInfos();

			for (CompInfo compInfo : compInfos) {
				String routeStr = compInfo.getRouteidList();
				String compFilterName = compInfo.getCompFilterName();
				if (StringHelper.isEmpty(routeStr)) {
					continue;
				}

				// 使用浅拷贝，避免下面remove出错
				List<String> routeList = new ArrayList<>(Arrays.asList(routeStr.split(";")));

				if (routeList.contains(routeId)) {
					routeList.remove(routeId);

					String routesValue = Joiner.on(";").join(routeList);

					// 更新组件与路由的关系 compFilterName字段在表中为unique 故不会出现多删情况
					compDBRepository.bindRoute(routesValue, compFilterName);
				}
			}

		} catch (Exception e) {
			LOGGER.error("删除路由与组件关系异常，method : deleteRouteCompReleations ,请检查！{}", e);
			throw e;
		}
	}
}
