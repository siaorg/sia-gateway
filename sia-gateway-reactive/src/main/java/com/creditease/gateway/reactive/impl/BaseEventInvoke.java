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


package com.creditease.gateway.reactive.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.reactive.ReactiveExecutable;
import com.creditease.gateway.reactive.ReactiveObservable;
import com.creditease.gateway.reactive.RxThreadPool;

import rx.Notification;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

/**
 * rxJava 响应抽象类
 * 
 * @author peihua
 * 
 */

public abstract class BaseEventInvoke<R> implements ReactiveExecutable<R>, ReactiveObservable<R> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseEventInvoke.class);

    public static enum EventType {

        CPU, IO, SAMETHREAD, NEW, RXS
    }

    @Override
    public Observable<R> observe() {

        Observable<R> observable = Observable.create(new OnSubscribe<R>() {

            @Override
            public void call(Subscriber<? super R> e) {

                R markEmits = null;

                e.onNext(markEmits);

                e.onCompleted();
            }
        });
        return observable;
    }

    @Override
    public Observable<R> emit() {

        ReplaySubject<R> subject = ReplaySubject.create();
        // eagerly kick off subscription
        final Subscription sourceSubscription = toObservable().subscribe(subject);
        // return the subject that can be subscribed to later while the
        // execution has already started
        return subject.doOnUnsubscribe(new Action0() {

            @Override
            public void call() {

                sourceSubscription.unsubscribe();
            }
        });
    }

    @Override
    public Observable<R> toObservable() {

        Observable<R> execution;

        final BaseEventInvoke<R> event = this;

        execution = executeCommandAsynch(event);

        Func1<? super Throwable, ? extends Observable<? extends R>> handleFallback = null;

        Action1<Notification<? super R>> setRequestContext = null;

        return execution.doOnNext(null).doOnCompleted(null).onErrorResumeNext(handleFallback)

                .doOnEach(setRequestContext);
    }

    private Observable<R> executeCommandAsynch(BaseEventInvoke<R> event) {

        Scheduler scheduler;

        switch (event.getEventType()) {
            case CPU:
                scheduler = Schedulers.computation();
                break;
            case IO:
                scheduler = Schedulers.io();
                break;
            case SAMETHREAD:
                scheduler = Schedulers.immediate();
                break;
            case NEW:
                scheduler = Schedulers.newThread();
                break;
            case RXS:
                scheduler = new RxThreadPool.RxDefaultThreadPool("RXS").getScheduler(new Func0<Boolean>() {

                    @Override
                    public Boolean call() {

                        return true;
                    }
                });
                break;
            default:
                scheduler = Schedulers.computation();
                break;
        }

        return Observable.defer(new Func0<Observable<R>>() {

            @Override
            public Observable<R> call() {

                logger.debug("Func called, thread name:{}", Thread.currentThread().getName());
                run(event);

                return null;

            }

        }).doOnTerminate(new Action0() {

            @Override
            public void call() {

            }

        }).doOnUnsubscribe(new Action0() {

            @Override
            public void call() {

            }

        }).subscribeOn((scheduler));
    }

    /**
     * 抽象方法调用
     * 
     */
    public abstract void run(BaseEventInvoke<R> event);

    public abstract EventType getEventType();

    /**
     * Schedulers类型
     * 
     */

    @Override
    public R execute() throws Exception {

        try {
            return queue().get();
        }
        catch (Exception e) {
        }
        return null;
    }

    @Override
    public Future<R> queue() throws Exception {

        final Future<R> delegate = toObservable().toBlocking().toFuture();

        final Future<R> f = new Future<R>() {

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {

                if (delegate.isCancelled()) {
                    return false;
                }
                return mayInterruptIfRunning;

            }

            @Override
            public boolean isCancelled() {

                return delegate.isCancelled();
            }

            @Override
            public boolean isDone() {

                return delegate.isDone();
            }

            @Override
            public R get() throws InterruptedException, ExecutionException {

                return delegate.get();
            }

            @Override
            public R get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {

                return delegate.get(timeout, unit);
            }

        };

        /* special handling of error states that throw immediately */
        if (f.isDone()) {
            try {
                f.get();
                return f;
            }
            catch (Exception e) {
                throw e;
            }
        }

        return f;
    }
}
