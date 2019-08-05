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


package com.creditease.gateway.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FIFOCache<K, V> {

    private final int MAX_CACHE_SIZE;
    private final float DEFAULT_LOAD_FACTORY = 0.75f;

    LinkedHashMap<K, V> map;

    public FIFOCache(int cacheSize) {
        MAX_CACHE_SIZE = cacheSize;
        int capacity = (int) Math.ceil(MAX_CACHE_SIZE / DEFAULT_LOAD_FACTORY) + 1;
        /*
         * 第三个参数设置为true，代表linkedlist按访问顺序排序，可作为LRU缓存 第三个参数设置为false，代表按插入顺序排序，可作为FIFO缓存
         */
        map = new LinkedHashMap<K, V>(capacity, DEFAULT_LOAD_FACTORY, false) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {

                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public LinkedHashMap<K, V> getMap() {

        return map;
    }

    public synchronized void put(K key, V value) {

        map.put(key, value);
    }

    public synchronized V get(K key) {

        return map.get(key);
    }

    public synchronized void remove(K key) {

        map.remove(key);
    }

    public synchronized Set<Map.Entry<K, V>> getAll() {

        return map.entrySet();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            stringBuilder.append(String.format("%s: %s  ", entry.getKey(), entry.getValue()));
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {

        FIFOCache<Integer, Integer> lru1 = new FIFOCache<>(5);
        lru1.put(1, 1);
        lru1.put(2, 2);
        lru1.put(3, 3);
        System.out.println(lru1);
        lru1.get(1);
        System.out.println(lru1);
        lru1.put(4, 4);
        lru1.put(5, 5);
        lru1.put(6, 6);
        System.out.println(lru1);
    }
}
