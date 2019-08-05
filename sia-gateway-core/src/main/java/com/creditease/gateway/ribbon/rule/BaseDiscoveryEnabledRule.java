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

import org.springframework.util.Assert;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.PredicateBasedRule;

/**
 * @author peihua
 * 
 */
public abstract class BaseDiscoveryEnabledRule extends PredicateBasedRule {

    private final CompositePredicate predicate;

    public BaseDiscoveryEnabledRule(BaseDiscoveryEnabledPredicate discoveryEnabledPredicate) {
        Assert.notNull(discoveryEnabledPredicate, "Parameter 'discoveryEnabledPredicate' can't be null");
        this.predicate = createCompositePredicate(discoveryEnabledPredicate, new AvailabilityPredicate(this, null));
    }

    @Override
    public AbstractServerPredicate getPredicate() {

        return predicate;
    }

    private CompositePredicate createCompositePredicate(BaseDiscoveryEnabledPredicate discoveryEnabledPredicate,
            AvailabilityPredicate availabilityPredicate) {

        return CompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate).build();
    }
}
