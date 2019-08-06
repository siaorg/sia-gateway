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


package com.common.system.shiro;

import com.common.system.RedisCacheManager;
import com.common.system.ShiroCache;
import com.common.system.entity.RcRole;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huangqian
 * @Date: 2018/7/18 15:48
 * @Description: 从session中获取用户信息
 */
@Component
@ConditionalOnClass(RedisTemplate.class)
public class ShiroSession {

    private final static Logger LOGGER = LoggerFactory.getLogger(ShiroSession.class);

    @Autowired
    private RedisCacheManager redisCacheManager;

    private static final String SEPRATOR = "@";

    /**
     * getRoleNames   获取用户角色名称
     *
     * @param cookies
     * @return
     */
    public List<String> getRoleNamesByCookie(Cookie[] cookies) {
        String sessionId = null;

        for (Cookie cookie : cookies) {
            if ("jsessionid".equals(cookie.getName())) {
                sessionId = cookie.getValue();
            }
        }
        List<String> roleNames = null;
        try {
            roleNames = getRolesFromShiroSession(sessionId);
        } catch (Exception ex) {
            LOGGER.error("ShiroSession.getRoleNames(Cookie[] cookies) is Exception :", ex);
        }
        return roleNames;
    }

    public String getJSessionIdbyCookie(Cookie[] cookies) {
        String sessionId = null;

        if(cookies== null)
        {
        	return null;
        }

        for (Cookie cookie : cookies) {
            if ("jsessionid".equals(cookie.getName())) {
                sessionId = cookie.getValue();
            }
        }

        return sessionId;
    }
    /**
     * getRoleNames   获取用户角色名称
     *
     * @param jsessionId
     * @return
     */
    public List<String> getRoleNames(String jsessionId) {
        List<String> roleNames = null;
        try {
            roleNames = getRolesFromShiroSession(jsessionId);
        } catch (Exception ex) {
            LOGGER.error(">>>>>>>>>>>> ShiroSession.getRoleNames(String jsessionId) is Exception :", ex);
        }
        return roleNames;
    }

    /**
     * 获取用户名
     *
     * @param cookies
     * @return
     */
    public String getUserName(Cookie[] cookies) {
        String sessionId = null;
        String userName = null;
        for (Cookie cookie : cookies) {
            if ("jsessionid".equals(cookie.getName())) {
                sessionId = cookie.getValue();
            }
        }
        try {
            userName = getUserFromShiroSession(sessionId);
        } catch (Exception ex) {
            LOGGER.error(">>>>>>>> ShiroSession.getUserName(Cookie[] cookies) is Exception :", ex);
        }
        return userName;
    }

    public String getUserName(String jsessionId) {
        String userName = null;
        try {
            userName = getUserFromShiroSession(jsessionId);
        } catch (Exception ex) {
            LOGGER.error(">>>>>>>>> ShiroSession.getUserName(String jsessionId) is Exception :", ex);
        }
        return userName;
    }

    private String getUserFromShiroSession(String jsessionId) {
        String userName = null;
        if (jsessionId != null) {
            try {
                Session shiroSession = getShiroSession(jsessionId);
                if (shiroSession != null) {
                    ShiroUser user = (ShiroUser) shiroSession.getAttribute("user");
                    LOGGER.error(">>>>>>>>> ShiroSession.user :", user);
                    if(user!=null)
                    {
                    	userName = user.getUsername();
                    }

                    if (userName != null && userName.contains(SEPRATOR)) {
                        userName = user.getUsername().substring(0, user.getUsername().indexOf("@"));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(">>>>>>>>> ShiroSession.getUserFromShiroSession() is Exception :", ex);
            }
        }
        return userName;
    }

    private List<String> getRolesFromShiroSession(String jsessionId) {
        List<String> roleNameList = new ArrayList<>();
        if (jsessionId != null) {
            try {
                Session shiroSession = getShiroSession(jsessionId);
                if (shiroSession != null) {
                    ShiroUser shiroUser = (ShiroUser) shiroSession.getAttribute("user");
                    List<RcRole> roles = shiroUser.getRoleList();
                    if (roles.size() > 0) {
                        roles.forEach(tmp -> {
                            roleNameList.add(tmp.getName());
                        });
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(">>>>>>>>ShiroSession.getShiroSession() is Exception :", ex);
            }
        }
        return roleNameList;
    }

    private Session getShiroSession(String jsessionId) {
        Session session = null;
        try {
            ShiroCache shiroCache = (ShiroCache) redisCacheManager.getCache("shiro-activeSessionCache");
            session = (Session) shiroCache.get(jsessionId);
            if (session == null) {
                LOGGER.error(">>>>> redisCacheManager.getCache(\"shiro-activeSessionCache\").shiroCache.get({}) is {}", jsessionId, shiroCache);
            }
        } catch (Exception ex) {
            LOGGER.error(">>>>>> ShiroSession.getShiroSession() is Exception :", ex);
        }
        return session;
    }

}
