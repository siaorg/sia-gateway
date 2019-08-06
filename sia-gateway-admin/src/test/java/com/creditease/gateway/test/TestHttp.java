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


package com.creditease.gateway.test;
import java.io.File;
import java.io.IOException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *@author peihua 
 *
 * */

public class TestHttp {

	/***
	 * ex: "10.143.131.85";//"127.0.0.1";// "10.143.135.136";
	 ***/
    final static String IP = "127.0.0.1";
    final static String FILENAME = "estack.zip";

    public static void main(String[] args) throws Exception {

        testUpload();
       // Thread.sleep(10 * 1000);
        testDownload();
    }

    static void testUpload() {

        String url = "http://" + IP + ":8090/upload";
        String filePath = "C:/Apps/" + FILENAME;

        RestTemplate rest = new RestTemplate();
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        String resp = rest.postForObject(url, param, String.class);
        System.out.println(resp);

    }

    static void testDownload() throws IOException {

        String url = "http://" + IP + ":10829/download/?fileName=" + FILENAME;
        RestTemplate rest = new RestTemplate();

        ResponseEntity<byte[]> resp = rest.getForEntity(url, byte[].class);
        byte[] body = resp.getBody();
        //IOHelper.writeFile("D:/download/" + fileName, body, false);
    }

}
