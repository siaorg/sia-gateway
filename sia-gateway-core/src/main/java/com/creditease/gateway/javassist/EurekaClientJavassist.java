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


package com.creditease.gateway.javassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.javassist.util.JavassistProcessor;

/**
 * 改写刷新逻辑
 * 
 * @Author:peihua
 * 
 */
public class EurekaClientJavassist {

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaClientJavassist.class);
    private static final String CLASSNAME = "org.springframework.cloud.netflix.eureka.CloudEurekaClient";
    private static final String METHODNAME = "onCacheRefreshed";

    public void hookCacheRefresh() {

        try {
            StringBuffer sbf = new StringBuffer();
            sbf.append("super.onCacheRefreshed();");
            String codePre = sbf.toString();

            JavassistProcessor.instance().hookExecuteBefore(this.getClass(), CLASSNAME, METHODNAME, codePre);

        }
        catch (Exception ex) {
            LOGGER.error("EurekaClientJavassist EXECUTE onCacheRefreshed  FAIL...", ex);
        }
    }
}
