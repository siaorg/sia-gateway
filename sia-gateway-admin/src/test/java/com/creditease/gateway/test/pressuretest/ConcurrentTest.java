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


package com.creditease.gateway.test.pressuretest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @description:
 * @author: guohuixie2
 * @create: 2019-03-11 17:41
 **/

/**
 * 
 * 模拟用户的并发请求，检测用户乐观锁的性能问题
 * 
 * @author fxb
 * 
 * @date 2018/3/29 18:55
 */
public class ConcurrentTest {

    final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private static RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {

        long starttime = System.currentTimeMillis();
        // 模拟10000人并发请求，用户钱包
        CountDownLatch latch = new CountDownLatch(1);
        // 模拟10000个用户
        for (int i = 0; i < 2010; i++) {
            AnalogUser analogUser = new AnalogUser("user" + i, "58899dcd-46b0-4b16-82df-bdfd0d953bfb" + i, "1",
                    "20.024", latch);
            analogUser.start();
        }
        // 计数器減一 所有线程释放 并发访问。
        latch.countDown();
        System.out.println("所有模拟请求结束 at " + SDF.format(new Date()));
        long end = System.currentTimeMillis();
        System.out.println(SDF.format(end - starttime));

    }

    static class AnalogUser extends Thread { // 模拟用户姓名

        String workerName;
        String openId;
        String openType;
        String amount;
        CountDownLatch latch;

        public AnalogUser(String workerName, String openId, String openType, String amount, CountDownLatch latch) {
            super();
            this.workerName = workerName;
            this.openId = openId;
            this.openType = openType;
            this.amount = amount;
            this.latch = latch;
        }

        @Override
        public void run() {

            // TODO Auto-generated method stub
            try {
                latch.await(); // 一直阻塞当前线程，直到计时器的值为0
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            post();// 发送post 请求

        }

        public void post() {

            ResponseEntity<String> result = null;
            System.out.println("模拟用户： " + workerName + " 开始发送模拟请求  at " + SDF.format(new Date()));
            try {
                result = restTemplate.getForEntity("http://127.0.0.1:8080/blueAndRed/available", String.class);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // sendPost("http://localhost:8080/Settlement/wallet/walleroptimisticlock.action",
            // "openId="+openId+"&openType="+openType+"&amount="+amount);
            System.out.println("操作结果：" + (StringUtils.isEmpty(result) ? "" : result.toString()));
            System.out.println("模拟用户： " + workerName + " 模拟请求结束  at " + SDF.format(new Date()));

        }
    }

}
