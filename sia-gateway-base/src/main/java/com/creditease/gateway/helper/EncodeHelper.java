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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/***
 * 编码解码相关工具类
 * 
 * @author admin
 *
 */
public class EncodeHelper {

    private EncodeHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * URL编码
     * <p>
     * 若想自己指定字符集,可以使用{@link #urlEncode(String input, String charset)}方法
     * </p>
     *
     * @param input
     *            要编码的字符
     * @return 编码为UTF-8的字符串
     */
    public static String urlEncode(String input) {

        return urlEncode(input, "UTF-8");
    }

    /**
     * URL编码
     * <p>
     * 若系统不支持指定的编码字符集,则直接将input原样返回
     * </p>
     *
     * @param input
     *            要编码的字符
     * @param charset
     *            字符集
     * @return 编码为字符集的字符串
     */
    public static String urlEncode(String input, String charset) {

        try {
            return URLEncoder.encode(input, charset);
        }
        catch (UnsupportedEncodingException e) {
            return input;
        }
    }

    /**
     * URL解码
     * <p>
     * 若想自己指定字符集,可以使用 {@link #urlDecode(String input, String charset)}方法
     * </p>
     *
     * @param input
     *            要解码的字符串
     * @return URL解码后的字符串
     */
    public static String urlDecode(String input) {

        return urlDecode(input, "UTF-8");
    }

    /**
     * URL解码
     * <p>
     * 若系统不支持指定的解码字符集,则直接将input原样返回
     * </p>
     *
     * @param input
     *            要解码的字符串
     * @param charset
     *            字符集
     * @return URL解码为指定字符集的字符串
     */
    public static String urlDecode(String input, String charset) {

        try {
            return URLDecoder.decode(input, charset);
        }
        catch (UnsupportedEncodingException e) {
            return input;
        }
    }

    /**
     * Base64编码
     *
     * @param input
     *            要编码的字符串
     * @return Base64编码后的字符串
     */
    public static byte[] base64Encode(String input) {

        return base64Encode(input.getBytes());
    }

    /**
     * Base64编码
     *
     * @param input
     *            要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static byte[] base64Encode(byte[] input) {

        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * Base64编码
     *
     * @param input
     *            要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static String base64Encode2String(byte[] input) {

        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input
     *            要解码的字符串
     * @return Base64解码后的字符串
     */
    public static byte[] base64Decode(String input) {

        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input
     *            要解码的字符串
     * @return Base64解码后的字符串
     */
    public static byte[] base64Decode(byte[] input) {

        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64URL安全编码
     * <p>
     * 将Base64中的URL非法字符�?,/=转为其他字符, 见RFC3548
     * </p>
     *
     * @param input
     *            要Base64URL安全编码的字符串
     * @return Base64URL安全编码后的字符串
     */
    public static byte[] base64UrlSafeEncode(String input) {

        return Base64.encode(input.getBytes(), Base64.URL_SAFE);
    }

    /**
     * encodeMD5
     * 
     * @param str
     * @return
     */
    public static String encodeMD5(String str) {

        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        }
        catch (Exception e) {
            // ignore
            return str;
        }
    }

}
