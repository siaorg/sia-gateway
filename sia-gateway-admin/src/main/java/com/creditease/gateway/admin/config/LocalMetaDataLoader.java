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


package com.creditease.gateway.admin.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.creditease.gateway.admin.repository.CompDbRepository;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;

/**
 * 读取Admin本地缓存用
 * 
 * @author peihua
 * 
 * 
 **/

@Component
public class LocalMetaDataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMetaDataLoader.class);
    private static final String SETTING = "component.json";

    @Autowired
    private CompDbRepository compdbrepository;

    public static Set<String> publicfilterSet = null;

    @Override
    public void run(String... args) {

        LOGGER.info("LocalMetaDataLoaderTask 启动!");

        try {
            String jsonString = getLocalSetting(SETTING);

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> mapping = JsonHelper.toObject(jsonString, Map.class);

            compdbrepository.udpateCompDesc(mapping);

            publicfilterSet =  mapping.keySet();

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);
        }
    }

    private String getLocalSetting(String jsonPattenName) {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonPattenName);
            if (in == null) {
                return null;
            }
            try {
                byte[] buffer = new byte[1024];
                while (in.read(buffer) != -1) {
                    result.write(buffer);
                }
            }
            catch (IOException e) {
                LOGGER.error("getLocalSetting fail,  file name:{}", jsonPattenName);
                new GatewayException(ExceptionType.AdminException, e);
            }
        }
        catch (Exception e) {

            LOGGER.error("getLocalSetting fail, file name:{}", jsonPattenName);
        }
        return result.toString();
    }
}
