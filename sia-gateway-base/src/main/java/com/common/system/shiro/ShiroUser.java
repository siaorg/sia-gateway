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


package com.common.system.shiro;



import com.common.system.entity.RcPrivilege;
import com.common.system.entity.RcRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: huangqian
 * @Date: 2018/7/18 15:48
 * @Description:
 */
public class ShiroUser implements Serializable {

    private static final long serialVersionUID = -4661753370573516137L;

    /**
     *  主键ID
     */
    private Integer id;   
    /**
     *  账号
     */
    private String username;   
    /**
     *  姓名
     */
    private String name;   
    /**
     *  部门id
     */
    private Integer deptId;   
    /**
     *  部门名称
     */
    private String deptName;        
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 角色集
     */
    private List<RcRole> roleList;

    List<RcPrivilege> privilegeList;
    /**
     * 菜单权限值
     */
    List<String> permissionValues = new ArrayList<String>();
    /**
     * 角色值
     */
    List<String> roleValues = new ArrayList<String>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public List<RcRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<RcRole> roleList) {
        this.roleList = roleList;
    }

    public List<RcPrivilege> getPrivilegeList() {
        return privilegeList;
    }

    public void setPrivilegeList(List<RcPrivilege> privilegeList) {
        this.privilegeList = privilegeList;
    }

    public List<String> getPermissionValues() {
        return permissionValues;
    }

    public void setPermissionValues(List<String> permissionValues) {
        this.permissionValues = permissionValues;
    }

    public List<String> getRoleValues() {
        return roleValues;
    }

    public void setRoleValues(List<String> roleValues) {
        this.roleValues = roleValues;
    }
}
