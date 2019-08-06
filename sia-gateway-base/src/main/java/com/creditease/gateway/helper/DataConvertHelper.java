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


package com.creditease.gateway.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/***
 * 
 * @author admin
 *
 */

public class DataConvertHelper {

    private DataConvertHelper() {
    }

    public static final String exception2String(Throwable e) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    public static final InputStream byte2InputStream(byte[] buf) {

        return new ByteArrayInputStream(buf);
    }

    public static final byte[] inputStream2byte(InputStream inStream) {

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            return in2b;
        }
        catch (IOException e) {
            // ignore
        }
        return new byte[0];
    }

    /**
     * sate convert to boolean
     * 
     * @param s
     * @param defaultVal
     * @return
     */
    public static boolean toBoolean(Object s, boolean defaultVal) {

        if (null == s) {
            return defaultVal;
        }

        if (s instanceof Boolean) {
            return (Boolean) s;
        }

        try {
            return Boolean.parseBoolean((String) s);
        }
        catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * safe convert to integer
     * 
     * @param str
     * @param defaultVal
     * @return
     */
    public static int toInt(Object str, int defaultVal) {

        if (null == str) {
            return defaultVal;
        }

        if (str instanceof Integer) {
            return (Integer) str;
        }

        try {
            return Integer.parseInt((String) str);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * safe convert to double
     * 
     * @param str
     * @param defaultVal
     * @return
     */
    public static Double toDouble(Object str, double defaultVal) {

        if (null == str) {
            return defaultVal;
        }

        if (str instanceof Double) {
            return (Double) str;
        }

        try {
            return Double.parseDouble((String) str);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * safe convert to long
     * 
     * @param str
     * @param defaultVal
     * @return
     */
    public static long toLong(Object str, long defaultVal) {

        if (null == str) {
            return defaultVal;
        }

        if (str instanceof Long) {
            return (Long) str;
        }

        try {
            return Long.parseLong((String) str);
        }
        catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * String to List<String>
     * 
     * @param str
     * @param seperator
     * @return
     */
    public static List<String> toList(String str, String seperator) {

        if (str==null) {
            return Collections.emptyList();
        }

        String[] strs = str.split(seperator);

        return Arrays.asList(strs);
    }

    /**
     * to URI
     * 
     * @param str
     * @return
     */
    public static URI toURI(String str) {

        if (str==null) {
            return null;
        }

        try {
            URI r = new URI(str);
            return r;
        }
        catch (URISyntaxException e) {
            // ignore
        }

        return null;
    }
}
