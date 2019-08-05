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


package com.creditease.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 定时任务配置
 * 
 * @author peihua
 * 
 * */
@Configuration
public class MySchedulingConfigurer implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySchedulingConfigurer.class);

	@Value("${spring.application.name}") 
	public  String groupName;
	
    /**
     * 这里将定时任务线程池大小设为：处理器个数*2
     * 
     */
    private static final int POOLSIZE = Runtime.getRuntime().availableProcessors() * 2;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        TaskScheduler taskScheduler = mytaskScheduler();
        taskRegistrar.setTaskScheduler(taskScheduler);
        LOGGER.info("SET ThreadPoolTaskScheduler POOL AS " + POOLSIZE);
    }

    /**
     * 并行任务使用策略：多线程处理
     *
     * @return ThreadPoolTaskScheduler 线程池
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler mytaskScheduler() {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOLSIZE);
        scheduler.setThreadNamePrefix("Scheduling-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public String groupName()
    {
    	return groupName;
    }
}
