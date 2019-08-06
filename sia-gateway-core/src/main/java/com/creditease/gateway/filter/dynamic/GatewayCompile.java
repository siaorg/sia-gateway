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


package com.creditease.gateway.filter.dynamic;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.creditease.gateway.annotation.FilterAnnotation;
import com.creditease.gateway.constant.GatewayConstant.FilterEnable;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.helper.IoHelper;
import com.creditease.gateway.helper.ReflectionHelper;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.message.ZuulHandler;
import com.creditease.gateway.service.ZuulService;
import com.netflix.zuul.DynamicCodeCompiler;

/**
 * 
 * JavaCompile description: 动态加载第三方Filter插件
 * 
 * @author peihua
 *
 */

public class GatewayCompile implements DynamicCodeCompiler {

	private final static Logger LOGGER = LoggerFactory.getLogger(GatewayCompile.class);
	private String javaPath;
	private final String packageName;
	private ZuulService zuulservice;

	public static CountDownLatch doneSignal = null;
	
	@Autowired
	protected ZuulHandler handler;
	
	@Value("${spring.gateway.admin.name}")
	private String adminName;
	
	@Value("${spring.application.name}")
	private String groupName;
	
	@Autowired 
	GatewayLoadLineRunner loadRunner;
	
	public GatewayCompile(String javaPath, String packageName, ZuulService zuulservice) {
		this.javaPath = javaPath;
		this.packageName = packageName;
		this.zuulservice = zuulservice;
	}

	@Override
	public Class<?> compile(String sCode, String sName) throws Exception {

		return null;
	}

	@Override
	public Class<?> compile(File file) throws Exception {
		String jarName = file.getName();
		if(doneSignal!=null)
		{
			LOGGER.info(">开始加载动态组件模块，compile doneSignal is :"+doneSignal.getCount());
		}else
		{
			LOGGER.info(">开始加载动态组件模块，doneSignal is null!!!");
		}		
		try {
			LOGGER.info(">step1 开始加载动态组件模块 ，File getAbsolutePath is :" + file.getAbsolutePath());

			jarName = jarName.substring(0, jarName.lastIndexOf("."));
			GatewayClassLoaderFactory ins = GatewayClassLoaderFactory.instance();
			ClassLoader loader = ins.getClassLoader(javaPath, jarName, file);
			String className = new String();
			
			if(jarName.contains(StringHelper.HENXIAN_SEPARATOR))
			{
				className = jarName.substring(0, jarName.indexOf("-"));
			}else
			{
				className = jarName;
			}
			String classStr = packageName + "." + className;
			LOGGER.info(">step2 开始加载动态组件模块 , className is :" + classStr);


			Class<?> clazz = loader.loadClass(classStr.trim());
			Map<String, Object> mAnnoInfos = new HashMap<String, Object>(16);
			mAnnoInfos = ReflectionHelper.getAnnotationAllFieldValues(clazz, FilterAnnotation.class);

			String type =(String) mAnnoInfos.get("type");
			int order =(int) mAnnoInfos.get("order");
			boolean isenabled =(boolean) mAnnoInfos.get("isenabled");
			String compname =(String) mAnnoInfos.get("compname");


			String compdesc =(String) mAnnoInfos.get("compdesc");

			LOGGER.info(">step3 組建解析成功：>>> type:"+type+">>> order:"+order+">>> isenabled:"+isenabled+">>> compname:"+compname+">>> compdesc:"+compdesc);
			
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			type = type.toUpperCase();
			String status = FilterEnable.notok.toString();
			if (isenabled) {
				status = FilterEnable.ok.toString();
			}
			CompInfo cmp = new CompInfo(compname, className, type, compdesc, order, currentTime, status,groupName);

			if (zuulservice != null) {
				zuulservice.registerFilter(cmp);
			}
			return clazz;
		} 
		catch(ClassNotFoundException e)
		{
			LOGGER.info("> 加载Filter失败：ClassNotFoundException 原因：" , e);
			doneSignal = new CountDownLatch(2);
			errorHandler(e , jarName);
		}
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("> 加载Filter失败：getCause 原因：" , e);
			doneSignal = new CountDownLatch(2);
			errorHandler(e , jarName);
		}finally
		{
			LOGGER.info(">countDown execute... step fianlly  doneSignal ->"+doneSignal );
			if(doneSignal!=null)
			{
				LOGGER.info("> step fianlly doneSignal->" + doneSignal.getCount());
				while( doneSignal.getCount()>0)
				{
					doneSignal.countDown();
				}
			}else
			{
				LOGGER.info(">step 4 doneSignal is still null" );
			}
		}
		return AbstractClass.class;
	}
	
	public void errorHandler(Exception e , String jarName) throws IOException
	{
		e.printStackTrace();
		
		LOGGER.info(">加载Filter失败原因：" + e);
		
		/**
		 * step1: Sleep一下
		 * 
		 * **/
		try {
			
			Thread.sleep(1000);
		
			
		} catch (Exception e1) {
			
			LOGGER.error(">> 通知ADMIN删除Exp：" + e1.getMessage());
		}
		
		/**
		 * step2: 自己删除
		 * 
		 * **/
		String filterName = GatewayClassLoaderFactory.instance().getFilterNameByJarName(jarName);

		boolean rst = GatewayClassLoaderFactory.instance().removeFilterComponent(filterName);
		
		LOGGER.error(">> 删除组件RST：" +rst);

		Map<String, File> filterPathMap = GatewayClassLoaderFactory.instance().getFilterPathMap();

		File fileremove = filterPathMap.get(filterName);

		if (fileremove != null) {
			
			String path = fileremove.getPath();
			
			if(com.creditease.gateway.helper.JvmToolHelper.isWindows())
			{
				path = path.replace("/", "\\");				
				path = fileremove.getAbsolutePath();
				
			}else
			{
				path = fileremove.getAbsolutePath();

			}
			
			int fileDeltetrst = IoHelper.deleteFile(path);
			
			LOGGER.info("> 删除结果：" + fileDeltetrst);
		}
		
	}
	

}
