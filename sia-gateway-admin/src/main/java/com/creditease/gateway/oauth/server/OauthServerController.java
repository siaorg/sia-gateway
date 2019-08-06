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

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.oauth.AuthConstants;
import com.creditease.gateway.oauth.AuthServerRunner;

/**
 * 证管理中心
 * 
 * @author peihua 认
 * 
 */

@RestController
public class OauthServerController {

	@Autowired
	private OauthServer oAuthService;

	@Autowired
	AuthServerRunner authServerRunner;

	private static final Logger LOGGER = LoggerFactory.getLogger(OauthServerController.class);

	@RequestMapping("/oauth/token")
	public HttpEntity<String> token(HttpServletRequest request)
			throws URISyntaxException, OAuthSystemException, ParseException {

		LOGGER.info("安全认证Token获取..");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json; charset=utf-8");
		try {

			// 构建OAuth请求
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
			// clientID --> 对应 routeid
			String clientID = oauthRequest.getClientId();
			String secret = oauthRequest.getClientSecret();
			Date date = new Date();
			LOGGER.info("安全认证Token获取,clientID:{}", clientID);
			LOGGER.info("安全认证Token获取,secret:{}", secret);
			// 检查安全口令是否在有效期内
			if (!oAuthService.checkClientSecretValid(clientID, date)) {
				OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
						.setErrorDescription(AuthConstants.INVALID_TIME).buildJSONMessage();
				return new ResponseEntity<String>(response.getBody(), headers,
						HttpStatus.valueOf(response.getResponseStatus()));
			}
			// 检查客户端安全KEY是否正确
			if (!oAuthService.checkClientSecret(clientID, secret)) {

				OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
						.setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
						.setErrorDescription(AuthConstants.INVALID_CLIENT_ID).buildJSONMessage();
				return new ResponseEntity<String>(response.getBody(), headers,
						HttpStatus.valueOf(response.getResponseStatus()));
			}
			String accessToken = null;
			// 生成Access Token
			OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
			String cachedToken = oAuthService.getTokenbyClientID(clientID);
			// String refreshToken = oauthIssuerImpl.refreshToken();
			if (cachedToken != null && oAuthService.checkAccessToken(clientID, cachedToken, date)) {
				accessToken = cachedToken;
			} else {

				String newToken = oauthIssuerImpl.accessToken();
				accessToken = newToken;
			}
			oAuthService.addAccessToken(clientID, accessToken, date);
			oAuthService.remoteCall();
			// 生成OAuth响应
			OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken).setExpiresIn(String.valueOf(oAuthService.getExpireIn()))
					.buildJSONMessage();

			// 根据OAuthResponse生成ResponseEntity
			return new ResponseEntity<String>(response.getBody(), headers,
					HttpStatus.valueOf(response.getResponseStatus()));

		} catch (OAuthProblemException e) {
			// 构建错误响应
			OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(e)
					.buildJSONMessage();
			return new ResponseEntity<String>(res.getBody(), headers, HttpStatus.valueOf(res.getResponseStatus()));
		}
	}

	/**
	 * 验证accessToken
	 *
	 * @param msg
	 * @return
	 */

	@RequestMapping(value = "/oauth/checkToken", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String checkAccessToken(@RequestBody Message msg) {

		try {
			Map<String, String> request = msg.getRequest();

			String routeid = request.get("routeid");
			String accessToken = request.get("accessToken");

			LOGGER.info("checkAccessToken routeid：{}", routeid);
			LOGGER.info("checkAccessToken accessToken：{}", accessToken);

			boolean b = oAuthService.checkAccessToken(routeid, accessToken, new Date());

			Message resp = new Message(b);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);

		} catch (Exception e) {
			new GatewayException(ExceptionType.AdminException, e);
		}
		return null;
	}

	@RequestMapping(value = "/registerAuthCache", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String addAccessToken() throws Exception {

		try {
			authServerRunner.registerAuthCache();
			return "SUCCESS";
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			return "FAIL";
		}
	}
}
