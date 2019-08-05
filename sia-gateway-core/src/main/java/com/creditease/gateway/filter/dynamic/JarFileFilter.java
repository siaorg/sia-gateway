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

/**
 * @author peihua
 * 
 * JavaFileFilter description: JAR格式验证
 *
 */
public class JarFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        // 验证文件是否已.jar文件结尾
        return name.endsWith(".jar");
    }
}
