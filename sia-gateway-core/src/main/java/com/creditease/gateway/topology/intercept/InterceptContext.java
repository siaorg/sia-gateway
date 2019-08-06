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

import java.util.Map;

/**
 * 拓扑上下文 接口
 * 
 * @author peihua
 */

public interface InterceptContext {

    public <T> T get(Class<T> c);

    public <T> void put(Class<T> c, T obj);

    public Object get(String name);

    public void put(String name, Object obj);

    public Map<String, Object> getAll();

    public void putAll(Map<String, Object> m);

    public int size();
}
