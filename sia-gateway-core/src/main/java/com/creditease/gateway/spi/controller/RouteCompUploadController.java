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


package com.creditease.gateway.spi.controller;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.creditease.gateway.filter.dynamic.GatewayClassLoaderFactory;
import com.creditease.gateway.filter.dynamic.GatewayCompile;

/**
 * 
 * @author peihua
 * 
 */

@Controller
public class RouteCompUploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteCompUploadController.class);

    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    @CrossOrigin(methods = { RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {

        GatewayCompile.doneSignal = new CountDownLatch(1);

        if (GatewayCompile.doneSignal != null) {
            LOGGER.info("> step1 doneSignal->{}", GatewayCompile.doneSignal.getCount());
        }

        String libPath = GatewayClassLoaderFactory.instance().getFilterJarPath();

        LOGGER.info(">step2 libPath->{}", libPath);

        if (file.isEmpty()) {

            LOGGER.error("FILE IS EMPTY");
            return "FILE IS EMPTY";
        }

        try {
            File directory = new File(libPath);

            if (!directory.exists()) {
                directory.mkdir();
            }

            String filename = file.getOriginalFilename();
            String uploadFile = libPath + File.separator + filename;
            File oldFile = new File(uploadFile);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            LOGGER.info(">step 2.1 uploadFile->{}", uploadFile);
            file.transferTo(new File(uploadFile));

            LOGGER.info(">uploadFile->{}", uploadFile);
            LOGGER.info(">step 2.2 doneSignal->{}", GatewayCompile.doneSignal.getCount());

            if (GatewayCompile.doneSignal != null) {

                LOGGER.info(">step 3 doneSignal->{}", GatewayCompile.doneSignal.getCount());
                GatewayCompile.doneSignal.await(30, TimeUnit.SECONDS);
                LOGGER.info(">step 4 doneSignal final->{}", GatewayCompile.doneSignal.getCount());
            }

            if (GatewayCompile.doneSignal.getCount() == 0) {
                return "UPLOAD File to Gateway Admin SUCCESS!!";
            }
            else {

                releastLock();
                return "UPLOAD File to Gateway Admin FAIL!!";
            }

        }
        catch (Exception e) {

            LOGGER.error(">Exception:{}", e.getCause());
            LOGGER.error("", e);
        }
        return "UPLOAD File to Gateway Admin Fail..";

    }

    public void releastLock() {

        while (GatewayCompile.doneSignal.getCount() > 0) {
            GatewayCompile.doneSignal.countDown();
        }
    }

}
