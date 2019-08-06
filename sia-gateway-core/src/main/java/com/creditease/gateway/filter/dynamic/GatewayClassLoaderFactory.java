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


package com.creditease.gateway.filter.dynamic;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.helper.StringHelper;

/**
 * @author peihua
 * 
 *         动态加载/卸载 第三方Filter-JAR包
 * 
 */

public class GatewayClassLoaderFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayClassLoaderFactory.class);

    private static final GatewayClassLoaderFactory INSTANCE = new GatewayClassLoaderFactory();

    private Map<String, JarURLConnection> urlMap = new ConcurrentHashMap<>();

    private Map<String, File> filterPathMap = new ConcurrentHashMap<>();

    public Map<String, File> getFilterPathMap() {

        return filterPathMap;
    }

    private static final String THIRDPARTY = "thirdparty";

    public static final GatewayClassLoaderFactory instance() {

        return INSTANCE;
    }

    /**
     * 
     * 动态加载Classloader
     * 
     **/
    public ClassLoader getClassLoader(String libpath, String jarName, File file) throws IOException {

        try {
            LOGGER.info("> getClassLoader 加载路径：{},加载JAR包名稱：{}", libpath, jarName);

            if (StringHelper.isEmpty(libpath) || StringHelper.isEmpty(jarName)) {

                LOGGER.error(">GatewayClassLoaderFactory 加载路径:{}有错误,JAR:{}", libpath, jarName);
                return null;
            }
            Set<String> featureJars = new HashSet<String>();
            featureJars.add(jarName + ".jar");
            JarURLConnection conn = urlMap.get(jarName);

            String filterName = getFilterNameByJarName(jarName);

            if (conn != null) {

                filterPathMap.remove(filterName);
                JarURLConnection urlopen = urlMap.get(filterName);

                urlopen.getJarFile().close();
                urlopen = null;
                urlMap.remove(filterName);
            }

            ClassLoader newClsLoader = createFilterClassLoader(libpath, featureJars);

            LOGGER.info(">newClsLoader location:{}", newClsLoader.toString());
            LOGGER.info(">classpath:{}", Thread.currentThread().getContextClassLoader().getResource("/").getPath());

            Thread.currentThread().setContextClassLoader(newClsLoader);
            filterPathMap.put(filterName, file);
            return newClsLoader;
        }
        catch (Exception e) {
            LOGGER.info(">getClassLoader加载Filter失败原因：{}", e.toString());
            throw e;
        }
    }

    public boolean removeFilterComponent(String filterName) throws IOException {

        LOGGER.error("> filterName:{}", filterName);
        JarURLConnection conn = urlMap.get(filterName);

        if (conn != null) {
            JarURLConnection urlopen = urlMap.get(filterName);
            if (urlopen != null) {
                urlopen.getJarFile().close();
                urlopen = null;

                LOGGER.error("> start remove  urlopen:{}", urlopen);
            }
            else {
                LOGGER.error("> urlopen is null !!!");
            }
            urlMap.remove(filterName);
            return true;
        }
        else {
            LOGGER.error(">conn is null!!!");
        }
        return false;

    }

    private ClassLoader createFilterClassLoader(String libPath, Set<String> featureJars) throws IOException {

        try {
            final URL[] files = loadJars(featureJars, libPath);

            for (URL file : files) {

                LOGGER.info(">createFilterClassLoader getPath:{}", file.getPath());
                addURLFile(file);
            }
            final ClassLoader parent = this.getClass().getClassLoader();

            return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {

                @Override
                public URLClassLoader run() {

                    return new URLClassLoader(files, parent);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(">createFilterClassLoader加载Filter失败原因：{}", e.toString());

            throw e;
        }
    }

    public void addURLFile(URL file) throws IOException {

        String filterPath = file.getFile();

        File f = new File(filterPath);
        String jarName = f.getName();
        String filterName = getFilterNameByJarName(jarName);

        LOGGER.info("> filterName:{}", filterName);
        URLConnection uc = file.openConnection();

        if (uc instanceof JarURLConnection) {

            uc.setUseCaches(true);
            ((JarURLConnection) uc).getManifest();
            urlMap.put(filterName, (JarURLConnection) uc);

        }
    }

    public String getFilterNameByJarName(String jarName) {

        String filterName;

        if (jarName.indexOf(StringHelper.HENXIAN_SEPARATOR) > 0) {
            filterName = (String) jarName.subSequence(0, jarName.indexOf("-"));
        }
        else {
            filterName = jarName;
        }

        return filterName;
    }

    private URL[] loadJars(final Set<String> featureJars, String... libPaths) throws IOException {

        List<File> files = new ArrayList<File>();

        for (String libPath : libPaths) {

            File plusRoot = new File(libPath);
            LOGGER.info(">plusRoot，exists:[{}],getPath:[{}],getAbsolutePath:[{}],getName:[{}],", plusRoot.exists(),
                    plusRoot.getPath(), plusRoot.getAbsolutePath(), plusRoot.getName());

            if (!plusRoot.exists() || !plusRoot.isDirectory()) {
                return null;
            }

            File[] jarFiles = plusRoot.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {

                    if (name.endsWith(".jar") && featureJars.contains(name)) {
                        return true;
                    }

                    return false;
                }

            });

            if (jarFiles == null || jarFiles.length == 0) {
                continue;
            }

            files.addAll(Arrays.asList(jarFiles));
        }

        List<URL> jarURLs = new ArrayList<URL>();

        for (File jar : files) {
            try {
                LOGGER.info("> jar.getAbsolutePath() >{} ", jar.getAbsolutePath());
                URL jarUrl = new URL("file:" + jar.getAbsolutePath());

                jarURLs.add(jarUrl);
            }
            catch (MalformedURLException e) {
                try {
                    throw e;
                }
                catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        }

        URL[] jarURLArray = new URL[jarURLs.size()];

        jarURLs.toArray(jarURLArray);
        return jarURLArray;
    }

    public String getJarPath() {

        String jarFilePath = GatewayClassLoaderFactory.class.getProtectionDomain().getCodeSource().getLocation()
                .getFile();
        try {
            jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // ignore
        }

        return jarFilePath;
    }

    public String getFilterJarPath() {

        String libPath = getJarPath();

        int end = libPath.indexOf("bin");

        int start = 0;

        if (end < 0) {
            LOGGER.info("> bin目录不存在，退出GatewayLoadLineRunner， JARPATH:{}", getJarPath());
            return null;
        }
        if (libPath.contains(StringHelper.MAOHAO_SEPARATOR)) {
            start = libPath.indexOf(":") + 1;
        }
        libPath = libPath.substring(start, end);

        libPath = libPath + THIRDPARTY;
        return libPath;
    }
}
