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


package com.creditease.gateway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creditease.gateway.admin.filter.AuthInterceptor;
import com.creditease.gateway.message.Message;

/**
 * @description: 网关开源-登录 注销类
 * @author: guohuixie2
 * @create: 2019-06-25 16:37
 **/

@Controller
public class AdminLoginController {

    @Autowired
    AuthInterceptor authservice;

    /**
     * 登录
     * 
     * @param username
     * @param roleName
     * @return
     */
    @GetMapping("/loginSystem")
    @ResponseBody
    public Message loginSystem(@RequestParam String username, @RequestParam String roleName) {

        return authservice.loginSystem(username, roleName);
    }

    /**
     * 注销
     * 
     * @return
     */
    @GetMapping("/logoutSystem")
    public String logoutSystem() {

        return  authservice.logOutSystem();
    }
}
