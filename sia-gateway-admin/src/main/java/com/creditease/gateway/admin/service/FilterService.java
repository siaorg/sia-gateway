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


package com.creditease.gateway.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.BwfilterObj;
import com.creditease.gateway.helper.JsonHelper;
import com.creditease.gateway.message.Message;

/**
 * 黑白名单管理服务
 * 
 * @author peihua
 * 
 * 
 * */

@Service
public class FilterService extends BaseAdminService{

	 /**
     * 給某一個路由添加白名单
     * 
     */

    public boolean addWhiteList2Route(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        boolean flag = redisRepository.addWBList2Route(zuulGroupName, routeid, fo);

        String option = "add";

        if(flag)
        {
        	 flag = remoteCallZuul(zuulGroupName, routeid, fo,option);
        	
        	  if (!flag) {
        		  redisRepository.deleteRedis(zuulGroupName, routeid, fo);
              }
        }
        return flag;
    }

    /**
     * 查询某一个route所有的白名单
     */
    public Object queryWhiteList(String zuulGroupName, String routeid)throws Exception  {

        Object rs = redisRepository.queryWhiteList(zuulGroupName, routeid);

        return rs;

    }
	
    public boolean updateWBList2Route(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

    	boolean flag =  redisRepository.updateWBList2Route(zuulGroupName, routeid, fo);

        String option = "update";


        if(flag)
        {
        	 flag = remoteCallZuul(zuulGroupName, routeid, fo,option);
        	
        	  if (!flag) {
        		  redisRepository.deleteRedis(zuulGroupName, routeid, fo);
              }
        }
        return flag;
    }

    /**
     * 通知远端路由Gateway
     * **/

    public boolean remoteCallZuul(String zuulGroupName, String routeid, BwfilterObj fo,String option)throws Exception
    {
    	boolean remoteCall = true;
    	 // 1：getInstances
        List<String> rs = zuulDiscovery.getServiceList(zuulGroupName);
        
        //SET BWFilterObj to Gateway for update L1Cache
        try {
            for (String instance : rs) {
            	
                // 3:call remote for each,return false when has error or not 200 OK                

                LOGGER.info("updateRedisHset url：{}",instance);
                
                Map<String,String> request = new HashMap<String,String>(8);
                
                request.put(GatewayConstant.ADMINOPTPARMKEY, JsonHelper.toString(fo));
                
                Message msg = new Message(request);
                
                String u = "http://" + instance;

                String rst;

                if(("add").equals(option) || ("update").equals(option))
                {
                     rst = handler.executeHttpCmd(u,GatewayConstant.ADMINOPTKEY.UFC.getValue(),msg);
                }
                else{
                    rst = handler.executeHttpCmd(u,GatewayConstant.ADMINOPTKEY.DFC.getValue(),msg);
                }

                LOGGER.info("remoteCallZuul rst：{}", rst);

                if(rst!=null)
				{
					remoteCall = Boolean.parseBoolean(rst);
					
					if(remoteCall == false)
					{
						break;
					}
				}
              
            }
        }
        catch (Exception e) {

            remoteCall = false;
            throw e;
        }
        return remoteCall;
    }

    public boolean deleteWBList2Route(String zuulGroupName, String routeid, BwfilterObj fo) throws Exception {

        boolean flag = redisRepository.deleteRedis(zuulGroupName, routeid, fo);

        String option = "delete";

        if(flag)
        {
            flag = remoteCallZuul(zuulGroupName, routeid, fo,option);

        }
        return flag;
    }
	
}
