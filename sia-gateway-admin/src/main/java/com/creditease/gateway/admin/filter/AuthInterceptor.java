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


package com.creditease.gateway.admin.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.creditease.gateway.admin.domain.MutiZuul;
import com.creditease.gateway.admin.service.AdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.ZuulInfo;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;

/**
 * 登陆用户信息处理类
 * @date 2018/9/19 15:58
 */
@Service
public class AuthInterceptor {

    public static final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AdminService adminserv;

    @Value("${spring.role.isadmin}")
    private String isadmin;

    /**
     *
     * 用戶權限取得
     *
     **/
    public boolean isadmin() {

        return Boolean.parseBoolean(isadmin);
    }

    /**
     * 获取当前用户的角色信息 对admin角色进行null处理（是为了数据检索式字段匹配，管理员默认null 返回全部）
     *
     * @return
     */
    public List<String> getCurrentUserRoles() {

        List<String> roleNames;
        HttpSession session = request.getSession();
        roleNames = (List<String>) session.getAttribute("roleNames");

        return roleNames;
    }

    /**
     * 获取当前用户的角色信息
     *
     * @return
     */
    public List<String> getCurrentUserAllRoles() {
        List<String> roleNames = null;
        try {

            HttpSession session = request.getSession();
            roleNames = (List<String>) session.getAttribute("roleNames");
            LOGGER.debug(">roleNames{}", JSON.toJSONString(roleNames));
        } catch (Exception e) {
            LOGGER.error("获取角色信息失败！",e);
        }
        return roleNames;

    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public String getCurrentUser() {

        String currentUser;
        HttpSession session = request.getSession();
        currentUser = (String) session.getAttribute("currentUser");
        return currentUser;
    }

    /**
     * 获取当前Role
     *
     * @return
     */
    public String getCurrentRoleName() {

        String currentRoleName = null;
        try {
            HttpSession session = request.getSession();

            currentRoleName = (String) session.getAttribute("currentRoleName");

            LOGGER.debug(">getCurrentRoleName--->currentRoleName:[{}]", currentRoleName);

            List<String> roleNames = (List<String>) session.getAttribute("roleNames");

            currentRoleName = StringHelper.isEmpty(currentRoleName) ? roleNames.get(0) : currentRoleName;

            if (StringHelper.isEmpty(currentRoleName)) {
                LOGGER.error(">getCurrentRoleName emptry:" + currentRoleName);
            }
        }
        catch (Exception e) {
            LOGGER.error(">getCurrentRoleName currentRoleName is empty", 500, "> currentRoleName is empty", e);
        }
        return currentRoleName;
    }

    public boolean isAdminUser(List<String> authusers) {

        if (isadmin()) {
            LOGGER.debug("> Admin管理端跳过权限判断:" + isadmin());

            return true;
        }
        else {
            boolean flag = false;

            if (authusers != null) {
                flag = authusers.contains(GatewayConstant.ROLE_ADMIN);
            }

            return flag;
        }
    }

    /**
     * 根据SessionID取得用户权限--》网关集群GroupName
     */
    public String getZuulGroupName() {

        List<String> roleNames = getCurrentUserAllRoles();

        if (null == roleNames) {
            LOGGER.error(">getZuulGroupName is null");
            return null;
        }

        for (String rolename : roleNames) {
            LOGGER.debug(">get All roleName:" + rolename);
        }

        boolean isAdmin = isAdminUser(roleNames);

        LOGGER.debug("> HttpSession is isAdmin:" + isAdmin);

        String roleName = getCurrentRoleName();

        LOGGER.debug("> getZuulGroupName roleName is select:" + roleName);

        return roleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX;

    }

    /**
     * 根据SessionID取得该用户所有网关组权限
     */
    public Map<String, MutiZuul> getMutiGroupName() {

        try {
            List<String> roleNames = getCurrentUserAllRoles();
            LOGGER.debug(">roleNames{}" , roleNames);
            List<ZuulInfo> groupList = adminserv.getAllZuulList();

            LOGGER.debug(">groupList{}",JSON.toJSONString(groupList));
            boolean isAdmin = isAdminUser(roleNames);

            if (isAdmin) {

                String roleName = GatewayConstant.ROLE_ADMIN;

                LOGGER.debug(">  roleName is admin:" + roleName);

                return getZuulMap(groupList);

            }
            else {

                Map<String, MutiZuul> temp = new HashMap<String, MutiZuul>(32);

                for (String roleName : roleNames) {

                    LOGGER.debug(">  roleName is select:" + roleName);

                    String key = roleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX;

                    temp.put(key, new MutiZuul());

                }

                Set<String> keysets = temp.keySet();

                List<ZuulInfo> filterZuulList = new ArrayList<ZuulInfo>();

                for (ZuulInfo zuul : groupList) {
                    String groupname = zuul.getZuulGroupName();

                    if (keysets.contains(groupname)) {
                        filterZuulList.add(zuul);
                    }

                }
                return getZuulMap(filterZuulList);

            }
        } catch (Exception e) {
            LOGGER.error("获取网关组信息失败！",e);
        }
        return null;
    }

    /**
     * 構造返回
     *
     */
    public Map<String, MutiZuul> getZuulMap(List<ZuulInfo> groupList) {

        Map<String, MutiZuul> zuulMapper = new HashMap<String, MutiZuul>(32);

        for (ZuulInfo zuul : groupList) {

            String zuulGroupName = zuul.getZuulGroupName();

            if (zuulMapper.containsKey(zuulGroupName)) {
                MutiZuul groupinfo = zuulMapper.get(zuulGroupName);

                groupinfo.setInstanceNo(groupinfo.getInstanceNo() + 1);

                if (!("RUNNING").equals(groupinfo)) {
                    groupinfo.setZuulStatus(zuul.getZuulStatus());
                }
            }
            else {
                MutiZuul groupinfo = new MutiZuul();

                groupinfo.setZuulGroupName(zuulGroupName);
                groupinfo.setInstanceNo(1);
                groupinfo.setZuulDesc(zuul.getZuulDesc());
                groupinfo.setZuulStatus(zuul.getZuulStatus());

                zuulMapper.put(zuulGroupName, groupinfo);
            }
        }

        return zuulMapper;
    }

    /**
     *
     * 设置当前网关组的角色
     *
     **/
    public String setCurrentRole(String currentRoleName) {

        HttpSession session = request.getSession();

        session.setAttribute("currentRoleName", currentRoleName);

        return currentRoleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX;
    }

    public String getZuulGroupName(String routeid) {

        return adminserv.getZuulGroupName(routeid);

    }

    public Message loginSystem(String username, String roleName) {

        try {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(roleName)) {
                return Message.buildInValidParamResult();
            }
            /**
             * add currentUser to session
             */
            List<String> roleNames = Lists.newArrayList(roleName);
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("currentUser", username);
            httpSession.setAttribute("roleNames", roleNames);
            List<String>  roleNames1 = (List<String>) httpSession.getAttribute("roleNames");
            LOGGER.debug("> roleNames1{}",roleNames1);
            return Message.buildSuccessResult(setCurrentRole(roleName));
        }
        catch (Exception e) {
            LOGGER.error(" > login fail...", e);
            return Message.buildExceptionResult();
        }
    }

    public String logOutSystem() {

        try {
            HttpSession httpSession = request.getSession();
            httpSession.removeAttribute("currentUser");
            httpSession.removeAttribute("currentRoleName");
            return "redirect:/";
        }
        catch (Exception e) {
            LOGGER.error("> admin system logout fail...", e);
            return String.valueOf(Message.ResponseCode.SERVER_ERROR_CODE.getCode());
        }
    }
}
