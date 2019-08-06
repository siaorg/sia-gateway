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


package com.creditease.gateway.javassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.javassist.util.JavassistProcessor;

/**
 * 增强ZUUL自带Filter机制问题
 * 
 * @author peihua
 *
 */
public class FilterLoaderJavassit {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilterLoaderJavassit.class);

	private static final String CLASSNAME = "com.netflix.zuul.FilterLoader";

	private static final String METHODNAME = "putFilter";

	/**
	 * 每次清除filter实例引用，防止内存泄露
	 */
	public FilterLoaderJavassit hookFileLoader() {

		try {
			StringBuffer sbf = new StringBuffer();
			sbf.append("hashFiltersByType.clear();");

			JavassistProcessor.instance().hookExecuteBefore(this.getClass(), CLASSNAME, METHODNAME, sbf.toString());

			return this;
			
		} catch (Exception e) {
			new GatewayException(ExceptionType.CoreException, e);
			LOGGER.error("", e);
		}
		return null;
	}

}
