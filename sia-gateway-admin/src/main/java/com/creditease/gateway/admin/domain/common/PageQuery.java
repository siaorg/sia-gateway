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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @description: 查询条件公共类
 * @author: guohuixie2
 * @create: 2019-06-05 15:19
 **/
@ApiModel
public class PageQuery implements Serializable {

    @ApiModelProperty("当前页码")
    private int pageNo = 1;

    @ApiModelProperty("分页大小")
    private int pageSize = 10;

    @ApiModelProperty(hidden = true)
    private int total;

    @ApiModelProperty(hidden = true)
    private int startRow;

    @ApiModelProperty("排序名称")
    private String sortName;

    @ApiModelProperty("升序 或  降序")
    private String sortType;

    public int getPageNo() {

        if (pageNo < 1) {
            pageNo = 1;
        }
        return pageNo;
    }

    public void setPageNo(int pageNo) {

        if (pageNo < 1) {
            this.pageNo = 1;
        }
        else {
            this.pageNo = pageNo;
        }
    }

    public int getPageSize() {

        if (pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {

        if (pageSize < 0) {
            this.pageSize = 10;
        }
        else {
            this.pageSize = pageSize;
        }
    }

    public int getTotal() {

        return total;
    }

    public void setTotal(int total) {

        this.total = total;
    }

    public int getStartRow() {

        int totalPage = this.total % this.pageSize == 0 ? this.total / this.pageSize : this.total / this.pageSize + 1;
        if (pageNo > totalPage) {
            this.pageNo = totalPage;
        }
        if (this.pageNo <= 1) {
            this.startRow = (this.pageNo - 1) * pageSize;
        }
        else {
            this.startRow = (this.pageNo - 1) * pageSize;
        }
        return this.startRow;
    }

    public void setStartRow(int startRow) {

        this.startRow = startRow;
    }

    public String getSortName() {

        return sortName;
    }

    public void setSortName(String sortName) {

        this.sortName = sortName;
    }

    public String getSortType() {

        return sortType;
    }

    public void setSortType(String sortType) {

        this.sortType = sortType;
    }
}
