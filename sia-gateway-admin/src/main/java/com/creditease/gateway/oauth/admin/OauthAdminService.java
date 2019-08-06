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

import com.creditease.gateway.oauth.AuthInfo;
import com.creditease.gateway.oauth.server.OauthServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 认证管理服务
 * 
 * @author peihua
 */

@Service
public class OauthAdminService {

	@Autowired
	private OauthAdminRepository clientRepo;

	@Autowired
	OauthServer authserver;

	public boolean createClient(AuthInfo client) {

		boolean rst = false;

		try {
			rst = clientRepo.createClient(client);

		} catch (DuplicateKeyException e) {
			rst = updateClient(client);

		} catch (Exception e) {
		}

		return rst;
	}

	public boolean updateClient(AuthInfo client) {

		// first update cache
		authserver.getSecretcache().put(client.getRouteid(), client.getClient_secret());
		return clientRepo.updateClient(client);
	}

	public boolean updateClientToken(String clientId, String token, String tokenUpdateTime) {

		return clientRepo.updateClientToken(clientId, token, tokenUpdateTime);
	}

	public void deleteClient(AuthInfo client) {

		clientRepo.deleteClient(client);
	}

	public List<AuthInfo> findAll() {

		return clientRepo.findAll();
	}

	public AuthInfo findByClientId(String clientId) {

		return clientRepo.findByClientId(clientId);
	}

	public AuthInfo findByClientSecret(String routeid, String clientSecret) {
		return clientRepo.findByClientSecret(routeid, clientSecret);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean checkClientSecretValid(String routeid, Date date) {

		String startTime = clientRepo.findByClientId(routeid).getStartTime();
		String endTime = clientRepo.findByClientId(routeid).getEndTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = dateFormat.format(date);
		if (nowTime.compareTo(startTime) < 0 || nowTime.compareTo(endTime) > 0) {
			return false;
		}
		return true;
	}

	public void deleteToken(String routeid) {

		clientRepo.deleteToken(routeid);
	}

}
