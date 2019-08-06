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


package com.creditease.gateway.ribbon;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.creditease.gateway.ribbon.updater.RibbonUpdaterEtcd;
import com.creditease.gateway.ribbon.updater.RibbonUpdaterStatic;

/**
 * Ribbon分发处理器
 * 
 * @author peihua
 * 
 **/

@Component
public class RibbonUpdaterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RibbonUpdaterFactory.class);

    protected static Map<String, RibbonUpdaterInterface> RibbonMapping = new HashMap<String, RibbonUpdaterInterface>();

    @Autowired
    RibbonUpdaterStatic rlofserver;

    @Autowired
    RibbonUpdaterEtcd retcd;

    /**
     * 初始化Zuul dispatch相关
     */
    private RibbonUpdaterFactory() {

        LOGGER.info("> ZuulLocatorFactory init >");

        locatorInitlization();
    }

    private void locatorInitlization() {

        RibbonMapping.put(RibbonServerSource.getDefault().toString(), new RibbonUpdaterStatic());
        RibbonMapping.put(RibbonServerSource.LISTOFSERVERETCD.toString(), new RibbonUpdaterEtcd());
    }

    public RibbonUpdaterInterface getLoctorUpdater(String routeribbonrule) {

        return RibbonMapping.get(routeribbonrule);
    }
}
