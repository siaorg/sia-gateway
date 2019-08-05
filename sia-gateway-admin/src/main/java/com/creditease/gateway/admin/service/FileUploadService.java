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

import java.io.File;
import java.util.List;

import com.creditease.gateway.constant.GatewayConstant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.creditease.gateway.admin.service.base.BaseAdminService;

/**
 * 文件上传管理
 * 
 * @author peihua
 * 
 * 
 */

@Service
public class FileUploadService extends BaseAdminService {

	private static final String POSTFIX = ".jar";

	public boolean processUpload(MultipartFile file, String currentRoleName) {
		if (file.isEmpty()) {
			LOGGER.error("FILE IS EMPTY!");
			return false;
		}
		String zuulGroupName = currentRoleName.toUpperCase() + GatewayConstant.ZUUL_POSTFIX;
		try {

			String pluginPath = getPluginPath(zuulGroupName);
			if (pluginPath == null) {
				LOGGER.error("pluginPath IS NULL");
				return false;
			}

			File directory = new File(pluginPath);
			if (!directory.exists()) {
				directory.mkdir();
			}

			String filename = file.getOriginalFilename();
			String uploadFile = pluginPath + File.separator + filename;

			if (!uploadFile.endsWith(POSTFIX)) {
				LOGGER.error("FILE Format is Wrong");
				return false;
			}

			File oldFile = new File(uploadFile);

			if (oldFile.exists()) {
				oldFile.delete();
			}

			// upload file
			file.transferTo(new File(uploadFile));
			LOGGER.info("start uploadFile->{}", uploadFile);
			boolean rst = transfertoZUUL(uploadFile, zuulGroupName);

			return rst;
		} catch (Exception e) {
			LOGGER.error("Exception:{}", e.getCause());
		}
		return false;
	}

	public boolean transfertoZUUL(String filePath, String groupName) throws Exception {

		try {
			List<String> zuulList = zuulDiscovery.getServiceList(groupName);

			if (zuulList.size() == 0) {
				LOGGER.error("transfertoZUUL not find groupName:{}", groupName);
				return false;
			}

			for (String path : zuulList) {

				String url = "http://" + path + "/upload";
				String result = handler.executeUploadCmd(url, filePath);

				LOGGER.info("transfertoZUUL rst:{}", result);
				if (result.toLowerCase().contains("fail")) {
					LOGGER.info("transfertoZUUL 失败 ！！");
					return false;
				}
			}
			return true;

		} catch (Exception e) {
			LOGGER.error("Exception:{}", e.getCause());
		}
		return false;
	}
}
