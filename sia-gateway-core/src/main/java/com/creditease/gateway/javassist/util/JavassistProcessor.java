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


package com.creditease.gateway.javassist.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

/**
 * 字节码改写Processor
 * 
 * @Author:peihua
 * 
 */
public final class JavassistProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProcessor.class);

    private static JavassistProcessor javassist = new JavassistProcessor();

    public static JavassistProcessor instance() {

        if (null == javassist) {
            javassist = new JavassistProcessor();
        }
        return javassist;
    }

    @SuppressWarnings("rawtypes")
    public void hookExecuteBefore(Class c, String className, String methodName, String preCode) {

        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(c);
            pool.insertClassPath(classPath);

            CtClass ctClass = pool.get(className);
            CtMethod cm = ctClass.getDeclaredMethod(methodName);
            cm.insertBefore(preCode);

            ctClass.writeFile();
            ctClass.toClass();

        }
        catch (Exception ex) {
            LOGGER.error("JavassistUtils EXECUTE onCacheRefreshed  FAIL...", ex);
        }
    }

    @SuppressWarnings("rawtypes")
    public void hookExecuteAfter(Class c, String className, String methodName, String proCode) {

        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(c);
            pool.insertClassPath(classPath);

            CtClass ctClass = pool.get(className);
            CtMethod cm = ctClass.getDeclaredMethod(methodName);
            cm.insertAfter(proCode);

            ctClass.writeFile();
            ctClass.toClass();

        }
        catch (Exception ex) {
            LOGGER.error("JavassistUtils EXECUTE onCacheRefreshed  FAIL...", ex);
        }
    }

    @SuppressWarnings("rawtypes")
    public void hookExecuteBefore(Class c, String className, String methodName, String preCode, String field)
            throws Exception {

        try {
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(c);
            pool.insertClassPath(classPath);

            CtClass ctClass = pool.get(className);

            ctClass.addField(CtField.make(field, ctClass));

            CtMethod cm = ctClass.getDeclaredMethod(methodName);
            cm.insertBefore(preCode);

            ctClass.writeFile();

            ctClass.toClass();

        }
        catch (Exception ex) {
            LOGGER.error("JavassistUtils EXECUTE onCacheRefreshed  FAIL...", ex);
        }
    }

}
