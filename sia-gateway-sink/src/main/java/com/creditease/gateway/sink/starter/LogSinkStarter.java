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


package com.creditease.gateway.sink.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.creditease.gateway.sink.es.EsIndexMgr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * LogSinkStarter初始化动作
 *
 * @author peihua
 **/
public class LogSinkStarter extends AbstractSinkStarter {

    private static final Logger logger = LoggerFactory.getLogger(LogSinkStarter.class);

    private String fileName;

    @Autowired
    private EsIndexMgr esIndexMgr;

    @Override
    public void preStart() {

        esIndexMgr.setIndexTemplate(getIndexMapping(fileName));
    }

    @Override
    public void postStart() {
    }

    public String getFileName() {

        return fileName;
    }

    public void setFileName(String fileName) {

        this.fileName = fileName;
    }

    private String getIndexMapping(String mappingFile) {

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(mappingFile);
        if (in == null) {
            return null;
        }

        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            in.close();

            return result.toString();
        } catch (IOException e) {
            logger.error("getIndexMapping fail, index mapping file name: " + fileName, e);
        }

        return null;
    }
}
