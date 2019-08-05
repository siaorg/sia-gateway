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


package com.creditease.gateway.hystrix;

import org.springframework.stereotype.Component;

import com.creditease.gateway.hystrix.strategy.FallCommandStrategy;
import com.creditease.gateway.hystrix.strategy.FallDefaultStrategy;
import com.creditease.gateway.hystrix.strategy.FallbackStrategy;
import com.creditease.gateway.hystrix.strategy.HostConnectStrategy;
import com.creditease.gateway.hystrix.strategy.HystrixDefaultStrategy;
import com.creditease.gateway.hystrix.strategy.RejectedthreadStrategy;
import com.creditease.gateway.hystrix.strategy.RequestBadStrategy;
import com.creditease.gateway.hystrix.strategy.ShortcircuitStrategy;
import com.creditease.gateway.hystrix.strategy.TimeoutStrategy;
import com.netflix.client.ClientException;
import com.netflix.hystrix.exception.HystrixRuntimeException;

/**
 * 熔断策略管理工厂
 * 
 * @author peihua
 * 
 **/
@Component
public class StrategyFactory {

    private FallbackStrategy stg;

    public FallbackStrategy getStrategy(Throwable cause, FallbackEvent event) {

        if (cause instanceof HystrixRuntimeException) {

            HystrixRuntimeException e = (HystrixRuntimeException) cause;

            switch (e.getFailureType()) {

                case BAD_REQUEST_EXCEPTION:
                    stg = new RequestBadStrategy(event);
                    break;
                case COMMAND_EXCEPTION:
                    stg = new FallCommandStrategy(event);
                    break;
                case TIMEOUT:
                    stg = new TimeoutStrategy(event);

                    break;
                case SHORTCIRCUIT:
                    stg = new ShortcircuitStrategy(event);
                    break;

                case REJECTED_THREAD_EXECUTION:
                    stg = new RejectedthreadStrategy(event);
                    break;

                default:
                    stg = new HystrixDefaultStrategy(event);
            }
        }
        else if (cause instanceof ClientException) {
            ClientException e = (ClientException) cause;

            switch (e.getErrorType()) {

                case GENERAL:
                case CONFIGURATION:
                    stg = new RequestBadStrategy(event);
                    break;
                case NUMBEROF_RETRIES_EXEEDED:
                case NUMBEROF_RETRIES_NEXTSERVER_EXCEEDED:
                    stg = new FallCommandStrategy(event);
                    break;
                case SOCKET_TIMEOUT_EXCEPTION:
                case READ_TIMEOUT_EXCEPTION:
                    stg = new TimeoutStrategy(event);
                    break;
                case UNKNOWN_HOST_EXCEPTION:
                case CONNECT_EXCEPTION:
                case NO_ROUTE_TO_HOST_EXCEPTION:
                    stg = new HostConnectStrategy(event);
                    break;

                case CLIENT_THROTTLED:
                case SERVER_THROTTLED:
                case CACHE_MISSING:
                    stg = new FallDefaultStrategy(event);
                    break;
                default:
                    stg = new FallDefaultStrategy(event);
                    break;
            }

        }
        else {
            stg = new FallDefaultStrategy(event);
        }
        return stg;
    }
}
