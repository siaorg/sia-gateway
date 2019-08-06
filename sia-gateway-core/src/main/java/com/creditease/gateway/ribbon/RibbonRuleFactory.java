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

import org.springframework.stereotype.Component;

import com.creditease.gateway.domain.RibbonnRule;
import com.creditease.gateway.ribbon.rule.MetadataMatchRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;

/**
 * 
 * 路由规则工厂
 * 
 * @author peihua
 * 
 */

@Component
public class RibbonRuleFactory {

    private Map<String, IRule> ruleCache = new HashMap<String, IRule>(8);

    private IRule rrRule = new RoundRobinRule();

    private IRule metadatRule = new MetadataMatchRule();

    public RibbonRuleFactory() {

        ruleCache.put(RibbonnRule.ROUNDROUBIN, rrRule);

        ruleCache.put(RibbonnRule.METADATARULE, metadatRule);
    }

    /**
     * get rule by rulekey
     *
     */
    public IRule getIRule(String rulekey) {

        return ruleCache.get(rulekey);

    }
}
