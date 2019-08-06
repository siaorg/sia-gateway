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

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

/**
 * @author: pengfeili23@creditease.cn
 * @Description: JSON 与对象之间互转
 * @date: 2018年6月27日 下午5:48:06
 */
public class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonHelper() {
    }

    public static boolean validateMap(String jsonString) {

        if (StringHelper.isEmpty(jsonString)) {
            return false;
        }

        jsonString = jsonString.trim();
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            return true;
        }

        return false;
    }

    public static String toString(Object obj) {

        if (null == obj)
        {
        	return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {

            // ignore
        }
        return obj.toString();
    }

    public static <T> T toObject(String jsonString, Class<T> c) {

        if (null == c || StringHelper.isEmpty(jsonString)) {
            return null;
        }

        try {
            return MAPPER.readValue(jsonString, c);
        } catch (Exception e) {

            // ignore
        }
        return null;
    }

    public static <T> List<T> toObjectArray(String jsonString, Class<T> c) {

        if (null == c || StringHelper.isEmpty(jsonString)) {
            return Collections.emptyList();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, c);

            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {

            // ignore
        }
        return null;
    }
}
