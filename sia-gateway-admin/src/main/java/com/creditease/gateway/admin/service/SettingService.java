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

import com.creditease.gateway.admin.service.base.BaseAdminService;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 设置管理
 * 
 * @author peihua
 */

@Service
public class SettingService extends BaseAdminService {

	public boolean saveAlarmSetting(String zuulGroup, String emailAddresss) throws Exception {

		boolean rst = false;

		try {
			rst = adminDBRepository.saveEmailSetting(zuulGroup, emailAddresss);

			if (!rst) {
				LOGGER.error("SaveAlarmSetting error:{}", rst);
			} else {
				LOGGER.info("SaveAlarmSetting success:{}", rst);
			}

		} catch (DuplicateKeyException e) {

			rst = adminDBRepository.updateEmailSetting(zuulGroup, emailAddresss);

			if (!rst) {
				LOGGER.error("updateAlarmSetting error:{}", rst);
			} else {
				LOGGER.info("updateAlarmSetting success:{}" , rst);
			}

		} catch (Exception e) {

			LOGGER.error(e.getMessage());

		}
		return rst;

	}

	public String getAlarmSetting(String zuulGroup) {

		String setting = adminDBRepository.getAlarmEmailSetting(zuulGroup);

		return setting;
	}

}
