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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpServletResponseWrapper
 * 
 * @author peihua
 * 
 */

public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer = null;

    private ServletOutputStream out = null;

    private PrintWriter writer = null;

    public HttpServletResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);

        buffer = new ByteArrayOutputStream();
        out = new WapperedOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, "UTF-8"));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {

        return out;
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {

        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {

        buffer.reset();
    }

    public String getResponseData(String charset) throws IOException {

        flushBuffer();
        byte[] bytes = buffer.toByteArray();
        try {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private class WapperedOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {

            bos.write(b);
        }

        @Override
        public boolean isReady() {

            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {

        }
    }
}
