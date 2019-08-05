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


package com.creditease.gateway.domain;

import java.util.Objects;

/**
 *  
 *  Counter公共Domain
 *  
 *  @author peihua
 *
 * */

public class CounterPair {
	
	private String counterkey;

	public String getCounterkey() {
		return counterkey;
	}

	public void setCounterkey(String counterkey) {
		this.counterkey = counterkey;
	}

	public String getCountervalue() {
		return countervalue;
	}

	public void setCountervalue(String countervalue) {
		this.countervalue = countervalue;
	}

	private String countervalue;


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CounterPair that = (CounterPair) o;
		return Objects.equals(counterkey, that.counterkey);
	}
}
