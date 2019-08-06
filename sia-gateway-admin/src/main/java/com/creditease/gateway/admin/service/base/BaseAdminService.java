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


package com.creditease.gateway.admin.service.base;

import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.admin.repository.AdminDbRepository;
import com.creditease.gateway.admin.repository.CompDbRepository;
import com.creditease.gateway.admin.repository.RedisRepository;
import com.creditease.gateway.admin.repository.RouteDbRepository;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.message.ZuulHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Service公共接口
 *
 * @author peihua
 */
public class BaseAdminService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseAdminService.class);

    private static final String SEPARATOR = ":";

    @Autowired
    public AuthInterceptor authCheckor;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected AdminDbRepository adminDBRepository;

    @Autowired
    protected RedisRepository redisRepository;

    @Autowired
    protected RouteDbRepository dbRepository;

    @Autowired
    protected DiscoveryService zuulDiscovery;

    @Autowired
    protected ZuulHandler handler;

    @Autowired
    protected CompDbRepository compDBRepository;

    /**
     * 通過request中權限
     */
    public String getZuulGroupName() {

        return authCheckor.getZuulGroupName();
    }

    /**
     * 通過數據庫
     */
    public String getZuulGroupName(String routeid) {

        return authCheckor.getZuulGroupName(routeid);
    }

    /**
     * 取得应用所在路径
     */
    public String getAppPath() {

        String libPath = getJarPath();

        int start = libPath.contains(SEPARATOR) ? libPath.indexOf(SEPARATOR) + 1 : 0;

        int end = libPath.indexOf("bin");
        if (end < 0) {
            // 兼容本地调试
            end = libPath.indexOf("classes");
        }

        if (end < 0) {
            LOGGER.error("bin or class目录不存在故退出，JarPath:{}", libPath);
            return null;
        }

        return libPath.substring(start, end);
    }

    /**
     * 取得第三方插件路径
     */
    public String getPluginPath(String groupName) {

        String pluginPath = getAppPath();

        if (pluginPath == null) {
            return null;
        }

        pluginPath = pluginPath + "thirdparty" + File.separator + groupName;

        LOGGER.info("File Upload end generatePluginPath, libPath:{}", pluginPath);

        return pluginPath;
    }

    public String getJarPath() {

        String jarFilePath = BaseAdminService.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }

        return jarFilePath;
    }
}
