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


package com.creditease.gateway.admin.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.FileUploadService;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 组件上传功能
 * 
 * @author peihua
 *
 */

@Controller
public class FileUploadController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fups;

    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    @CrossOrigin(methods = { RequestMethod.POST }, origins = "*")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,
            @RequestParam("currentRoleName") String currentRoleName) {

        try {
            LOGGER.info("step1 currentRoleName:[{}]", currentRoleName);

            boolean rst = fups.processUpload(file, currentRoleName);

            if (rst) {
                LOGGER.info("UPLOAD File to Gateway Admin SUCCESS!!");

                Message msg = new Message(rst);
                ObjectMapper mapper = new ObjectMapper();

                return mapper.writeValueAsString(msg);

            }
            else {
                LOGGER.error("UPLOAD File to Gateway Admin Fail..");

                return returnErrorMsg("upload fail", ResponseCode.SERVER_ERROR_CODE);
            }
        }
        catch (Exception e) {

            LOGGER.error("UPLOAD File to Gateway Admin Fail..");
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

}
