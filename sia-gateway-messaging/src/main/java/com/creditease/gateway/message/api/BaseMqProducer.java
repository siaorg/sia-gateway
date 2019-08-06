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

import com.creditease.gateway.message.api.domain.MqMessage;

/**
 * 
 * 消息生产抽象类
 * 
 * @author peihua
 */

public abstract class BaseMqProducer {

    protected static final Logger logger = LoggerFactory.getLogger(BaseMqProducer.class);

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected BaseMqProducer() {
        logger.info("MQProducer init without appname..");
    }

    protected BaseMqProducer(String appname) {
        logger.info("MQProducer init..");

        this.name = appname;
    }

    /**
     * MQ生产者start方法
     *
     * @return void
     */
    public abstract void start();

    /**
     * MQ生产者shutdown方法
     *
     * @return void
     */
    public abstract void shutdown();

    /**
     * MQ生产者send消息
     *
     * @param msg
     * @return void
     */
    public abstract void send(MqMessage msg);

}
