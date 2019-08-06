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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.domain.RouteObj;
import com.creditease.gateway.helper.IoHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;

/**
 * 组件管理服务
 * 
 * @author peihua
 * 
 */

@Service
public class ComponentService extends BaseAdminService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

	/**
	 * 查看queryCompDetail
	 * 
	 */
	public CompInfo queryCompDetail(String compFilterName) throws Exception {

		return compDBRepository.queryCompDetail(compFilterName);
	}

	/**
	 * 取得getComplist
	 * 
	 */
	public List<CompInfo> getComplist() throws Exception {

		List<CompInfo> list4comp = compDBRepository.queryCompList();
		List<RouteObj> list4route = dbRepository.getRouteList();
		List<String> routeList = new ArrayList<String>();

		for (RouteObj obj : list4route) {
			routeList.add(obj.getRouteid());
		}

		for (CompInfo comp : list4comp) {
			String ids = comp.getRouteidList();

			if (StringHelper.isEmpty(ids)) {
				continue;
			}

			String[] allbindIds = ids.split(";");

			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<String> lst = new CopyOnWriteArrayList(Arrays.asList(allbindIds));

			// filter過濾不属于自己的Routeid
			for (String id : lst) {
				if (!routeList.contains(id)) {
					lst.remove(id);
				}
			}
			comp.setContext(lst);
		}
		return list4comp;
	}

	/**
	 * 綁定路由
	 * 
	 */
	public boolean compBindRoute(List<String> request, String compFilterName) throws Exception {

		boolean rst = false;

		StringBuffer routeidsBuffer = new StringBuffer();

		for (String routeid : request) {
			routeidsBuffer.append(routeid);
			routeidsBuffer.append(";");
		}

		rst = compDBRepository.bindRoute(routeidsBuffer.toString(), compFilterName);
		if (rst) {
			remoteCall(getZuulGroupName());
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 遠端ZUUL-WEB接口調用
	 * 
	 */
	public void remoteCall(String groupName) throws Exception {

		List<String> zuulList = zuulDiscovery.getServiceList(groupName);

		for (String path : zuulList) {
			String url = "http://" + path;
			String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.BRR.getValue(), new Message());
			LOGGER.info("remoteCall rst:{}", result);
		}

	}

	public boolean removeComponent(String filterName) throws Exception {

		try {
			List<String> zuulList = zuulDiscovery.getServiceList(getZuulGroupName());

			/**
			 * step0: 删除Admin上得组件槽位
			 */
			String filterDir = getPluginPath(super.getZuulGroupName());

			File f = new File(filterDir);

			String jarname = null;

			if (f.exists()) {
				File[] files = f.listFiles();

				for (int i = 0; i < files.length; i++) {
					File fs = files[i];
					if (fs.getName().startsWith(filterName)) {
						jarname = fs.getName();
						break;
					}
				}
			}
			String filename = filterDir + File.separator + jarname;
			LOGGER.info(">> removeComponent filename：{}", filename);

			File fileremove = new File(filename);
			if (fileremove != null) {

				String path = fileremove.getPath();
				if (com.creditease.gateway.helper.JvmToolHelper.isWindows()) {
					path = path.replace("/", "\\");
					path = fileremove.getAbsolutePath();
				} else {
					path = fileremove.getAbsolutePath();
				}
				int rst = IoHelper.deleteFile(path);
				LOGGER.info("> removeComponent 删除结果：{}", rst);
			}
			/**
			 * step1: 通知所有ZUUL-instantce卸载Componet
			 */
			for (String path : zuulList) {

				String url = "http://" + path;
				Map<String, String> map = new HashMap<String, String>(16);
				map.put("compFilterName", filterName);

				Message msg = new Message(map);
				String result = handler.executeHttpCmd(url, GatewayConstant.ADMINOPTKEY.CPR.getValue(), msg);
				LOGGER.info("remoteCall rst:{}", result);
			}
			/**
			 * step2: 删除数据库
			 */
			compDBRepository.deleteComponent(filterName);

		} catch (Exception e) {
			LOGGER.error(">removeComponent Exception:{}", e.getCause());
			return false;
		}
		return true;
	}

	public List<CompInfo> getBizComplist() throws Exception {

		List<CompInfo> list4Biz = compDBRepository.queryBizCompList();
		return list4Biz;
	}
}
