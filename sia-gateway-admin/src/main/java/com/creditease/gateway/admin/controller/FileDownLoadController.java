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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creditease.gateway.admin.controller.base.BaseAdminController;
import com.creditease.gateway.admin.service.FileDownloadService;
import com.creditease.gateway.constant.SagProtocol;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.IoHelper;
import com.creditease.gateway.message.Message;
import com.creditease.gateway.message.Message.ResponseCode;

/**
 * 
 * 文件下载服务
 * 
 * @author peihua
 * 
 **/

@Controller
public class FileDownLoadController extends BaseAdminController {

    public static final Logger LOGGER = LoggerFactory.getLogger(FileDownLoadController.class);

    @Autowired
    private FileDownloadService fdls;

    /**
     * 文件列表获取
     * 
     */
    @RequestMapping(value = "/" + SagProtocol.DOWNLOADFILELIST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getDownLoadFileList(@RequestBody Message msg) {

        try {
            Map<String, String> req = msg.getRequest();
            String groupName = req.get("groupName");

            List<String> rst = fdls.getUploadFileList(groupName);
            Message resp = new Message(rst);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resp);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error("getDownLoadFileList Exception is:{}", e.getLocalizedMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }

    /**
     * 
     * 文件下载
     * 
     */
    @RequestMapping(value = "/" + SagProtocol.FILEDOWNLOAD, method = { RequestMethod.GET })
    public ResponseEntity<byte[]> fileDownload(@RequestParam("fileName") String fileName,
            @RequestParam("groupName") String groupName) {

        // 下载文件路径
        String fileUrl = fdls.getDownloadFilePath(groupName, fileName);

        HttpHeaders headers = new HttpHeaders();
        // 通知浏览器以attachment（下载方式）打开
        headers.setContentDispositionFormData("attachment", fileName);
        // application/octet-stream ： 二进制流数据（最常见的文件下载）。
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        byte[] body = null;
        try {
            body = IoHelper.readFile(fileUrl);

        }
        catch (IOException e) {
            LOGGER.error("", e);
            body = new byte[0];
        }
        return new ResponseEntity<byte[]>(body, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/" + SagProtocol.FILEREMOVE, method = { RequestMethod.GET })
    public String fileRemove(@RequestParam("jarName") String jarName, @RequestParam("groupName") String groupName) {

        try {

            boolean rst = fdls.removeFile(groupName, jarName);

            Message resp = new Message(rst);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resp);

        }
        catch (Exception e) {
            new GatewayException(ExceptionType.AdminException, e);

            LOGGER.error(">>> Exception is:" + e.getLocalizedMessage());
            return returnErrorMsg(e.getLocalizedMessage(), ResponseCode.SERVER_ERROR_CODE);
        }
    }
}
