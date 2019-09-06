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


package com.creditease.gateway.service.impl;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import com.creditease.gateway.service.StatisticService;
import com.google.common.collect.Maps;

/**
 * 统计组件
 *
 * @author peihua
 */

public class StatisticServiceImpl implements StatisticService {

    private Map<String, LongAdder> map = Maps.newConcurrentMap();

    /**
     * 计数操作
     */
    @Override
    public void increment(String counterName) {

        LongAdder longAdder = map.get(counterName);

        if (longAdder == null) {
            map.putIfAbsent(counterName, new LongAdder());

            longAdder = map.get(counterName);
        }

        longAdder.increment();
    }

    /**
     * 获得计数
     */
    @Override
    public long getCount(String counterName) {

        LongAdder longAdder = map.get(counterName);

        return longAdder == null ? 0 : longAdder.sum();
    }
}
