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


package com.creditease.gateway.oauth.admin;

import java.util.List;

import com.creditease.gateway.constant.GatewayConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.oauth.AuthInfo;

/**
 * 认证管理Repository
 * 
 * @author peihua
 */

@Repository
public class OauthAdminRepository {

	@Autowired
	@Qualifier(GatewayConstant.JDBCTEMPLATENAME)
	protected JdbcTemplate adminJdbcTemplate;

	private static final String INSERTAUTHINFO = "INSERT INTO gateway_oauth_access_token (routeid, client_secret, token, startTime,endTime) values(?,?,?,?,?)";

	private static final String UPDATEAUTHINFO = "update gateway_oauth_access_token set client_secret = ? ,startTime=? ,endTime=? where routeid =?";

	private static final String UPDATEAUTHTOKEN = "update gateway_oauth_access_token set token = ? ,tokenUpdateTime=? where routeid =?";

	private static final String DELETEAUTHINFO = "DELETE FROM gateway_oauth_access_token where routeid='%s'";

	private static final String QUERYAUTHINFO = "select * from gateway_oauth_access_token where routeid='%s' ";

	private static final String QUERYALLAUTHINFO = "select * from gateway_oauth_access_token";

	private static final String DELETETOKEN = "update gateway_oauth_access_token set token = null , tokenUpdateTime = null where routeid ='%s'";

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAdminRepository.class);

	public boolean createClient(AuthInfo client) {

		try {

			LOGGER.info("OauthAdminRepository createClient routeid:{}", client.getRouteid());

			adminJdbcTemplate.update(INSERTAUTHINFO, client.getRouteid(), client.getClient_secret(), null,

					client.getStartTime(), client.getEndTime());

			LOGGER.info("Create AuthClient success!");
			return true;

		} catch (Exception e) {
			throw e;
		}
	}

	public boolean updateClient(AuthInfo client) {

		try {

			LOGGER.info("OauthAdminRepository update client:{}", client.getRouteid());

			int rst = adminJdbcTemplate.update(UPDATEAUTHINFO, client.getClient_secret(), client.getStartTime(),
					client.getEndTime(), client.getRouteid());

			LOGGER.info("> update client success, status is: " + rst);

			return true;

		} catch (Exception e) {

			LOGGER.error(e.getMessage());

			return false;
		}

	}

	public boolean updateClientToken(String clientid, String token, String tokenUpdateTime) {

		try {

			LOGGER.info("updateClientToken ,clientid{}, token:{}", clientid, token);
			int rst = adminJdbcTemplate.update(UPDATEAUTHTOKEN, token, tokenUpdateTime, clientid);
			LOGGER.info("updateClientToken success, status is: {}" + rst);

			return true;

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return false;
		}

	}

	public int deleteClient(AuthInfo client) {

		int rst = 0;
		try {
			String deleteSQL = StringHelper.format(DELETEAUTHINFO, client.getRouteid());

			LOGGER.info("DBRepository delete AUTH obj deleteSQL: {}", deleteSQL);
			rst = adminJdbcTemplate.update(deleteSQL);
			LOGGER.info("DBRepository delete AUTH obj result:{} ", rst);

		} catch (DataAccessException e) {
			throw e;
		}
		return rst;
	}

	public List<AuthInfo> findAll() {

		try {
			List<AuthInfo> results = adminJdbcTemplate.query(QUERYALLAUTHINFO,
					new BeanPropertyRowMapper<>(AuthInfo.class));
			return results;

		} catch (EmptyResultDataAccessException e) {
			return null;

		} catch (DataAccessException e) {
			throw e;
		}

	}

	public AuthInfo findByClientId(String routeid) {
		try {

			String querySQL = StringHelper.format(QUERYAUTHINFO, routeid);
			LOGGER.info("findByClientSecret SQL:{}" + querySQL);

			RowMapper<AuthInfo> rm = BeanPropertyRowMapper.newInstance(AuthInfo.class);
			AuthInfo info = adminJdbcTemplate.queryForObject(querySQL, rm);

			return info;

		} catch (EmptyResultDataAccessException e) {
			return null;

		} catch (DataAccessException e) {
			throw e;
		}

	}

	public AuthInfo findByClientSecret(String routeid, String clientSecret) {

		try {

			String querySQL = StringHelper.format(QUERYAUTHINFO, routeid);

			LOGGER.info("findByClientSecret SQL:{}", querySQL);

			RowMapper<AuthInfo> rm = BeanPropertyRowMapper.newInstance(AuthInfo.class);

			AuthInfo info = adminJdbcTemplate.queryForObject(querySQL, rm);

			String secr = info.getClient_secret();

			if (clientSecret.equals(secr)) {
				LOGGER.info("findByClientSecret clientSecret:{}", clientSecret);
				LOGGER.info(" findByClientSecret secr:{}", secr);
				return info;
			}

		} catch (DataAccessException e) {
			throw e;
		}
		return null;

	}

	public int deleteToken(String routid) {

		int rst = 0;

		try {

			String deleteSQL = StringHelper.format(DELETETOKEN, routid);

			LOGGER.info("DBRepository delete token  deleteSQL: {}", deleteSQL);
			rst = adminJdbcTemplate.update(deleteSQL);

			LOGGER.info("DBRepository delete token result: {}", rst);

		} catch (DataAccessException e) {
			throw e;
		}
		return rst;
	}
}
