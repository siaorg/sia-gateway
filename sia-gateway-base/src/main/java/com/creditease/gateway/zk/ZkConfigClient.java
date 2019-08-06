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


package com.creditease.gateway.zk;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.helper.StringHelper;

/***
 * 
 * NotUsed
 * 
 * @author admin
 *
 */

public class ZkConfigClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZkConfigClient.class);

	private CuratorFramework client = null;

	public ZkConfigClient(String zkaddr) {

		client = CuratorFrameworkFactory.newClient(zkaddr, new RetryNTimes(10, 5000));
		client.start();
		LOGGER.info("success connect to Zookeeper: " + zkaddr);

	}

	/**
	 * get CuratorFramework for some use (e.g. lock)
	 * 
	 * @return
	 */
	public CuratorFramework getCuratorFramework() {

		return client;
	}

	/**
	 * add create authorization, can only create children in give path
	 */
	public void addCreateAuth() {

		try {
			client.getZookeeperClient().getZooKeeper().addAuthInfo(GatewayConstant.DIGEST,
					GatewayConstant.CREATEAUTH.getBytes());
		} catch (Exception e) {
			LOGGER.info("addCreateAuth: ", e);
		}
	}

	/**
	 * all permissions
	 */
	public void addAllAuth() {

		try {
			client.getZookeeperClient().getZooKeeper().addAuthInfo(GatewayConstant.DIGEST,
					GatewayConstant.ALLAUTH.getBytes());
		} catch (Exception e) {
			LOGGER.info("addAllAuth: ", e);
		}
	}

	/**
	 * createPersistentZKNode, creatingParentsIfNeeded for given path,
	 * CreateMode.PERSISTENT
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public boolean createPersistentZKNode(String path, String data) {

		if (StringHelper.isEmpty(path) || isExists(path) || data == null) {
			return false;
		}
		try {

			String zkPath = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,
					data.getBytes());
			LOGGER.info("createPersistentZKNode，创建节点成功，节点地址:" + zkPath);
			return true;
		} catch (Exception e) {
			LOGGER.error("createPersistentZKNode，创建节点失败:" + e.getMessage() + "，path:" + path, e);
		}
		return false;
	}

	/**
	 * createPersistentZKNode, set default value
	 * 
	 * @param path
	 * @return
	 */
	public boolean createPersistentZKNode(String path) {

		return createPersistentZKNode(path, GatewayConstant.ZK_DEFAULT_VALUE);
	}

	/**
	 * createEphemeralZKNode, creatingParentsIfNeeded for given path, leaf node
	 * is CreateMode.EPHEMERAL
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public boolean createEphemeralZKNode(String path, String data) {

		if (StringHelper.isEmpty(path) || isExists(path) || data == null) {
			return false;
		}
		try {

			String zkPath = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,
					data.getBytes());
			LOGGER.info("createPersistentZKNode，创建节点成功，节点地址:" + zkPath);
			return true;
		}

		catch (Exception e) {
			LOGGER.error("createPersistentZKNode，创建节点失败:" + e.getMessage() + "，path:" + path, e);
		}
		return false;
	}

	/**
	 * createEphemeralZKNode, set default value
	 * 
	 * @param path
	 * @return
	 */
	public boolean createEphemeralZKNode(String path) {

		return createEphemeralZKNode(path, GatewayConstant.ZK_DEFAULT_VALUE);
	}

	/**
	 * setData
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public boolean setData(String path, String data) {

		if (!isExists(path) || data == null) {
			return false;
		}
		try {

			Stat stat = client.setData().forPath(path, data.getBytes());
			LOGGER.info("setData，更新数据成功, path:" + path + ", stat: " + stat);
			return true;
		}

		catch (Exception e) {
			LOGGER.error("setData，更新节点数据失败:" + e.getMessage() + "，path:" + path, e);
		}
		return false;
	}

	/**
	 * may return null if path not exists
	 * 
	 * @param path
	 * @return
	 */
	public String getData(String path) {

		String response = null;
		if (!isExists(path)) {
			return response;
		}
		try {
			byte[] datas = client.getData().forPath(path);
			response = new String(datas, "utf-8");
			LOGGER.info("读取数据成功, path:" + path + ", content:" + response);
		}

		catch (Exception e) {
			LOGGER.error("getData，读取数据失败! path: " + path + ", errMsg:" + e.getMessage(), e);
		}
		return response;
	}

	/**
	 * may return null if path not exists
	 * 
	 * @param path
	 * @return
	 */
	public List<String> getChildren(String path) {

		List<String> list = null;
		if (!isExists(path)) {
			return list;
		}
		try {
			list = client.getChildren().forPath(path);
			LOGGER.info("getChildren，读取数据成功, path:" + path + ", content:");
		}

		catch (Exception e) {
			LOGGER.error("getChildren，读取数据失败! path: " + path + ", errMsg:" + e.getMessage(), e);
		}
		return list;
	}

	/**
	 * for given path
	 * 
	 * @param path
	 * @return
	 */
	public boolean isExists(String path) {

		if (StringHelper.isEmpty(path)) {
			return false;
		}
		try {
			Stat stat = client.checkExists().forPath(path);
			return null != stat;
		} catch (Exception e) {
			LOGGER.error("isExists 读取数据失败! path: " + path + ", errMsg:" + e.getMessage(), e);
		}
		return false;
	}

	/**
	 * for given path (node) isPersistent or (EPHEMERAL)
	 * 
	 * @param path
	 * @return
	 */
	public boolean isPersistent(String path) {

		if (StringHelper.isEmpty(path)) {
			return false;
		}
		try {
			Stat stat = client.checkExists().forPath(path);
			if (stat == null) {
				return false;
			}
			// If it is not an ephemeral node, it will be zero.
			return stat.getEphemeralOwner() == 0L;
		} catch (Exception e) {
			LOGGER.error("isPersistent 读取数据失败! path: " + path + ", errMsg:" + e.getMessage(), e);
		}
		return false;
	}

	/**
	 * only delete leaf node for given path
	 * 
	 * @param path
	 * @return
	 */
	public boolean deleteLeafZKNode(String path) {

		if (!isExists(path)) {
			return false;
		}
		try {
			client.delete().forPath(path);
			LOGGER.info("deleteLeafZKNode，删除节点成功，节点地址:" + path);
			return true;
		}

		catch (Exception e) {
			LOGGER.error("deleteLeafZKNode，删除节点失败:" + e.getMessage() + "，path:" + path, e);
		}
		return false;
	}

	/**
	 * deletingChildrenIfNeeded for given path
	 * 
	 * @param path
	 * @return
	 */
	public boolean deletePathZKNode(String path) {

		if (!isExists(path)) {
			return false;
		}
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);
			LOGGER.info("deletePathZKNode，删除节点成功，节点地址:" + path);
			return true;
		}

		catch (Exception e) {
			LOGGER.error("deletePathZKNode，删除节点失败:" + e.getMessage() + "，path:" + path, e);
		}
		return false;
	}

}
