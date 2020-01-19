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


package com.creditease.gateway.service;

/**
 * 统计服务
 *
 * @author peihua
 *
 * */

public interface StatisticService {

    /**
     * 增加计数
     *
     * @param counterName
     * @return void
     */
    void increment(String counterName);

    /**
     * 获得计数
     *
     * @param counterName
     * @return int
     */
    long getCount(String counterName);

}
