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


package com.creditease.gateway.domain.topo;

import java.util.Objects;

import com.creditease.gateway.constant.GatewayConstant.LinkState;

/**
 * 拓扑LINK对象
 * 
 * @author peihua
 * 
 */
public class Link {

    private String dest;

    private String source;

    private LinkState state;

    // 最近一次请求时间，判断当前LINK状态
    private long lastRequestTime;

    public Link(String source, String dest) {
        this.source = source;
        this.dest = dest;
    }

    public String getSource() {

        return source;
    }

    public void setSource(String source) {

        this.source = source;
    }

    public String getDest() {

        return dest;
    }

    public void setDest(String dest) {

        this.dest = dest;
    }

    public LinkState getState() {

        return state;
    }

    public void setState(LinkState state) {

        this.state = state;
    }

    public long getLastRequestTime() {

        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {

        this.lastRequestTime = lastRequestTime;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o != null && o instanceof Link) {
            Link link = (Link) o;
            if (Objects.equals(source, link.source) && Objects.equals(dest, link.dest)) {
                return true;
            }
        }
        else {
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(source, dest);
    }
}
