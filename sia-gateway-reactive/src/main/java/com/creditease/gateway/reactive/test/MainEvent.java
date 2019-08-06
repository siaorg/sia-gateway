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


package com.creditease.gateway.reactive.test;

import com.creditease.gateway.reactive.impl.BaseEventInvoke;

/**
 * rxJava测试类
 * 
 * @author peihua
 * 
 */

public class MainEvent extends BaseEventInvoke<MainEvent> {

    private static int test = 0;

    @Override
    public void run(BaseEventInvoke<MainEvent> event) {

        // TODO Auto-generated method stub

        try {
            Thread.sleep(2000);
            System.out.println(">run......");
            test = 1;
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        MainEvent te = new MainEvent();

        System.out.println(">start");

        try {
            // te.queue();
            te.execute();
            // te.emit();
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        System.out.println(">end");

        while (true) {
            if (test != 1) {
                try {
                    Thread.sleep(50);
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else {
                break;
            }
        }
        System.out.println(">return");

    }

    @Override
    public EventType getEventType() {

        return EventType.CPU;
    }
}
