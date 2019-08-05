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


package com.creditease.gateway.domain;

import java.util.Objects;

/**
 * URI: 请求次数， 失败数， 请求平均响应时间（Desc），请求最大响应（参数），请求最小响应（参数），ServiceID，上一次执行时间
 * 
 * @author peihua
 * 
 */

public class UrlRecordAggregate implements Comparable<UrlRecordAggregate> {

    private String url;

    // 附属信息
    private String routeid;

    private String instanceId;

    private String groupName;

    private long lastInvokeTime;

    // 指标信息
    private int sumCount;

    private int failedCount;

    private long sumSpan;

    private long maxSpan;

    private long minSpan;

    private long avgSpan;

    public UrlRecordAggregate() {

    }

    public UrlRecordAggregate(String urlpath, String ist, String rid, String gn, long time) {
        url = urlpath;
        instanceId = ist;
        routeid = rid;
        groupName = gn;
        lastInvokeTime = time;
    }

    public void init(int sumcount, int failedcount, String urlpath, String rid, String gn, long time, long sumSpan,
            long maxSpan, long minSpan, long avgSpan) {

        url = urlpath;
        routeid = rid;
        groupName = gn;
        lastInvokeTime = time;
        this.sumCount = sumcount;
        this.failedCount = failedcount;
        this.sumSpan = sumSpan;
        this.maxSpan = maxSpan;
        this.minSpan = minSpan;
        this.avgSpan = avgSpan;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrlRecordAggregate urlg = (UrlRecordAggregate) o;
        return Objects.equals(url, urlg.url) && Objects.equals(routeid, urlg.routeid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, routeid);
    }

    public void increSumCount() {

        this.sumCount++;
    }

    public void increFailedCount() {

        this.failedCount++;
    }

    public void spanCompute(long span) {

        sumSpan += span;

        if (span > maxSpan) {
            maxSpan = span;
        }
        if (span < minSpan || minSpan == 0) {
            minSpan = span;
        }

        avgSpan = sumSpan / sumCount;
    }

    public void spanAggreCompute(long sumcount, long failedcount, long sum, long max, long min) {

        sumCount += sumcount;

        failedCount += failedcount;

        sumSpan += sum;

        if (max > maxSpan) {
            maxSpan = max;
        }
        if (min < minSpan || minSpan == 0) {
            minSpan = min;
        }
        if (sumCount > 0) {
            avgSpan = sumSpan / sumCount;
        }

    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    public String getRouteid() {

        return routeid;
    }

    public void setRouteid(String routeid) {

        this.routeid = routeid;
    }

    public String getInstanceId() {

        return instanceId;
    }

    public void setInstanceId(String instanceId) {

        this.instanceId = instanceId;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {

        this.groupName = groupName;
    }

    public long getLastInvokeTime() {

        return lastInvokeTime;
    }

    public void setLastInvokeTime(long lastInvokeTime) {

        this.lastInvokeTime = lastInvokeTime;
    }

    public int getFailedCount() {

        return failedCount;
    }

    public void setFailedCount(int failedCount) {

        this.failedCount = failedCount;
    }

    public long getSumSpan() {

        return sumSpan;
    }

    public void setSumSpan(long sumSpan) {

        this.sumSpan = sumSpan;
    }

    public long getMaxSpan() {

        return maxSpan;
    }

    public void setMaxSpan(long maxSpan) {

        this.maxSpan = maxSpan;
    }

    public long getMinSpan() {

        return minSpan;
    }

    public void setMinSpan(long minSpan) {

        this.minSpan = minSpan;
    }

    public long getAvgSpan() {

        return avgSpan;
    }

    public void setAvgSpan(long avgSpan) {

        this.avgSpan = avgSpan;
    }

    public int getSumCount() {

        return sumCount;
    }

    public void setSumCount(int sumCount) {

        this.sumCount = sumCount;
    }

    @Override
    public int compareTo(UrlRecordAggregate urg) {

        return (int) (urg.getAvgSpan() - this.avgSpan);
    }
}
