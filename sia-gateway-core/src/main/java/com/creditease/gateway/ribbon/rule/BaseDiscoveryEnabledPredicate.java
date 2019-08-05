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

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

/**
 * 
 * 远端服务断言抽象类
 * 
 * @author peihua
 * @modifyied by peihua
 * 
 */

public abstract class BaseDiscoveryEnabledPredicate extends AbstractServerPredicate {

    @Override
    public boolean apply(PredicateKey input) {

        boolean flag = (input != null) && input.getServer() instanceof DiscoveryEnabledServer
                && apply((DiscoveryEnabledServer) input.getServer());

        return flag;
    }

    /**
     * Returns whether the specific matches this predicate.
     *
     */
    protected abstract boolean apply(DiscoveryEnabledServer server);
}
