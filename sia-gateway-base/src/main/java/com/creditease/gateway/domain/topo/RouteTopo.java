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

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.creditease.gateway.domain.FIFOCache;

/**
 * 拓扑管理对象
 * 
 * @author peihua
 * 
 */
public class RouteTopo implements Serializable {

    private static final long serialVersionUID = -4661753370573516137L;

    // 浏览器(host)、应用集合（host）
    private Set<String> clientNodes = new HashSet<String>();;

    // 网关：instanceId
    private Set<String> zuulNodes = new HashSet<String>();

    // 下游服务地址
    private Set<String> upstreamNodes = new HashSet<String>();;

    // Link
    private Set<Link> link = new HashSet<Link>();

    private String routeId;

    private String groupName;

    private int maxDelay = 0;

    private int minDelay = 0;

    // 最近一次请求时间
    private long lastRequestTime;

    private Map<String, String> exceptionCause = new LinkedHashMap<String, String>();

    // FIFO:存储最近100条数据的delay
    private FIFOCache<String, String> recentDelays = new FIFOCache<String, String>(500);

    public synchronized void addLink(Link e) {

        this.link.remove(e);
        this.link.add(e);
    }

    public void updateSpanTime(String path, int delay, long startTime) {

        lastRequestTime = startTime;

        if (delay > maxDelay) {
            maxDelay = delay;

        }
        if ((minDelay == 0) || (delay < minDelay)) {
            minDelay = delay;
        }
        try {

            recentDelays.put(path, Integer.toString(delay));

        }
        catch (Exception e) {

        }
    }

    public Set<String> getClientNodes() {

        return clientNodes;
    }

    public void setClientNodes(Set<String> clientNodes) {

        this.clientNodes = clientNodes;
    }

    public FIFOCache<String, String> getRecentDelays() {

        return recentDelays;
    }

    public void setRecentDelays(FIFOCache<String, String> recentDelays) {

        this.recentDelays = recentDelays;
    }

    public Set<String> getZuulNodes() {

        return zuulNodes;
    }

    public void setZuulNodes(Set<String> zuulNodes) {

        this.zuulNodes = zuulNodes;
    }

    public Set<String> getUpstreamNodes() {

        return upstreamNodes;
    }

    public void setUpstreamNodes(Set<String> upstreamNodes) {

        this.upstreamNodes = upstreamNodes;
    }

    public String getRouteId() {

        return routeId;
    }

    public void setRouteId(String routeId) {

        this.routeId = routeId;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

    public Set<Link> getLink() {

        return link;
    }

    public void setLink(Set<Link> link) {

        this.link = link;
    }

    public int getMaxDelay() {

        return maxDelay;
    }

    public void setMaxDelay(int maxDelay) {

        this.maxDelay = maxDelay;
    }

    public int getMinDelay() {

        return minDelay;
    }

    public void setMinDelay(int minDelay) {

        this.minDelay = minDelay;
    }

    public long getLastRequestTime() {

        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {

        this.lastRequestTime = lastRequestTime;
    }

    public Map<String, String> getExceptionCause() {

        return exceptionCause;
    }

    public void setExceptionCause(Map<String, String> exceptionCause) {

        this.exceptionCause = exceptionCause;
    }

    @Override
    public String toString() {

        StringBuffer b = new StringBuffer();
        b.append("clientNodes:");
        for (String c : clientNodes) {
            b.append(c);
        }

        b.append("zuulNodes:");
        for (String z : zuulNodes) {
            b.append(z);
        }

        b.append("upstreamNodes:");
        for (String u : upstreamNodes) {
            b.append(u);
        }

        b.append("link:");
        for (Link l : link) {
            b.append(l.getSource() + "-" + l.getDest());
        }
        return b.toString();

    }

    public static void main(String args[]) {

        try {
            RouteTopo rt = new RouteTopo();

            Link lk = new Link("1", "2");
            lk.setLastRequestTime(System.currentTimeMillis());
            rt.addLink(lk);
            System.out.println("before lk lasttime:" + lk.getLastRequestTime());

            Thread.sleep(100);

            Link lk2 = new Link("2", "2");
            lk2.setLastRequestTime(System.currentTimeMillis());
            System.out.println("before lk2 lasttime:" + lk2.getLastRequestTime());
            rt.addLink(lk2);
            Thread.sleep(100);

            Link lk3 = new Link("1", "2");
            lk3.setLastRequestTime(System.currentTimeMillis());
            System.out.println("after lk3 lasttime:" + lk3.getLastRequestTime());
            Link lk4 = new Link("2", "2");
            lk4.setLastRequestTime(System.currentTimeMillis());
            System.out.println("after lk4 lasttime:" + lk4.getLastRequestTime());

            rt.addLink(lk3);
            rt.addLink(lk4);

            Set<Link> link = rt.getLink();

            for (Link l : link) {
                System.out.println("link lasttime:" + l.getLastRequestTime());
            }
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
