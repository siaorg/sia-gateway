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

package com.creditease.gateway.http.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * HttpServletRequestWrapper
 * 
 * @author peihua
 * 
 */

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {

    private final byte[] body;

    public HttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public BufferedReader getReader() throws IOException {

        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {

                return true;
            }

            @Override
            public boolean isReady() {

                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {

                return bais.read();
            }
        };
    }

    @Override
    public String getHeader(String name) {

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {

        return super.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {

        return super.getHeaders(name);
    }

    public String getStringBody() {

        return new String(body);
    }

    void binaryReader(HttpServletRequest request) {

        int len = request.getContentLength();

        try {
            ServletInputStream stream = request.getInputStream();

            byte[] buffer = new byte[len];
            stream.read(buffer, 0, len);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
