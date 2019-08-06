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


package com.creditease.gateway.topology.intercept;

import java.util.HashMap;
import java.util.Map;

import com.creditease.gateway.topology.Event;

/**
 * 拓扑上下文实现
 * 
 * @author peihua
 */

@SuppressWarnings("rawtypes")
public class ThreadContext extends HashMap implements InterceptContext {

    private static final long serialVersionUID = -7111489572045255441L;

    private final transient Event event;

    public Event getEvent() {

        return event;
    }

    public ThreadContext(Event e) {
        event = e;
    }

    @Override
    public <T> void put(Class<T> key, T value) {

        if (key == null || value == null) {
            return;
        }

        put(key.getName(), value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {

        if (key == null) {
            return null;
        }

        return (T) get(key.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put(String key, Object value) {

        if (checkEmpty(key) || value == null) {
            return;
        }

        super.put(key, value);
    }

    /**
     * @param key
     * @return
     */
    private boolean checkEmpty(String key) {

        return key == null || "".equals(key);
    }

    @Override
    public Object get(String key) {

        if (checkEmpty(key)) {
            return null;
        }

        return super.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getAll() {

        return this;
    }
}
