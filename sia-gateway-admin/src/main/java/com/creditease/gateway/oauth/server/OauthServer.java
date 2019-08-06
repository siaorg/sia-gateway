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


package com.creditease.gateway.oauth.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.creditease.gateway.admin.domain.AdminInfo;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.message.ZuulHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.creditease.gateway.oauth.admin.OauthAdminService;
import com.google.common.collect.Maps;

/**
 * 认证管理中心Service
 * 
 * @author peihua 
 * 
 */

@Service("OauthServer")
public class OauthServer {

	@Value("${token.expiration}")
	private long expiration;

	private static final int MILISECONDS = 1000;

	private static final String DEADSTATUS = "dead";

	@Autowired
	ZuulHandler handler;

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthServer.class);

	private Map<String, String> secretcache = Maps.newConcurrentMap();

	private Map<String, String> tockenCache = Maps.newConcurrentMap();

	private Map<String, String> tokenUpdateTimecache = Maps.newConcurrentMap();

	public Map<String, String> getTokenUpdateTimecache() {

		return tokenUpdateTimecache;
	}

	public void setTokenUpdateTimecache(Map<String, String> tokenUpdateTimecache) {

		this.tokenUpdateTimecache = tokenUpdateTimecache;
	}

	public Map<String, String> getSecretcache() {

		return secretcache;
	}

	public void setSecretcache(Map<String, String> secretcache) {

		this.secretcache = secretcache;
	}

	public Map<String, String> getTockencache() {

		return tockenCache;
	}

	public void setTockencache(Map<String, String> tockencache) {

		tockenCache = tockencache;
	}

	@Autowired
	private OauthAdminService oAuthAdminService;

	@Autowired
	private AdminService adminService;

	@Autowired
	public OauthServer() {

		LOGGER.info("认证管理中心启动！");
	}

	public void addAuthCode(String routeid, String secret) {

		secretcache.put(routeid, secret);
	}

	public void addAccessToken(String routeid, String accessToken, Date date) {

		tockenCache.put(routeid, accessToken);
		// 缓存中增加token生效的时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		tokenUpdateTimecache.put(accessToken, sdf.format(date));
		oAuthAdminService.updateClientToken(routeid, accessToken, sdf.format(date));
	}

	public void removeAuthCode(String routeid) {

		secretcache.remove(routeid);
	}

	public String getTokenbyClientID(String routeid) {

		return tockenCache.get(routeid);
	}

	public boolean checkAccessToken(String routeid, String accessToken, Date date) throws ParseException {

		String tokeninCache = tockenCache.get(routeid);
		if (tokeninCache != null && tokeninCache.equals(accessToken)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String tokenUpdateTime = tokenUpdateTimecache.get(accessToken);
				String nowTime = sdf.format(date);
				long period = sdf.parse(nowTime).getTime() - sdf.parse(tokenUpdateTime).getTime();
				if (period < getExpireIn() * MILISECONDS) {
					return true;
				}
			} catch (Exception e) {

				LOGGER.info("Exception", e);
			}

		} else {
			return false;
		}
		return false;
	}

	public boolean checkClientId(String clientId) {

		return oAuthAdminService.findByClientId(clientId) != null;
	}

	public boolean checkClientSecret(String routeid, String clientSecret) {

		return oAuthAdminService.findByClientSecret(routeid, clientSecret) != null;
	}

	public long getExpireIn() {

		return expiration;
	}

	public boolean checkClientSecretValid(String routeid, Date date) {

		return oAuthAdminService.checkClientSecretValid(routeid, date);
	}

	public void removeToken(String routeid) {

		tokenUpdateTimecache.remove(tockenCache.get(routeid));
		tockenCache.remove(routeid);

	}

	public void remoteCall() {
		try {
			List<AdminInfo> list = adminService.getAdminListStatus();
			for (AdminInfo admin : list) {

				if ((DEADSTATUS).equals(admin.getAdminStatus())) {

					LOGGER.info("AdminInstanceId:" + admin.getAdminInstanceid() + "admin status is :"
							+ admin.getAdminStatus());
					continue;
				}
				String url = "http://" + admin.getAdminInstanceid() + "/registerAuthCache";
				String result = handler.executeHttpCmd(url);
				LOGGER.info("registerAuthCache result: " + result);
			}
		} catch (Exception e) {
			LOGGER.error("registerAuthCache result: FAIL:{}", e.getMessage());
		}
	}
}
