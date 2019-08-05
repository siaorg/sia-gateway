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


package com.creditease.gateway.message;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.creditease.gateway.helper.IoHelper;

/**
 * 
 * 隔离HTTP的具体实现
 * 
 * @author peihua
 * 
 **/

@Component
public class ZuulHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZuulHandler.class);

    public String executeHttpCmd(String url, String type, Message msg) throws Exception {

        String result = null;

        url += "/sagOpt" + type;

        LOGGER.info("http request url is:" + url + " request path is: " + type);

        try {
            result = restTemplate.postForObject(url, msg, String.class);

        }
        catch (HttpServerErrorException e) {

            LOGGER.error(e.getMessage());
            throw e;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
            throw e;
        }

        return result;
    }

    public String executeHttpGetCmd(String url, String type) throws Exception {

        String result = null;

        url += "/sagOpt" + type;

        LOGGER.info("http request url is:" + url + " request path is: " + type);

        try {
            result = restTemplate.getForObject(url, String.class);

        }
        catch (HttpServerErrorException e) {

            LOGGER.error(e.getMessage());
            throw e;
        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
            throw e;
        }

        return result;
    }

    public String executeHttpCmd(String url, Message msg) throws Exception {

        String result = null;

        LOGGER.info("http request url is:" + url);

        try {
            result = restTemplate.postForObject(url, msg, String.class);

        }
        catch (HttpServerErrorException e) {

            LOGGER.error(e.getMessage());
            throw e;

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
            throw e;
        }

        LOGGER.info("http request result:" + result);

        return result;
    }

    public ResponseEntity<String> executeHttpTestCmd(String url, String type, Message msg) {

        ResponseEntity<String> result = null;

        LOGGER.info("http request url is:" + url + " request path is: " + type);

        String error = null;

        HttpStatus errorCode = null;

        try {
            result = restTemplate.getForEntity(url, String.class);

            return result;

        }
        catch (HttpServerErrorException e) {

            error = e.getResponseBodyAsString();
            errorCode = e.getStatusCode();

            LOGGER.error(e.getMessage());
            result = new ResponseEntity<String>(error, errorCode);

            throw e;

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
            result = new ResponseEntity<String>(error, errorCode);

            throw e;
        }

    }

    public String executeUploadCmd(String url, String filePath) {

        String resp = null;
        try {
            FileSystemResource resource = new FileSystemResource(new File(filePath));
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("file", resource);

            resp = restTemplate.postForObject(url, param, String.class);

        }
        catch (RestClientException e) {
            LOGGER.error(">>> RestClientException:" + e.getCause());
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(">>> Exception:" + e.getCause());
            throw e;
        }
        return resp;
    }

    public String executePostCmd(String url, MultiValueMap<String, Object> param) {

        String resp = null;
        try {

            resp = restTemplate.postForObject(url, param, String.class);

        }
        catch (RestClientException e) {
            LOGGER.error("> RestClientException:" + e.getCause());
            throw e;
        }
        catch (Exception e) {
            LOGGER.error("> Exception:" + e.getCause());
            throw e;
        }
        return resp;
    }

    public <T> String executePostEntity(String url, HttpHeaders requestHeaders, T requestBody) {

        // HttpEntity
        HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        // post
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        return responseEntity.getBody();
    }

    public String executeHttpCmd(String url) {

        String result = null;

        try {

            result = restTemplate.getForObject(url, String.class);
            return result;
        }
        catch (Exception e) {

            LOGGER.info(e.getMessage());

            throw e;
        }
    }

    public <T> String executeAsynHttpCmd(String url, T requestBody, HttpHeaders requestHeaders) {

        String result = null;
        try {

            HttpEntity<T> httpEntity = new HttpEntity<>(requestBody, requestHeaders);

            ListenableFuture<ResponseEntity<String>> rs = asyncRestTemplate.postForEntity(url, httpEntity,
                    String.class);

            result = StringUtils.isEmpty(rs) ? null : rs.toString();

        }
        catch (Exception e) {

            LOGGER.info(e.getMessage(), e);
        }

        return result;

    }

    public <T> String executeAsynHttpCmd(String url) {

        String result = null;
        try {

            ListenableFuture<ResponseEntity<String>> rs = asyncRestTemplate.getForEntity(url, String.class);

            result = StringUtils.isEmpty(rs) ? null : rs.toString();

        }
        catch (Exception e) {

            LOGGER.info(e.getMessage(), e);
            throw e;
        }

        return result;

    }

    public String executeDownloadFile(String filename, String getUrl, String dstPath) {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(getUrl, HttpMethod.GET, httpEntity, byte[].class);

        byte[] body = resp.getBody();
        if (body == null || body.length <= 0) {
            return "> executeDownloadFile FILE IS EMPTY";
        }
        try {

            dstPath = dstPath + File.separator + filename;

            LOGGER.info(">executeDownloadFile dstPath:" + dstPath);

            IoHelper.writeFile(dstPath, body, false);
            return "> GET FILE SUCCESS";
        }
        catch (Exception e) {
            LOGGER.error("writeFile fail", e);

            return "> GET FILE FAIL";

        }
    }
}
