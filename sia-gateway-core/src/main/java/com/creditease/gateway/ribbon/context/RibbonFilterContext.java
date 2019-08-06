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


package com.creditease.gateway.ribbon.context;

import java.util.Map;

/**
 * 
 * @author peihua
 * 
 *         Ribbon discovery filter context stores the attributes based on which the server matching will be performed.
 *
 */
public interface RibbonFilterContext {

    /**
     * Adds the context attribute.
     *
     */
    RibbonFilterContext add(String key, String value);

    /**
     * Retrieves the context attribute.
     */
    String get(String key);

    /**
     * Removes the context attribute.
     *
     */
    RibbonFilterContext remove(String key);

    /**
     * Retrieves the attributes.
     */
    Map<String, String> getAttributes();
}
