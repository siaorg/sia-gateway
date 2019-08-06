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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peihua 
 * 
 *     Ribbon discovery filter context, stores the attributes based on which the server matching will be
 *         performed.
 *
 */
public class DefaultRibbonFilterContext implements RibbonFilterContext {

    /**
     * Filter attributes.
     */
    private final Map<String, String> attributes = new HashMap<String, String>();

    /**
     *
     * @param key
     *            the attribute key
     * @param value
     *            the attribute value
     */
    @Override
    public RibbonFilterContext add(String key, String value) {

        attributes.put(key, value);
        return this;
    }

    /**
     *
     * @param key
     *            the attribute key
     * @return the attribute value
     */
    @Override
    public String get(String key) {

        return attributes.get(key);
    }

    /**
     *
     * @param key
     *            the context attribute
     */
    @Override
    public RibbonFilterContext remove(String key) {

        attributes.remove(key);
        return this;
    }

    /**
     * Retrieves the attributes.
     *
     * @return the attributes
     */
    @Override
    public Map<String, String> getAttributes() {

        return Collections.unmodifiableMap(attributes);
    }
}
