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

import com.creditease.gateway.localcache.LocalCacheManager.L1CacheState;
/***
 * 
 * @author admin
 *
 */
public class L1CacheObj {

	private Object obj;
	
	protected L1CacheState state;

	public L1CacheObj(Object obj, L1CacheState state) {
		this.obj = obj;
		this.state = state;
	}

	public Object getObj() {

		return obj;
	}

	public L1CacheState getState() {

		return state;
	}
}
