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


package com.creditease.gateway.cache;

import java.util.LinkedHashMap;

/**
 * LRU最近最少使用CACHE
 * 
 * @author peihua
 * 
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final int SIZE;

    public LRUCache(int size) {
        /**
         * int initialCapacity, float loadFactor, boolean accessOrder 这3个分别表示容量，加载因子和是否启用LRU规则
         */
        super(size, 0.75f, true);
        SIZE = size;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {

        return size() > SIZE;
    }

}
