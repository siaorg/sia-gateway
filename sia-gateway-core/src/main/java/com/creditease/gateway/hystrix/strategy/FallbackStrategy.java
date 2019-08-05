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


package com.creditease.gateway.hystrix.strategy;

import com.creditease.gateway.hystrix.FallbackEvent;

/**
 * 熔断管理策略抽象类
 * 
 * @author peihua
 * 
 */

public abstract class FallbackStrategy {

    FallbackEvent event;

    public void executeStrategy() {

        event.setFallbackType(getFallbackType());

        strategy(event);

        event.emit();
    }

    public FallbackStrategy(FallbackEvent event) {
        this.event = event;
    }

    public abstract void strategy(FallbackEvent event);

    public abstract String getFallbackType();
}
