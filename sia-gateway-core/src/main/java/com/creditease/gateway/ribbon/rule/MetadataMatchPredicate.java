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


package com.creditease.gateway.ribbon.rule;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.creditease.gateway.ribbon.context.RibbonFilterContext;
import com.creditease.gateway.ribbon.context.RibbonFilterContextHolder;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

/**
 * 
 * 基于元数据匹配的断言
 * 
 * @author peihua
 * 
 */
public class MetadataMatchPredicate extends BaseDiscoveryEnabledPredicate {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean apply(DiscoveryEnabledServer server) {

        final RibbonFilterContext context = RibbonFilterContextHolder.getCurrentContext();

        /**
         * @comment: attributes是request请求中包含的{k:v}键值对 : eg=>{"version", "1"}
         * 
         **/
        final Set<Map.Entry<String, String>> attributes = Collections
                .unmodifiableSet(context.getAttributes().entrySet());

        /**
         * @comment: metadata是OrignalServer存储的{k:v}键值对
         * 
         **/
        final Map<String, String> metadata = server.getInstanceInfo().getMetadata();

        return metadata.entrySet().containsAll(attributes);
    }
}
