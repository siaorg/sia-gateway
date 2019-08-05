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

/** 
 *  缓存管理
 * 
 *  @author peihua
 * 
 * */

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class LocalCacheManager implements ApplicationContextAware {

	public static LocalCacheManager cacheMgr = null;

	private L1Cache l1cache;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		LocalCacheManager.cacheMgr = applicationContext.getBean(LocalCacheManager.class);
	}

	enum L1CacheState {
		/**
		 * NOEXISTS：不存在
		 * GOOD：可用
		 * */
		NOEXISTS, GOOD
	}

	protected LocalCacheManager() {

		l1cache = new L1Cache();
	}

	/**
	 * 注册需要L1 Cache的Key
	 * 
	 * @param region
	 * @param key
	 * @param expireSeconds
	 *            以分钟为秒，抵达过期时限时会主动从remote缓存拉去值
	 */
	public void enableL1Cache(String key) {

		this.l1cache.register(key);
	}

	/**
	 * 取消L1 Cache
	 * 
	 * @param region
	 * @param key
	 */
	public void disableL1Cache(String key) {

		this.l1cache.unregister(key);
	}


	/**
	 * 获取L1Cache的个数
	 * 
	 * @return
	 */
	public int getL1CacheCount() {

		return this.l1cache.getCacheObjectCount();
	}


	 /**
	  * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {

		/**
		 * 先检查是否有L1Cache, 如果存在且没过期则返回true
		 */
		if (this.l1cache.exists(key) == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 默认ASYNC操作
	 * 
	 * @param region
	 * @param key
	 * @param handler
	 */
	public void del(String key) {

		l1cache.del(key);

	}


	/**
	 * 默认ASYNC操作
	 * 
	 * @param region
	 * @param key
	 * @param value
	 * @param handler
	 */
	public void put(String key, Object value) {
		
		l1cache.put(key, value);

	}

	/**
	 * SYNC操作
	 * 
	 * @param region
	 * @param key
	 * @return
	 */
	public Object get(String key) {

		L1CacheObj l1c = this.l1cache.get(key);

		if (l1c != null && (l1c.state == L1CacheState.GOOD)) {

			return l1c.getObj();
		} else {
			return null;
		}
	}

}
