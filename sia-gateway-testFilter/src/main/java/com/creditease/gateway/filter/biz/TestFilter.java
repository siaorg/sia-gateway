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


package com.creditease.gateway.filter.biz;

import com.creditease.gateway.annotation.FilterAnnotation;
import com.creditease.gateway.template.absfilter.AbstractThirdPartyFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * 业务Filter模板实例Demo
 * 
 * @author peihua
 */

@FilterAnnotation(type = "pre", order = 9, isenabled = true, compname = "测试组件", compdesc = "heel2444422ooo")
public class TestFilter extends AbstractThirdPartyFilter {

    @Override
    public void process(RequestContext ctx, String routeid) {

        System.out.println(">> hello  testFilter4 !");
    }

    @Override
    public Class<?> getClassValue() {

        return TestFilter.class;
    }

}
