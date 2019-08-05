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

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.oauth.AuthInfo;
import com.creditease.gateway.oauth.server.OauthServer;

import io.swagger.annotations.ApiOperation;

/**
 * 认证服务WEB接口
 * 
 * @author peihua
 * 
 * */

@RestController
public class OauthAdminController {

    /**
     * 存储路由安全认证服务
     *
     */
	private static final Logger LOGGER = LoggerFactory.getLogger(OauthAdminController.class);
	
    @Autowired
	private OauthAdminService oauthadminservice;
    
    @Autowired
    private OauthServer oauthserver ;
    
    @ApiOperation(value = "saveAuth")
    @RequestMapping(value = "/saveAuth", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String saveAuth(@RequestBody AuthInfo authinfo)  {
    	
    	try {
			/**
			 * 口令过期就删除token
			 */
    		String routeid = authinfo.getRouteid();
    		String secret = authinfo.getClient_secret();
    		Map<String, String> tockenCache = oauthserver.getTockencache();
    		Map<String, String> secretCache = oauthserver.getSecretcache();
    		
    		
			if(tockenCache.get(routeid) != null 
					&& secretCache.get(routeid) != null 
					&& ! secret.equals(secretCache.get(routeid))){
				oauthadminservice.deleteToken(routeid);
				oauthserver.removeToken(routeid);
			}

			/**
			 * step1: 存储AUTH到数据库
			 *
			 * */
    		boolean rst = oauthadminservice.createClient(authinfo);
			/**
    		 * step2: 同步AUTH信息到OAUTHServer缓存中
    		 * 
    		 * ***/
    		
    		oauthserver.addAuthCode(authinfo.getRouteid(), authinfo.getClient_secret());
    		
    		Message resp = new Message(rst);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);
    		
    	} catch (Exception e) {
    		LOGGER.error("Exception :{}",e.getCause());
			new GatewayException(ExceptionType.AdminException,e);
		}
		return null;

    }
    
    @ApiOperation(value = "queryAuth")
    @RequestMapping(value = "/queryAuth", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryAuth(@RequestBody AuthInfo authinfo)  {
    	
    	try {
    		
    		AuthInfo auth = oauthadminservice.findByClientId(authinfo.getRouteid());
    		
    		if(auth!=null)
    		{
    			oauthserver.addAuthCode(auth.getRouteid(), auth.getClient_secret());
        		
        		Message resp = new Message(auth);
    			ObjectMapper mapper = new ObjectMapper();
    			return mapper.writeValueAsString(resp);
    		}
    		else{
    			Message resp = new Message();
    			ObjectMapper mapper = new ObjectMapper();
    			return mapper.writeValueAsString(resp);
    		}
    		
    		
    	} catch (Exception e) {
    		LOGGER.error("Exception :{}",e.getCause().getMessage());
			new GatewayException(ExceptionType.AdminException,e);
		}
		return null;

    }

    @ApiOperation(value = "deleteAuth")
    @RequestMapping(value = "/deleteAuth", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteAuth(@RequestBody AuthInfo authinfo)  {
    	
    	try {
    		
    		/**
    		 * step1: 删除AUTH
    		 * 
    		 * ***/
    		oauthadminservice.deleteClient(authinfo);
    		
       		/**
    		 * step2: 从缓存中删除AUTH
    		 * 
    		 * ***/
    		oauthserver.removeAuthCode(authinfo.getRouteid());
    		
    		Message resp = new Message(true);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(resp);
    		
    	} catch (Exception e) {
    		LOGGER.error("Exception :{}",e.getCause().getMessage());
			new GatewayException(ExceptionType.AdminException,e);
		}
		return null;

    }
}
