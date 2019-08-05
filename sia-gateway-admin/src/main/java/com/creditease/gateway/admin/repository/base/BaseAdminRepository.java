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


package com.creditease.gateway.admin.repository.base;

import com.creditease.gateway.admin.domain.common.PageQuery;
import com.creditease.gateway.admin.domain.common.PaginateList;
import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * Repository 公共接口
 *
 * @author peihua
 */

public class BaseAdminRepository {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseAdminRepository.class);

	@Autowired
	public AuthInterceptor authCheckor;

	@Autowired
	public RedisUtil redisUtil;

	@Autowired
	@Qualifier(GatewayConstant.JDBCTEMPLATENAME)
	protected JdbcTemplate adminJdbcTemplate;

	public String getGroupName() {

		String zuulGroupName = authCheckor.getZuulGroupName();

		return zuulGroupName;
	}

	public String getSqlStrByGroupName(String sql) {

		String zuulGroupName = authCheckor.getZuulGroupName();

		String querySQL = sql;
		if (!zuulGroupName.equals(GatewayConstant.API_GATEWAY_CORE)) {
			querySQL = sql + " where zuulGroupName = " + "'" + zuulGroupName + "'";
		}

		return querySQL;
	}

	public String getSqlStrByGroupAndIds(String sql, List<String> routeIdList) {

		if (routeIdList == null || routeIdList.isEmpty()) {
			return getSqlStrByGroupName(sql);
		}
		StringBuilder sb = new StringBuilder("'");
		for (String id : routeIdList) {
			sb.append(id).append("','");
		}
		String querySQL = sql + " where";
		String zuulGroupName = authCheckor.getZuulGroupName();
		if (!zuulGroupName.equals(GatewayConstant.API_GATEWAY_CORE)) {
			querySQL += " zuulGroupName = '" + zuulGroupName + "' and";
		}
		return querySQL + " id in (" + sb.toString().substring(0, sb.length() - 2) + ")";
	}

	public String getSqlStrWithoutWhere(String sql) {

		String zuulGroupName = authCheckor.getZuulGroupName();
		LOGGER.info("zuulGroupName:[{}]", zuulGroupName);

		String querySQL = null;

		if (zuulGroupName.equals(GatewayConstant.API_GATEWAY_CORE)) {

			querySQL = sql;

		} else {
			querySQL = sql + " and zuulGroupName = " + "'" + zuulGroupName + "'";
		}

		return querySQL;
	}

	/**
	 * 获取排序sql
	 * 
	 * @param pageQuery
	 * @return
	 */
	public String getTailSql(PageQuery pageQuery) {
		StringBuffer stringBuffer = new StringBuffer();
		String orderColumns = pageQuery.getSortName();
		String sortType = pageQuery.getSortType();
		if (!StringUtils.isEmpty(orderColumns)) {
			stringBuffer.append(" order by " + orderColumns);
		}
		if (!StringUtils.isEmpty(sortType)) {
			stringBuffer.append(" " + sortType);
		}
		stringBuffer.append(" limit " + pageQuery.getStartRow() + "," + pageQuery.getPageSize());

		return stringBuffer.toString();
	}

	/**
	 * 初始化 paginateList
	 * 
	 * @param args
	 * @param countSQL
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PaginateList getPaginateList(PageQuery args, String countSQL) {
		int total = adminJdbcTemplate.queryForObject(countSQL, Integer.class);
		PaginateList paginateList = new PaginateList(args.getPageNo(), args.getPageSize(), total);
		args.setTotal(total);
		return paginateList;
	}

}
