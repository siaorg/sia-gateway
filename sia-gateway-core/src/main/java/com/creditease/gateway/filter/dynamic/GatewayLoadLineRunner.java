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


package com.creditease.gateway.filter.dynamic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.helper.JvmToolHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.service.ZuulService;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;

/**
 * 
 * JavaLoadLineRunner : 动态Filter启动类
 * 
 * @author peihua
 *
 */
@Component
class GatewayLoadLineRunner implements CommandLineRunner {

    private final Logger LOGGER = Logger.getLogger(getClass());

    private String libPath = null;

    @Value("${spring.application.name}")
    private String groupName;

    @Value("${zuul.java.packageName}")
    private String packageName;

    @Value("${zuul.filter.interval}")
    private String interval;
    @Value("${zuul.filter.dynamic}")
    private String dynamic;

    @Autowired
    ZuulService zuulservice;

    @Value("${spring.gateway.admin.name}")
    private String adminName;

    @Autowired
    DiscoveryService zuuldisc;

    @Autowired
    protected ZuulHandler handler;

    @Override
    public void run(String... args) throws Exception {

        if (!StringHelper.TRUEFLAG.equals(dynamic)) {
            return;
        }

        /**
         * 第一步: 下载第三方组件到thirdparty目录
         **/
        try {
            List<String> thirdJarList = zuuldisc.getServiceList(adminName);

            if (null == thirdJarList || thirdJarList.size() < 0) {
                LOGGER.warn(">thirdJarList is empty in Admin!!");
            }
            for (String path : thirdJarList) {

                try {
                    String url = "http://" + path + SagProtocol.DOWNLOADFILELIST;

                    Map<String, String> parmeter = new HashMap<String, String>(8);

                    parmeter.put("groupName", groupName);

                    Message msg = new Message(parmeter);

                    String result = handler.executeHttpCmd(url, msg);

                    Message response = JsonHelper.toObject(result, Message.class);

                    if (null == response) {
                        LOGGER.warn("result is null!");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    List<String> flist = (List<String>) response.getResponse();

                    String dstPath = GatewayClassLoaderFactory.instance().getFilterJarPath();

                    if (flist == null) {
                        LOGGER.warn("flist is null!");
                    }
                    else {
                        LOGGER.info(">flist result:" + flist.toArray());

                        for (String filename : flist) {
                            String getUrl = "http://" + path + SagProtocol.FILEDOWNLOAD + "?fileName=" + filename + "&"
                                    + "groupName=" + groupName;

                            handler.executeDownloadFile(filename, getUrl, dstPath);
                        }
                    }
                    LOGGER.info("> flist result:" + flist.toArray());
                    break;
                }
                catch (Exception e) {
                    LOGGER.error("> download failed:{}", e);
                    continue;
                }

            }
        }
        catch (Exception e) {

            new GatewayException(ExceptionType.CoreException, e);
            LOGGER.error(">动态初始化加载Filter失败  error :{}", e);
        }

        /**
         * 第二步: 从thirdparty目录加载第三方插件
         */
        libPath = GatewayClassLoaderFactory.instance().getFilterJarPath();

        if (StringHelper.isEmpty(libPath)) {
            LOGGER.error(">LibPath is null,动态加载JAR包路径:" + libPath);

            if (JvmToolHelper.isWindows()) {
                libPath = "C://thirdparty";

            }
            else {

                LOGGER.error("> LibPath is null:" + libPath);
                return;
            }
        }
        /**
         * 第三步： 加载FilterManger
         */
        filterMangerInit();

    }

    public void filterMangerInit() {

        LOGGER.info("> 动态Filter已开启,动态加载JAR包路径:" + libPath + ",包全路径名：" + packageName);

        LOGGER.info("> GatewayLoadLineRunner ClassLoader location:" + this.getClass().getClassLoader().toString());

        File file = null;

        try {

            FilterLoader.getInstance().setCompiler(new GatewayCompile(libPath, packageName, zuulservice));

        }
        catch (Exception e) {

            e.printStackTrace();

            new GatewayException(ExceptionType.CoreException, e);

            LOGGER.error(">动态初始化加载Filter失败  error :" + e.getStackTrace());

        }
        finally {

            try {
                FilterFileManager.setFilenameFilter(new JarFileFilter());

                file = new File(libPath);

                if (file.isDirectory() && file.exists()) {

                    LOGGER.info(">FilterFileManager init start >");

                    FilterFileManager.init(Integer.parseInt(interval), libPath);

                }
                else {
                    LOGGER.error(">动态初始化加载Filter失败,加载Filter路径：" + libPath);
                }
            }
            catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}
