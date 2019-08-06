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


package com.creditease.gateway.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.creditease.gateway.admin.domain.CompAdapter;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.domain.CompInfo;

/**
 * 组件组合功能
 *
 * @author peihua
 */

@Service
public class CompTransferService extends BaseAdminService {

    private static final String STATUS = "ok";

    /**
     * log
     * 
     * deploy
     */
    public enum CompTypeEnum {

        log("日志"), deploy("灰度"), monitor("监控"), limit("限流"), security("安全"), thirdparty("第三方");

        String cname;

        CompTypeEnum(String cname) {
            this.cname = cname;
        }
    }

    public List<CompAdapter> transfer(List<CompInfo> comp) throws Exception {

        List<CompAdapter> arrayList = new ArrayList<CompAdapter>();
        for (CompTypeEnum flevel : CompTypeEnum.values()) {
            CompAdapter adapter = new CompAdapter(flevel.toString(), flevel.cname, new ArrayList<CompInfo>());
            arrayList.add(adapter);
        }
        for (CompInfo compinfo : comp) {
            String level = compinfo.getStatus();
            // 兼容
            if (STATUS.equals(level)) {
                level = CompTypeEnum.thirdparty.toString();
            }
            for (CompAdapter adpter : arrayList) {
                if (level.equals(adpter.getFirstLevel())) {
                    adpter.getCompList().add(compinfo);
                }
            }
        }
        return arrayList;
    }
}
