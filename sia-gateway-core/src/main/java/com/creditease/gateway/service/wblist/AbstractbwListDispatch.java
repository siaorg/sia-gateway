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

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.creditease.gateway.domain.BwfilterObj;
import com.netflix.zuul.context.RequestContext;

/**
 * 黑白名单处理抽象类
 * 
 * @author peihua
 *  
 * */

public abstract class AbstractbwListDispatch   {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractbwListDispatch.class);

    protected static final String PROXY_KEY = "proxy";
    
    protected static Map<String, AbstractbwListDispatch> processorMapping = new HashMap<String, AbstractbwListDispatch>();

    /**
     * 添加 processor 映射
     * 
     * @param key
     * @param processor
     */
    public static void addProcessorMapping(String key, AbstractbwListDispatch processor) {

        if (null == key || null == processor)
        {
        	return;
        }

        processorMapping.put(key, processor);

        logger.info("ADD processor mapping: " + key + "=" + processor.getClass().getName());
        
    }

    /**
     * 
     * @param operation
     * @param obj
     * @param req
     * @return
     * @throws DispatchException
     */
   
	public void doProcess(String strategy, RequestContext ctx, BwfilterObj obj) {

    	AbstractbwListDispatch processor = findProcessor(strategy);
    	
    	String routeid = (String) ctx.get(PROXY_KEY);
    	
        if(processor!=null)
        {
        	 processor.process(ctx, obj,routeid);
        }
    }
	
    /**
     * get processor
     * 
     * @param key
     * @return
     */
    private AbstractbwListDispatch findProcessor(String key) {

        return processorMapping.get(key);
    }
	
	/**
	 * 黑白名单处理逻辑
	 * @param ctx
	 * @param obj
	 * @param routeid
	 * @return void
	 * 
	 * */
	public abstract void process(RequestContext ctx, BwfilterObj obj, String routeid);
	

}
