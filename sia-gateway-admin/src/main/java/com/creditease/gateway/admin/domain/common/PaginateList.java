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


package com.creditease.gateway.admin.domain.common;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 分页工具类
 * @author: guohuixie2
 * @create: 2019-06-05 15:19
 **/
public class PaginateList<T> implements Serializable {

    private int pageNo = 1;

    private int pageSize = 10;

    private int total;

    private int totalPage;

    private boolean first;

    private boolean last;

    private boolean next;

    private boolean previous;

    private List<T> dataList;

    public PaginateList(int pageNo, int pageSize, int total) {
        this.pageSize = pageSize < 0 ? 10 : pageSize;
        this.total = total;
        this.totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        if (pageNo > totalPage) {
            this.pageNo = this.totalPage;
        }
        else if (pageNo < 1) {
            this.pageNo = 1;
        }
        else {
            this.pageNo = pageNo;
        }
        this.first = pageNo == 1;
        this.last = pageNo == totalPage;
        this.next = pageNo < totalPage;
        this.previous = pageNo > 1;
    }

    public int getPageNo() {

        return pageNo;
    }

    public void setPageNo(int pageNo) {

        this.pageNo = pageNo;
    }

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize(int pageSize) {

        this.pageSize = pageSize;
    }

    public int getTotal() {

        return total;
    }

    public void setTotal(int total) {

        this.total = total;
    }

    public int getTotalPage() {

        return totalPage;
    }

    public void setTotalPage(int totalPage) {

        this.totalPage = totalPage;
    }

    public boolean isFirst() {

        return first;
    }

    public void setFirst(boolean first) {

        this.first = first;
    }

    public boolean isLast() {

        return last;
    }

    public void setLast(boolean last) {

        this.last = last;
    }

    public boolean isNext() {

        return next;
    }

    public void setNext(boolean next) {

        this.next = next;
    }

    public boolean isPrevious() {

        return previous;
    }

    public void setPrevious(boolean previous) {

        this.previous = previous;
    }

    public List<T> getDataList() {

        return dataList;
    }

    public void setDataList(List<T> dataList) {

        this.dataList = dataList;
    }

    @Override
    public String toString() {

        return "PaginateList{" + "pageNo=" + pageNo + ", pageSize=" + pageSize + ", total=" + total + ", totalPage="
                + totalPage + ", first=" + first + ", last=" + last + ", next=" + next + ", previous=" + previous
                + ", dataList=" + dataList + '}';
    }
}
