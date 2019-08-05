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


package com.creditease.gateway.handler;

import com.creditease.gateway.message.api.MqHandler;
import com.creditease.gateway.message.api.domain.MqMessage;
import com.creditease.gateway.sink.abs.BaseDataCollector;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 
 * 数据Sink消费管理接口
 * 
 * @author peihua
 **/
public class DataConsumerHandler implements MqHandler {

    @Autowired
    private BaseDataCollector dataCollector;

    @Override
    public void handle(List<MqMessage> msgList) {

        Map<String, List<String>> map = new HashMap<>(8);

        for (MqMessage msg : msgList) {
            if (map.get(msg.getTopic()) == null) {
                map.put(msg.getTopic(), new LinkedList<>());
            }

            map.get(msg.getTopic()).add(msg.getMsg());
        }

        dataCollector.sinkToSource(map);
    }

    @Override
    public void handle(MqMessage msg) {

        dataCollector.sinkToSource(msg.getTopic(), msg.getMsg());
    }

}
