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


package com.creditease.gateway.localcache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creditease.gateway.localcache.LocalCacheManager.L1CacheState;

/**
 * @author admin
 * L1Cache 
 *
 */
public class L1Cache {

	Map<String, Integer> cacheToken = new ConcurrentHashMap<String, Integer>();
	Map<String, Object> cacheObjs = new ConcurrentHashMap<String, Object>();

	private static final int MAXVALUE = 100;
	/**
	 * 注册需要L1Cache的Key
	 * 
	 * @param key
	 * @
	 *           
	 */
	public void register(String key) {


		this.cacheToken.put(key,100);
	}

	/**
	 * 取消L1Cache的Key
	 * 
	 * @param key
	 */
	public void unregister(String key) {

		this.cacheToken.remove(key);
		this.cacheObjs.remove(key);
	}

	/**
	 * 存放L1 Cache
	 * 
	 * @param key
	 * @param obj
	 */
	public void put(String key, Object obj) {

		if (null == key || null == obj) {
			return;
		}
		if (this.cacheToken.containsKey(key)) {
			this.cacheObjs.put(key, obj);
		}
	}


	/**
	 * 删除L1 Cache
	 * 
	 * @param key
	 */
	public void del(String key) {

		if (null == key) {
			return;
		}
		if (this.cacheToken.containsKey(key)) {
			this.cacheObjs.remove(key);
		}
	}

	/**
	 * 检测L1 Cache是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {

		if (null == key) {
			return false;
		}
		if (!this.cacheToken.containsKey(key)) {
			return false;
		}
		return true;
	}

	/**
	 * 取L1 Cache
	 * 
	 * @param key
	 * @return
	 */
	public L1CacheObj get(String key) {

		int expire = 0 ;
		
		if(cacheToken.get(key) != null)
		{
			 expire = cacheToken.get(key);
		}
		
		/**
		 * CASE 1：不存在L1 Cache
		 */
		if (expire != MAXVALUE) {
			return new L1CacheObj(null, L1CacheState.NOEXISTS);
		}
		/**
		 * CASE 2: 存在L1 Cache，且米有过期
		 */
		
		return new L1CacheObj(this.cacheObjs.get(key), L1CacheState.GOOD);
		
		
	}

	/**
	 * 释放L1 Cache
	 */
	public void release() {

		this.cacheToken.clear();
		this.cacheObjs.clear();
	}

	/**
	 * 获取L1 Cache的对象数目
	 * 
	 * @return
	 */
	public int getCacheObjectCount() {

		return this.cacheObjs.size();
	}
}
