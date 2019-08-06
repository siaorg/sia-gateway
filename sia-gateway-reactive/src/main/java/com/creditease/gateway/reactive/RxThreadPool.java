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


package com.creditease.gateway.reactive;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.functions.Func0;

/**
 * 反应POOL
 * 
 * @author peihua
 * 
 */

public interface RxThreadPool {

    public ExecutorService getExecutor();

    static class Factory {

        final static ConcurrentHashMap<String, RxThreadPool> threadPools = new ConcurrentHashMap<String, RxThreadPool>();

        static RxThreadPool getInstance(String key) {

            RxThreadPool previouslyCached = threadPools.get(key);
            if (previouslyCached != null) {
                return previouslyCached;
            }

            synchronized (RxThreadPool.class) {
                if (!threadPools.containsKey(key)) {
                    threadPools.put(key, new RxDefaultThreadPool(key));
                }
            }
            return threadPools.get(key);
        }

        static synchronized void shutdown() {

            for (RxThreadPool pool : threadPools.values()) {
                pool.getExecutor().shutdown();
            }
            threadPools.clear();
        }

        static synchronized void shutdown(long timeout, TimeUnit unit) {

            for (RxThreadPool pool : threadPools.values()) {
                pool.getExecutor().shutdown();
            }
            for (RxThreadPool pool : threadPools.values()) {
                try {
                    while (!pool.getExecutor().awaitTermination(timeout, unit)) {
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(
                            "Interrupted while waiting for thread-pools to terminate. Pools may not be correctly shutdown or cleared.",
                            e);
                }
            }
            threadPools.clear();
        }
    }

    public class RxDefaultThreadPool implements RxThreadPool {

        String key;

        public RxDefaultThreadPool(String key) {
            this.key = key;
        }

        @Override
        public ExecutorService getExecutor() {

            return getExecutor();
        }

    }

    public default Scheduler getScheduler(Func0<Boolean> shouldInterruptThread) {

        return new RxScheduler(null, this, shouldInterruptThread);
    }
}
