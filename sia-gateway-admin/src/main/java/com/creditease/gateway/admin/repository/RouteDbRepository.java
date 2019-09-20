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


package com.creditease.gateway.admin.repository;

import com.creditease.gateway.admin.domain.QueryObj;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.admin.repository.base.BaseAdminRepository;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.domain.RouteObj;
import com.creditease.gateway.helper.StringHelper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 路由访问数据库功能
 * 
 * @author peihua
 */

@Repository
public class RouteDbRepository extends BaseAdminRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteDbRepository.class);

	private static final String EMPTY = "empty";
	private static final String SEPRATOR = ";";

	@Autowired
	@Qualifier(GatewayConstant.JDBCTEMPLATENAME)
	protected JdbcTemplate adminJdbcTemplate;

	@Autowired
	AuthInterceptor authInterceptor;

	private static final String QUERYROUTECOMPLIST = "select * from gateway_component  where  1 = 1  ";

	private static final String ORDERBYSTR = " order by id  ";

	private static final String INSERTROUTE = "INSERT INTO gateway_route (id, path, serviceId, url, retryable, enabled,stripPrefix,apiName,zuulGroupName,routeStatus,strategy) values(?,?,?,?,?, ?,?,?,?,?,?)";

	private static final String UPDATEROUTE = "UPDATE gateway_route SET path='%s', serviceId='%s', url='%s', retryable=%s, enabled=%s,stripPrefix=%s,apiName='%s',zuulGroupName='%s' ,routeStatus='%s' ,strategy='%s'  where id='%s'";

	private static final String DELETEROUTE = "DELETE FROM gateway_route where id='%s'";

	private static final String SELECTSTRING = " id as routeid,path,serviceid,url,retryable,enabled,stripPrefix,apiName,zuulGroupName,routeStatus,strategy";

	private static final String GETROUTELIST = "select " + SELECTSTRING + "  from gateway_route ";

	private static final String GETROUTEBYID = "select " + SELECTSTRING + " from gateway_route where id ='%s' ";

	private static final String GETBYPATH = "select " + SELECTSTRING + " from gateway_route where path like %s ";

	private static final String QUERYROUTELISTBYURL = "select " + SELECTSTRING
			+ " from gateway_route  where ( url like %s or serviceid like %s ) ";

	private static final String QUERYROUTELISTBYAPINAME = "select " + SELECTSTRING
			+ " from gateway_route  where apiName = %s";

	private static final String QUERYROUTELISTBYROUTEID = "select " + SELECTSTRING
			+ " from gateway_route where id like %s";

	private static final String UPDATEROUTESTATUS = "UPDATE gateway_route SET routeStatus='%s'  where id='%s'";

	private static final String GETAPPLICATIONNAMES = " select distinct apiName from gateway_route where 1 =1 ";

	private static final String GETCOUNTBYGROUPNAMEANDPATH = "select count(*) from gateway_route where zuulGroupName = %s and path = %s ";

	public int addRoute(RouteObj routeobj) throws Exception {

		int rst = 0;
		try {
			if (StringHelper.isEmpty(routeobj.getUrl())) {

				rst = adminJdbcTemplate.update(INSERTROUTE, routeobj.getRouteid(), routeobj.getPath(),
						routeobj.getServiceId(), null, routeobj.getRetryable(), routeobj.getEnabled(),
						routeobj.isStripPrefix(), routeobj.getApiName(), routeobj.getZuulGroupName(),
						routeobj.getRouteStatus(), routeobj.getStrategy());
			} else {

				rst = adminJdbcTemplate.update(INSERTROUTE, routeobj.getRouteid(), routeobj.getPath(),
						routeobj.getServiceId(), routeobj.getUrl(), routeobj.getRetryable(), routeobj.getEnabled(),
						routeobj.isStripPrefix(), routeobj.getApiName(), routeobj.getZuulGroupName(),
						routeobj.getRouteStatus(), routeobj.getStrategy());
			}

			LOGGER.info("insert route, routeid:[{}], path:[{}], insert result:[{}]", routeobj.getRouteid(),
					routeobj.getPath(), rst);
		} catch (DataAccessException e) {

			LOGGER.info("addRoute errror message:[{}]", e.getMessage(), e);
			if (e.getMessage().contains(GatewayConstant.PATH_CONFLICT)) {
				return GatewayConstant.PATH_CONFLICT_ERROR;
			}
			if (e.getMessage().contains(GatewayConstant.ROUTEID_CONFLICT)) {
				return GatewayConstant.ROUTIE_CONFLICT_ERROR;
			}
			return GatewayConstant.CLIENT_ERROR;
		} catch (Exception e) {
			return GatewayConstant.SERVER_ERROR;
		}
		return GatewayConstant.OK;
	}

	public int updateRoute(RouteObj routeobj) throws Exception {

		LOGGER.info("DBRepository update route obj..");
		try {
			String formatupdate = StringHelper.format(UPDATEROUTE, routeobj.getPath(), routeobj.getServiceId(),
					routeobj.getUrl(), routeobj.getRetryable(), routeobj.getEnabled(), routeobj.isStripPrefix(),
					routeobj.getApiName(), routeobj.getZuulGroupName(), routeobj.getRouteStatus(),
					routeobj.getStrategy(), routeobj.getRouteid());

			LOGGER.info("DBRepository updateRoute SQL: {}", formatupdate);
			int rst = adminJdbcTemplate.update(formatupdate);
			LOGGER.info("DBRepository update route obj result:{} ", rst);

		} catch (DataAccessException e) {
			return GatewayConstant.CLIENT_ERROR;
		} catch (Exception e) {
			return GatewayConstant.SERVER_ERROR;
		}
		return GatewayConstant.OK;
	}

	public int updateRoutebyStatus(RouteObj routeobj) throws Exception {

		LOGGER.info("DBRepository update route obj..");
		try {
			String updateRoutebyidSQL = StringHelper.format(UPDATEROUTESTATUS, routeobj.getRouteStatus(),
					routeobj.getRouteid());

			LOGGER.info("DBRepository updateRoutebyStatus SQL:{} ", updateRoutebyidSQL);
			int rst = adminJdbcTemplate.update(updateRoutebyidSQL);
			LOGGER.info("DBRepository update route obj result:{} ", rst);

		} catch (DataAccessException e) {
			return GatewayConstant.CLIENT_ERROR;
		} catch (Exception e) {
			return GatewayConstant.SERVER_ERROR;
		}
		return GatewayConstant.OK;
	}

	public int deleteRoute(RouteObj routeobj) throws Exception {

		LOGGER.info("DBRepository delete route obj..");
		try {

			String deleteSQL = StringHelper.format(DELETEROUTE, routeobj.getRouteid());
			LOGGER.info("DBRepository delete route obj deleteSQL: {}", deleteSQL);

			int rst = adminJdbcTemplate.update(deleteSQL);
			LOGGER.info("DBRepository delete route obj result: {}", rst);

		} catch (DataAccessException e) {
			return GatewayConstant.CLIENT_ERROR;
		} catch (Exception e) {
			return GatewayConstant.SERVER_ERROR;
		}
		return GatewayConstant.OK;

	}

	public List<RouteObj> getRouteList() throws Exception {

		LOGGER.info("queryRouteList");

		String querySQL = getSqlStrByGroupName(GETROUTELIST) + " order by zuulGroupName";

		List<RouteObj> results = adminJdbcTemplate.query(querySQL, new BeanPropertyRowMapper<>(RouteObj.class));

		LOGGER.info("getRouteList success!");

		return results;

	}

	public List<RouteObj> getRouteListByIds(List<String> routeIdList) throws Exception {

		String querySQL = getSqlStrByGroupAndIds(GETROUTELIST, routeIdList) + " order by zuulGroupName";

		List<RouteObj> results = adminJdbcTemplate.query(querySQL, new BeanPropertyRowMapper<>(RouteObj.class));

		LOGGER.info("getRouteListByIds ids success!");

		return results;

	}

	public RouteObj queryRouteByid(QueryObj obj) throws Exception {

		String routeid = obj.getRouteid();

		String querySQL = StringHelper.format(GETROUTEBYID, routeid);

		LOGGER.info("queryRouteByid querySQL:{}", querySQL);

		RowMapper<RouteObj> rm = BeanPropertyRowMapper.newInstance(RouteObj.class);

		RouteObj info = adminJdbcTemplate.queryForObject(querySQL, rm);

		return info;

	}

	public List<RouteObj> queryRouteListbyPath(QueryObj obj) throws Exception {
		// RowMapperResultSetExtractor
		LOGGER.info("queryRouteListbyPath");

		String routeid = obj.getRouteid();

		String path = obj.getPath();

		String url = obj.getUrl();

		String apiName = obj.getApiName();

		String formatquery = null;

		if (!StringHelper.isEmpty(routeid)) {
			// 为路由id字段加转义符
			routeid = routeid.replaceAll("\\_", "\\\\_").replaceAll("\\%", "\\\\%");
			formatquery = StringHelper.format(QUERYROUTELISTBYROUTEID, StringHelper.appendLike(routeid));

		} else if (!StringHelper.isEmpty(path)) {

			formatquery = StringHelper.format(GETBYPATH, StringHelper.appendLike(obj.getPath()));

		} else if (!StringHelper.isEmpty(url)) {

			formatquery = StringHelper.format(QUERYROUTELISTBYURL, StringHelper.appendLike(obj.getUrl()),
					StringHelper.appendLike(obj.getUrl()));

		} else if (!StringHelper.isEmpty(apiName)) {
			if (EMPTY.equals(apiName)) {
				formatquery = QUERYROUTELISTBYAPINAME.substring(0, QUERYROUTELISTBYAPINAME.lastIndexOf("apiName"))
						+ " ( apiName is null or apiName = '' ) ";
			} else {
				formatquery = StringHelper.format(QUERYROUTELISTBYAPINAME, "'" + obj.getApiName() + "'");
			}

		} else {
			LOGGER.info("the request is missing path:" + path + " or url:" + url);
			return null;
		}

		formatquery = super.getSqlStrWithoutWhere(formatquery);

		LOGGER.info("formatquery:{}", formatquery);

		List<RouteObj> results = adminJdbcTemplate.query(formatquery,

				new BeanPropertyRowMapper<>(RouteObj.class));

		return results;

	}

	public List<RouteObj> getRouteInstanceList() throws Exception {

		LOGGER.info("queryRouteList");

		List<RouteObj> results = adminJdbcTemplate.query(GETROUTELIST,

				new BeanPropertyRowMapper<>(RouteObj.class));

		LOGGER.info("get route list success!");

		return results;

	}

	public Map<String, List<String>> queryRouteCompRelations() throws Exception {

		LOGGER.info("queryRouteCompRelations");

		Map<String, List<String>> routeCompMaps;
		try {
			List<CompInfo> results = this.getCompInfos();

			routeCompMaps = Maps.newHashMap();

			for (CompInfo comp : results) {
				String compName = comp.getCompName();

				String routeidList = comp.getRouteidList();

				if (StringHelper.isEmpty(routeidList) || routeidList.split(SEPRATOR).length == 0) {
					LOGGER.info("routeidList is empty!");
					continue;
				}

				for (String routeId : routeidList.split(SEPRATOR)) {
					if (!routeCompMaps.containsKey(routeId)) {
						List<String> list = new ArrayList<>();
						list.add(compName);
						routeCompMaps.put(routeId, list);
					} else {
						List<String> list = routeCompMaps.get(routeId);
						list.add(compName);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("加载路由组件关系失败，请检查！", e);
			throw e;
		}

		LOGGER.info("get queryRouteCompRelations success!");

		return routeCompMaps;
	}

	public List<CompInfo> getCompInfos() throws Exception {
		try {
			String formatquery = super.getSqlStrWithoutWhere(QUERYROUTECOMPLIST);
			formatquery = formatquery + " or zuulGroupName = 'ALL' " + ORDERBYSTR;
			return adminJdbcTemplate.query(formatquery, new BeanPropertyRowMapper<>(CompInfo.class));
		} catch (Exception e) {
			LOGGER.error("加载路由组件关系失败，请检查！", e);
			throw e;
		}
	}

	public List<String> queryApplicationNameList() throws Exception {

		List<String> info;
		try {
			String querySQL = StringHelper.format(GETAPPLICATIONNAMES);

			querySQL = super.getSqlStrWithoutWhere(querySQL);

			LOGGER.info("querySQL:" + querySQL);

			info = adminJdbcTemplate.queryForList(querySQL, String.class);

		} catch (DataAccessException e) {
			LOGGER.info("RouteDBRepository queryApplicationNameList fail!", e);
			throw e;
		}
		return info;
	}

	public boolean inspectRepeat(RouteObj routeobj) throws Exception {
		try {
			String querySQL = StringHelper.format(GETCOUNTBYGROUPNAMEANDPATH, "'" + routeobj.getZuulGroupName() + "'",
					"'" + routeobj.getPath() + "'");
			LOGGER.info("querySQL:" + querySQL);
			int count = adminJdbcTemplate.queryForObject(querySQL, Integer.class);

			// false : 不重复 ； true : 重复
			return 0 == count ? false : true;
		} catch (Exception e) {
			LOGGER.error("添加路由前校验异常，zuulGroupName : {} , path: {} ,请检查！", routeobj.getZuulGroupName(),
					routeobj.getPath(), e);
			throw e;
		}
	}
}
