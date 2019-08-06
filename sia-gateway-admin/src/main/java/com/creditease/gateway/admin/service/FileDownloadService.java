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
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.creditease.gateway.admin.service.base.BaseAdminService;
import com.creditease.gateway.helper.IoHelper;

/**
 * 
 * 文件下载管理
 * 
 * @author peihua
 * 
 * 
 */
@Service
public class FileDownloadService extends BaseAdminService {

	public String getDownloadFilePath(String groupname, String filename) {

		// 下载文件路径
		String fileUrl = getPluginPath(groupname) + File.separator + filename;
		return fileUrl;
	}

	public List<String> getUploadFileList(String groupName) {

		List<String> lst = new ArrayList<String>();
		String filePath = getPluginPath(groupName);
		File f = new File(filePath);

		if (!f.exists()) {
			LOGGER.info("thirdparty plugin is emptry!!!! groupName:{} ",groupName);
			return lst;
		}else
		{
			 File[] files = f.listFiles();
			 for(int i=0; i<files.length; i++)
			 {
				 File fs = files[i];
				 lst.add(fs.getName());
			 }
		}
		return lst;
	}
	
	public boolean removeFile(String groupName, String jarName)
	{
		try {
			LOGGER.info("removeFile groupName:{},jarName:{} ",groupName,jarName);
			String filePath = getPluginPath(groupName);		
			String path = filePath+File.separator+jarName;
			
			int rst = IoHelper.deleteFile(path);	
			LOGGER.info("removeFile rst: {}", rst);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
