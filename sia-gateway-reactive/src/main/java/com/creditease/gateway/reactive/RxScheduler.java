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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.internal.schedulers.ScheduledAction;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * 反应Scheduler
 * 
 * @author peihua
 * 
 */

public class RxScheduler extends Scheduler {

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100));
    private final Scheduler actualScheduler;
    private final RxThreadPool threadPool;

    public RxScheduler(RxThreadPool threadPool, RxThreadPool rxThreadPool, Func0<Boolean> shouldInterruptThread) {

        this.threadPool = threadPool;
        this.actualScheduler = new ThreadPoolScheduler(threadPool, shouldInterruptThread);
    }

    private static class ThreadPoolScheduler extends Scheduler {

        private final RxThreadPool threadPool;
        private final Func0<Boolean> shouldInterruptThread;

        public ThreadPoolScheduler(RxThreadPool threadPool, Func0<Boolean> shouldInterruptThread) {
            this.threadPool = threadPool;
            this.shouldInterruptThread = shouldInterruptThread;
        }

        @Override
        public Worker createWorker() {

            return new ThreadPoolWorker(shouldInterruptThread);
        }

    }

    @Override
    public Worker createWorker() {

        Func0<Boolean> shouldInterruptThread = null;

        return new ThreadPoolWorker(shouldInterruptThread);
    }

    private static class ThreadPoolWorker extends Worker {

        private final CompositeSubscription subscription = new CompositeSubscription();
        private final Func0<Boolean> shouldInterruptThread;

        public ThreadPoolWorker(Func0<Boolean> shouldInterruptThread) {

            this.shouldInterruptThread = shouldInterruptThread;
        }

        @Override
        public void unsubscribe() {

            subscription.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {

            return subscription.isUnsubscribed();
        }

        @Override
        public Subscription schedule(final Action0 action) {

            if (subscription.isUnsubscribed()) {

                return Subscriptions.unsubscribed();
            }

            ScheduledAction sa = new ScheduledAction(action);

            subscription.add(sa);
            sa.addParent(subscription);

            FutureTask<?> f = (FutureTask<?>) executor.submit(sa);
            sa.add(new FutureCompleterWithConfigurableInterrupt(f, shouldInterruptThread, executor));

            return sa;
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {

            throw new IllegalStateException("scheduling error");
        }
    }

    private static class FutureCompleterWithConfigurableInterrupt implements Subscription {

        private final FutureTask<?> f;
        private final Func0<Boolean> shouldInterruptThread;
        private final ThreadPoolExecutor executor;

        private FutureCompleterWithConfigurableInterrupt(FutureTask<?> f, Func0<Boolean> shouldInterruptThread,
                ThreadPoolExecutor executor) {
            this.f = f;
            this.shouldInterruptThread = shouldInterruptThread;
            this.executor = executor;
        }

        @Override
        public void unsubscribe() {

            executor.remove(f);
            if (shouldInterruptThread.call()) {
                f.cancel(true);
            }
            else {
                f.cancel(false);
            }
        }

        @Override
        public boolean isUnsubscribed() {

            return f.isCancelled();
        }
    }

}
