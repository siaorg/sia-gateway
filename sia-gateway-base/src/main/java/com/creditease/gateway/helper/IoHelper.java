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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;
/***
 * 
 * @author admin
 *
 */
public class IoHelper {

    private IoHelper() {
    }

    /**
     * getPathSplitor
     * 
     * @param os
     * @return
     */
    public static String getPathSplitor(String os) {

        if (("Windows").equalsIgnoreCase(os)) {
            return "\\";
        }
        else {
            return "/";
        }
    }

    /**
     * isFile
     * 
     * @param path
     * @return
     */
    public static boolean isFile(String path) {

        File file = new File(path);

        if (file.exists() && file.isFile()) {
            return true;
        }

        return false;
    }

    /**
     * isDirectory
     * 
     * @param path
     * @return
     */
    public static boolean isDirectory(String path) {

        File file = new File(path);

        if (file.exists() && file.isDirectory()) {
            return true;
        }

        return false;
    }

    /**
     * writeTxtFile
     * 
     * @param destination
     * @param data
     * @param encoding
     * @throws IOException
     */
    public static void writeTxtFile(String destination, String data, String encoding, boolean append)
            throws IOException {

        writeFile(destination, data.getBytes(encoding), append);

    }

    public static void write(String line, String filename) {

        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(line);
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writeFile
     * 
     * @param destination
     * @param data
     * @throws IOException
     */
    public static void writeFile(String destination, byte[] data, boolean append) throws IOException {

        File temp = new File(destination);

        if (temp.exists() && append == true) {
            FileOutputStream fos = new FileOutputStream(temp, true);
            try {
                fos.write(data);
                fos.flush();
            }
            finally {
                fos.close();
            }
        }
        else {

            FileOutputStream fos = new FileOutputStream(temp);
            try {
                fos.write(data);
                fos.flush();
            }
            finally {
                fos.close();
            }
        }
    }

    /**
     * readFile
     * 
     * @param destination
     * @return
     * @throws IOException
     */
    public static byte[] readFile(String destination) throws IOException {

        File temp = new File(destination);

        if (temp.exists()) {
            FileInputStream fis = new FileInputStream(temp);
            byte[] data = new byte[(int) temp.length()];
            try {
                fis.read(data);
            }
            finally {
                fis.close();
            }
            return data;
        }

        return new byte[0];
    }

    /**
     * 
     * @param path
     * @param encoding
     * @return
     */
    public static String readTxtFile(String path, String encoding) {

        try {
            if (path == null || "".equals(path)) {
                return null;
            }

            FileInputStream fstream = new FileInputStream(path);
            StringBuilder source = new StringBuilder();

            getStringFromStream(encoding, fstream, source);

            return source.toString();

        }
        catch (Exception ee) {
            // ignore
        }

        return null;
    }

    /**
     * @param encoding
     * @param fstream
     * @param source
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static void getStringFromStream(String encoding, FileInputStream fstream, StringBuilder source)
            throws IOException, UnsupportedEncodingException {

        try {

            byte[] buffer = new byte[4096];
            int readct = -1;
            while ((readct = fstream.read(buffer)) > 0) {
                if (encoding != null) {
                    source.append(new String(buffer, 0, readct, encoding));
                }
                else {
                    source.append(new String(buffer, 0, readct));
                }
            }
        }
        finally {
            fstream.close();
        }
    }

    /**
     * existFile
     * 
     * @param path
     * @return
     */
    public static boolean exists(String path) {

        try {
            File temp = new File(path);
            return temp.exists();
        }
        catch (Exception e) {
            // ignore
        }
        return false;
    }

    /**
     * getCurrentPath
     * 
     * @return
     */
    public static String getCurrentPath() {

        File temp = new File("");

        return temp.getAbsolutePath();
    }

    public static String getFormatCurrentPath() {

        File temp = new File("");

        return temp.getAbsolutePath().replaceAll("\\\\", "/");
    }

    /**
     * getExt
     * 
     * @param path
     * @return
     */
    public static String getExt(String path) {

        String[] info = path.split("\\.");

        return info[info.length - 1];
    }

    public static String[] getFileNamesStr(String path) {

        List<File> al = getFiles(path);
        String[] fl = null;
        if (al != null && !al.isEmpty()) {
            fl = new String[al.size()];
            for (int i = 0; i < al.size(); i++) {
                fl[i] = al.get(i).getName();
            }
        }

        return fl;
    }

    public static String[] getFilesStr(String path) {

        List<File> al = getFiles(path);
        String[] fl = null;
        if (al != null && !al.isEmpty()) {
            fl = new String[al.size()];
            for (int i = 0; i < al.size(); i++) {
                fl[i] = al.get(i).getAbsolutePath();
            }
        }

        return fl;
    }

    /**
     * getFiles
     * 
     * @param path
     * @return
     */
    public static List<File> getFiles(String path) {

        File dir = new File(path);

        if (dir.exists()) {

            File[] files = dir.listFiles();

            if (files != null && files.length > 0) {
                return initDirs(files, false);
            }
        }

        return Collections.emptyList();
    }

    public static List<File> initDirs(File[] files, boolean needDirs) {

        ArrayList<File> dirs = new ArrayList<File>();

        for (int i = 0; i < files.length; i++) {
            if (needDirs == false && files[i].isFile()) {
                dirs.add(files[i]);
            }
            else if (needDirs == true && files[i].isDirectory()) {
                dirs.add(files[i]);
            }
        }

        return dirs;
    }

    /**
     * getDirs
     * 
     * @param path
     * @return
     */
    public static List<File> getDirs(String path) {

        File dir = new File(path);

        if (dir.exists()) {

            File[] files = dir.listFiles();

            if (files != null && files.length > 0) {
                return initDirs(files, true);
            }
        }

        return Collections.emptyList();
    }

    /**
     * copyFolder
     * 
     * @param src
     * @param tar
     * @return
     * @throws IOException
     */
    public static void copyFolder(String src, String tar) throws IOException {

        File srcFile = new File(src);

        if (srcFile.isDirectory()) {

            // create target folder
            File tarFile = new File(tar);

            if (!tarFile.exists()) {

                tarFile.mkdir();

            }

            // copy file & folder
            String[] srcfiles = srcFile.list();

            if (srcfiles != null && srcfiles.length > 0) {

                dealSrcFiles(srcfiles, srcFile, tarFile);

            }

        }

    }

    public static void dealSrcFiles(String[] srcfiles, File srcFile, File tarFile) throws IOException {

        for (int i = 0; i < srcfiles.length; i++) {

            File temp = new File(srcFile.getAbsolutePath() + "/" + srcfiles[i]);

            if (!temp.isDirectory()) {
                copyFile(srcFile.getAbsolutePath() + "/" + srcfiles[i], tarFile.getAbsolutePath() + "/" + srcfiles[i]);
            }
            else {
                copyFolder(srcFile.getAbsolutePath() + "/" + srcfiles[i],
                        tarFile.getAbsolutePath() + "/" + srcfiles[i]);
            }

        }
    }

    /**
     * moveFolder
     * 
     * @param src
     * @param tar
     * @return
     */
    public static int moveFolder(String src, String tar) {

        int res = 0;

        try {
            copyFolder(src, tar);
            res = 1;
        }
        catch (IOException e) {
            res = -1;
        }

        res = deleteFolder(src);

        return res;
    }

    /**
     * copyFile
     * 
     * @param src
     * @param tar
     * @return
     */
    public static int copyFile(String src, String tar) {

        int res = 0;

        File srcFile = new File(src);

        if (srcFile.exists()) {

            try {
                byte[] data = readFile(src);

                writeFile(tar, data, false);

                res = 1;
            }
            catch (Exception ee) {
                // ignore
            }
        }

        return res;
    }

    /**
     * moveFile
     * 
     * @param src
     * @param tar
     * @return
     */
    public static int moveFile(String src, String tar) {

        int res = 0;

        if (copyFile(src, tar) > 0) {
            res = deleteFile(src);
        }

        return res;
    }

    /**
     * createFolder
     * 
     * @param path
     * @return
     */
    public static int createFolder(String path) {

        int res = 0;
        File dir = new File(path);

        if (!dir.exists()) {

            File dirParent = new File(dir.getParent());

            if (!dirParent.exists()) {
                createFolder(dir.getParent());
            }

            if (dir.mkdir())
            {
            	res = 1;
            }

        }

        return res;
    }

    /**
     * deleteFile
     * 
     * @param path
     * @return
     */
    public static int deleteFile(String path) {

        int res = 0;

        File temp = new File(path);

        if (temp.exists() && temp.delete()) {
            res = 1;
        }

        return res;
    }

    /**
     * deleteFolder
     * 
     * @param path
     * @return
     */
    public static int deleteFolder(String path) {

        int res = 1;
        File dir = new File(path);

        String[] files = dir.list();

        if (files != null && files.length > 0) {

            for (int i = 0; i < files.length; i++) {

                File temp = new File(path + "/" + files[i]);

                if (temp.isDirectory()) {
                    res += deleteFolder(temp.getAbsolutePath());
                }

                try {
                    temp.delete();
                }
                catch (Exception ee) {
                    return -1;
                }
            }

        }

        dir.delete();

        return res;
    }

    /**
     * 读取取文件，获取字节
     * 
     * @param fileUrl
     * @return
     */
    public static byte[] getBytes(String fileUrl) {

        File file = new File(fileUrl);
        if (!file.exists()) {
            return null;
        }

        FileInputStream io = null;
        byte[] data = null;
        try {
            io = new FileInputStream(file);
            data = new byte[io.available()];
            io.read(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (null != io) {
                try {
                    io.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }

            }
        }
        return data;
    }
    
    public static String readJsonFile(Resource resource) throws IOException
    {
    	BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
    	StringBuffer message=new StringBuffer();
    	String line = null; 
    	while((line = br.readLine()) != null) { 
    	message.append(line); 
    	}
    	String defaultString=message.toString();
    	String result=defaultString.replace("\r\n", "").replaceAll(" +", "");
    	System.out.println(result);
    	
		return result;
    }

    /**
     * 下载文件
     * 
     * @param urlString
     * @return
     * @throws Exception
     */
    public static String downloadFile(String urlString) throws Exception {

        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        con.setConnectTimeout(30 * 1000);
        con.setRequestProperty("Charset", "UTF-8");

        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder fileContent = new StringBuilder();
        // 开始读取
        String line;
        while ((line = br.readLine()) != null) {
            fileContent.append(new String(line.getBytes(), "UTF-8"));
            fileContent.append(System.getProperty("line.separator"));
        }
        // 完毕，关闭所有链接
        is.close();
        br.close();

        return fileContent.toString();
    }

    /**
     * copy from inputStream to outputStream
     * 
     * @param input
     * @param output
     * @return long sum
     * @throws IOException
     */
    public static long copyStream(InputStream input, OutputStream output) throws IOException {
    	// Adjust if you want
        byte[] buffer = new byte[1024]; 
        int bytesRead = 0;
        long count = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
            count += bytesRead;
        }
        return count;
    }
}
