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

import java.util.concurrent.Future;


import rx.Observable;

/**
 * 反应接口
 * 
 * @author peihua
 * 
 * */

public interface ReactiveExecutable<R> {

    /**
     * Used for synchronous execution of command.
     * 
     * @return R
     * @throws RuntimeException
     *     
     **/
    public R execute()throws Exception;
    
    /**
     * Used for asynchronous execution of command.
     * 
     * @return R
     * @throws RuntimeException
     *     
     **/
    public Future<R> queue() throws Exception;

    /**
     * Used for asynchronous execution of command.
     * 
     * @return R
     * @throws RuntimeException
     *     
     **/
    public Observable<R> emit() throws Exception;
}
