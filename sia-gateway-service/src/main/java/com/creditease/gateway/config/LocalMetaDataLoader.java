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


package com.creditease.gateway.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.scheduler.TopoLogyScheduledTask;

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

    private static final String TOPOJSON = "topo.json";

    @Autowired
    TopoLogyScheduledTask task;

    @Override
    public void run(String... args) {

        LOGGER.info("LocalMetaDataLoaderTask 启动!");

        try {
            String jsonString = getLocalTopo(TOPOJSON);

            Map topo = JsonHelper.toObject(jsonString, Map.class);

            task.statisticTopoTask();

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
        }
    }

    private String getLocalTopo(String jsonPattenName) {

        ByteArrayOutputStream result = null;
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonPattenName);
            if (in == null) {
                return null;
            }

            result = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[1024];
                while (in.read(buffer) != -1) {
                    result.write(buffer);
                }
            }
            catch (IOException e) {
                LOGGER.error("getLocaltSetting fail,  file name:{}", jsonPattenName);
                new GatewayException(ExceptionType.ServcieException, e);

                return null;
            }
        }
        catch (Exception e) {

            LOGGER.error("getLocaltSetting fail, file name:{}", jsonPattenName);
        }

        return result.toString();
    }
}
