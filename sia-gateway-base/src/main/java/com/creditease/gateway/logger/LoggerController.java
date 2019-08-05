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


package com.creditease.gateway.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.message.Message;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yongbiao
 * @Date: 2019/3/11 14:18
 */
@RestController
public class LoggerController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @RequestMapping(value = "setlevel/{name}/{level}")
    @ResponseBody
    public String setLoggerLevel(@PathVariable("name") String name, @PathVariable("level") String level) {

        ObjectMapper mapper = new ObjectMapper();
        Level logbackLevel = Level.valueOf(level);

        try {
            if (!level.equalsIgnoreCase(logbackLevel.toString())) {
                String noLevel = "No logger level:" + level;
                logger.warn(noLevel);

                return mapper.writeValueAsString(new Message(noLevel, Message.ResponseCode.PARAM_ERROR_CODE.getCode()));
            }

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            if (!name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME) && loggerContext.exists(name) == null) {
                String noLogger = "No logger name:" + name;
                logger.warn(noLogger);

                Message msg = new Message(noLogger, Message.ResponseCode.PARAM_ERROR_CODE.getCode());

                return mapper.writeValueAsString(msg);
            }

            loggerContext.getLogger(name).setLevel(logbackLevel);

            logger.info("Set logger level success! name:" + name + ", level:" + level);

            Map<String, String> map = new HashMap<>(2);
            map.put("name", name);
            map.put("level", logbackLevel.toString());

            return mapper.writeValueAsString(new Message(map, Message.ResponseCode.SUCCESS_CODE.getCode()));

        } catch (IOException e) {
            logger.error("ObjectMapper writeValueAsString failed!", new GatewayException(ExceptionType.CommExeption, e));

            return null;
        }

    }

    @RequestMapping(value = "getlevel/{name:.+}")
    @ResponseBody
    public String getLoggerLevel(@PathVariable("name") String name) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            if (!name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME) && loggerContext.exists(name) == null) {
                String noLogger = "No logger name:" + name;
                logger.warn(noLogger);

                return mapper.writeValueAsString(new Message(noLogger, Message.ResponseCode.PARAM_ERROR_CODE.getCode()));
            }

            String loggerName = name;
            Level level = loggerContext.getLogger(loggerName).getLevel();

            while (level == null) {
                int lastIndexOf = loggerName.lastIndexOf(".");
                if (lastIndexOf <= 0) {
                    break;
                }

                loggerName = loggerName.substring(0, lastIndexOf);
                level = loggerContext.getLogger(loggerName).getLevel();
            }

            if (level == null) {
                level = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getLevel();
            }

            Map<String, String> map = new HashMap<>(2);
            map.put("name", name);
            map.put("level", level.toString());

            logger.info("Get logger level. name:" + name + ", level:" + level.toString());

            return mapper.writeValueAsString(new Message(map, Message.ResponseCode.SUCCESS_CODE.getCode()));

        } catch (IOException e) {
            logger.error("ObjectMapper writeValueAsString failed!", new GatewayException(ExceptionType.CommExeption, e));

            return null;
        }
    }
}
