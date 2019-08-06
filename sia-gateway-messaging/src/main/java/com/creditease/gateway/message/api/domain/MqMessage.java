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


package com.creditease.gateway.message.api.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * MqMessage公共Domain
 *
 * @author peihua
 */

public class MqMessage {

    private String topic;
    private String msg;

    private Map<String, Object> args;

    public MqMessage(String topic, String msg) {

        this.topic = topic;
        this.msg = msg;
    }

    public String getTopic() {
        return topic;
    }

    public String getMsg() {
        return msg;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public Object getArg(String key) {

        return args == null ? null : args.get(key);
    }

    public void addArg(String key, Object arg) {

        if (args == null) {
            args = new HashMap<>(8);
        }

        args.put(key, arg);
    }

    @Override
    public String toString() {
        return new StringBuilder("{topic:").append(topic).append(", msg:").append(msg)
                .append(", args:").append(args == null ? " " : args.toString()).append("}").toString();
    }

}
