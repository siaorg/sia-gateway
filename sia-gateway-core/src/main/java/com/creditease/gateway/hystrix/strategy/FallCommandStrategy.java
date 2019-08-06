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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.hystrix.FallbackEvent;

/**
 * TODO:FallCommandStrategy处理
 * 
 * @author peihua
 * 
 */

public class FallCommandStrategy extends FallbackStrategy {

    private static Logger logger = LoggerFactory.getLogger(FallCommandStrategy.class);

    public FallCommandStrategy(FallbackEvent event) {
        super(event);
    }

    @Override
    public void strategy(FallbackEvent event) {

        logger.error("> Instance{},has exception:{}", event.getInstanceId(), event.getFallbackMsg());

    }

    @Override
    public String getFallbackType() {

        return "COMMANDEXCEPTION";
    }
}
