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


package com.creditease.gateway.message.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息消费抽象类
 * 
 * @author peihua
 */

public abstract class BaseMqConsumer {

    protected static final Logger logger = LoggerFactory.getLogger(BaseMqConsumer.class);

    private String name;

    public String getName() {
        return name;
    }

    public BaseMqConsumer() {

        logger.info("MqConsumer init without appName..");
    }

    public BaseMqConsumer(String appname) {

        this.name = appname;
        logger.info("MqConsumer init..");
    }

    /**
     * MQ消费者start方法
     *
     * @return void
     */
    abstract public void start();

    /**
     * MQ消费者shutdown方法
     *
     * @return void
     */
    abstract public void shutdown();

}
