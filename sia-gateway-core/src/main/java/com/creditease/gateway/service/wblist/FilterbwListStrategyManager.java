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


package com.creditease.gateway.service.wblist;

import org.springframework.stereotype.Component;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.BwfilterObj;
import com.netflix.zuul.context.RequestContext;

/**
 * 黑白名单策略分發器
 * 
 * @author peihua
 * 
 **/

@Component
public class FilterbwListStrategyManager extends AbstractbwListDispatch {

    /**
     * 初始化gateway dispatch相关
     */
    private FilterbwListStrategyManager() {
        mappingProcessor();
    }

    /**
     * 添加映射不同黑白名单策略对应不同process
     */
    private void mappingProcessor() {

        AbstractbwListDispatch.addProcessorMapping(GatewayConstant.BWSTRATEGY.IP.getValue(), new FilterbyipProcessor());
        AbstractbwListDispatch.addProcessorMapping(GatewayConstant.BWSTRATEGY.API.getValue(),
                new FilterByDomainProcessor());

    }

    @Override
    public void process(RequestContext ctx, BwfilterObj obj, String routeid) {

        return;
    }
}
