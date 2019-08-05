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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.common.system.shiro.ShiroSession;
import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.helper.StringHelper;

/**
 * 网关权限认证
 * 
 * @author peihua
 *
 */

@Component
public class CorsFilter implements Filter {

    private final static Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);

    public static final String OPTIONMETHOD = "OPTIONS";
    public static final String URLHEALTH = "/health";
    public static final String URLOAUTH = "/oauth";
    public static final String LOGINURL = "/loginSystem";

    public enum STATE {

        /**
         * FISRTCHECK:第一次状态检查
         *
         * SECONDCHECK:第二次状态检查
         *
         */
        FISRTCHECK("first"),

        SECONDCHECK("second");

        private String status;

        private STATE(String status) {

            this.status = status;
        }

        @Override
        public String toString() {

            return status;
        }
    }

    @Value("${redirectUrl}")
    private String redirectUrl;

    @Value("${OPEN_LOGIN_FLAG}")
    private boolean openLogFlag;

    @Autowired
    public AuthInterceptor authCheckor;

    @Autowired
    private ShiroSession shiroSession;

    private STATE sts = STATE.FISRTCHECK;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS,DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("XDomainRequestAllowed", "1");

        String method = httpServletRequest.getMethod();
        String url = httpServletRequest.getRequestURI();
        String customRole = httpServletRequest.getHeader("customRole");

        boolean testFlag = false;

        if (!StringHelper.isEmpty(customRole)) {
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("roleNames", Arrays.asList(customRole.split(",")));
            testFlag = true;
        }

        LOGGER.debug(">>>>>getRequestURI method::::" + method + ">>>>>getRequestURI :" + url);

        /**
         * 前端做Option探测，大坑。。。。。。
         **/
        if (OPTIONMETHOD.equals(method) || URLHEALTH.equals(url) || url.contains(URLOAUTH) || testFlag
                || url.contains(SagProtocol.DOWNLOADFILELIST) || url.contains(SagProtocol.FILEREMOVE)
                || url.contains(SagProtocol.FILEDOWNLOAD)) {
            chain.doFilter(httpServletRequest, res);

        }
        else if (openLogFlag) {
            /**
             * OPEN_LOGIN_FLAG--> true:指当前网关admin系统走自己的登录逻辑
             */
            if (LOGINURL.equals(url)) {
                chain.doFilter(httpServletRequest, res);
            }
            else {
                HttpSession session = httpServletRequest.getSession();
                String userName = (String) session.getAttribute("currentUser");
                if (StringUtils.isEmpty(userName)) {
                    ((HttpServletResponse) res).sendRedirect(redirectUrl);
                }
                chain.doFilter(httpServletRequest, res);
            }
        }
        else {
            HttpSession session = httpServletRequest.getSession();
            String jsessionid = (String) session.getAttribute("jsessionid");
            String jsessionid4parm = httpServletRequest.getParameter("jsessionid");

            if (!StringHelper.isEmpty(jsessionid4parm)) {
                jsessionid = jsessionid4parm;
            }
            if (StringHelper.isEmpty(jsessionid)) {
                jsessionid = httpServletRequest.getParameter("jsessionid");
                if (StringHelper.isEmpty(jsessionid)) {
                    LOGGER.info(">>>>>jsessionid :" + jsessionid);
                }
            }
            if (!checkLogin(jsessionid, httpServletRequest, response)) {

                LOGGER.info(">>>>> checkLogin failed for jeesion ID::" + jsessionid);
                /**
                 * 双重检查
                 **/
                String jsessionid4parme = httpServletRequest.getParameter("jsessionid");

                LOGGER.info(">>>>> doublecheck for jsessionid4parme::" + jsessionid4parme);
                sts = STATE.SECONDCHECK;

                if (!StringHelper.isEmpty(jsessionid4parme)) {
                    LOGGER.info(">>>>> doublecheck  2 for jsessionid4parme::" + jsessionid4parme);

                    if (!checkLogin(jsessionid4parme, httpServletRequest, response)) {
                        cleanSession(session, res);
                    }
                }
                else {
                    cleanSession(session, res);
                }
            }
            else {
                chain.doFilter(httpServletRequest, res);
            }
        }
    }

    public void cleanSession(HttpSession session, ServletResponse res) {

        LOGGER.error(">>>>> cleanSession:" + session);

        session.removeAttribute("jsessionid");
        session.removeAttribute("currentUser");
        session.removeAttribute("roleNames");
        try {
            ((HttpServletResponse) res).sendRedirect(redirectUrl);
        }
        catch (IOException e) {

            LOGGER.error(">>>>> cleanSession error:" + e.getStackTrace());
        }
    }

    boolean checkLogin(String jsessionid, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("jsessionid", jsessionid);
        String userName = shiroSession.getUserName(jsessionid);
        List<String> roleNames = shiroSession.getRoleNames(jsessionid);

        LOGGER.info(">>>>> userName::::::" + userName);

        session.setAttribute("currentUser", userName);
        session.setAttribute("roleNames", roleNames);

        boolean isadmin = authCheckor.isadmin();

        if (isadmin) {
            LOGGER.info(">>>>> 超级用户权限开启，用户为Admin :" + isadmin);

            session.setAttribute("currentUser", "admin");
            roleNames.add("admin");
            session.setAttribute("roleNames", roleNames);
            return true;

        }
        else {
            if (sts.toString().equals(STATE.FISRTCHECK)) {
                session.removeAttribute("jsessionid");
                session.removeAttribute("currentUser");
                session.removeAttribute("roleNames");
            }

            if (StringHelper.isEmpty(userName) && roleNames.size() == 0) {
                return false;
            }
        }

        return true;
    }

    private static String getPostData(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        try {
            Map<String, String[]> map = request.getParameterMap();

            sb = new StringBuilder();

            if (map != null) {

                sb.append("request parameters:\t");
                for (Map.Entry<String, String[]> entry : map.entrySet()) {
                    sb.append("[" + entry.getKey() + "=" + printArray(entry.getValue()) + "]");
                }
            }

            InputStream in = request.getInputStream();
            String reqBbody = StreamUtils.copyToString(in, Charset.forName("UTF-8"));

            sb.append("requestBody:" + reqBbody + "\n");

            LOGGER.info(">>>>> request body :" + sb.toString());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    private static String printArray(String[] arr) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
