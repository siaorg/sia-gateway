/*
Navicat MySQL Data Transfer

Source Server         : 10.143.128.215(test)
Source Server Version : 50635
Source Host           : 10.143.128.215:3306
Source Database       : zoner

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2018-10-19 17:06:51
*/
create database IF not exists `zoner`;

use zoner;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `gateway_admin`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_admin`;
CREATE TABLE `gateway_admin` (
  `adminInstanceId` varchar(50) NOT NULL DEFAULT '',
  `adminHotsName` varchar(255) DEFAULT NULL,
  `zuulLastStartTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最後一次重啓時間',
  `adminStatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`adminInstanceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_alarm`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_alarm`;
CREATE TABLE `gateway_alarm` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'alarmID',
  `zuulGroupName` varchar(50)  null comment '网关组',
  `zuulInstance` varchar(255) NOT NULL,
  `alarmCreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Alarm時間',
  `alarmInfomation` varchar(3000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_component`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_component`;
CREATE TABLE `gateway_component` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'compId',
  `compName` varchar(50) NOT NULL,
  `compFilterName` varchar(50) NOT NULL,
  `compType` varchar(50) DEFAULT NULL,
  `compOrder` bigint(20) NOT NULL,
  `compUpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改時間',
  `status` varchar(20) DEFAULT NULL COMMENT '组件状态',
  `routeidList` varchar(256) DEFAULT NULL,
  `compdesc` varchar(3000) DEFAULT NULL,
  `zuulGroupName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`,`compFilterName`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `gateway_counter`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_counter`;
CREATE TABLE `gateway_counter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'alarmID',
  `zuulGroupName` varchar(50)  null comment '网关组',
  `zuulInstance` varchar(255) NOT NULL,
  `counterKey` varchar(50) NOT NULL,
  `counterValue` bigint(20) NOT NULL,
  `datetime` varchar(50) NOT NULL COMMENT 'Counter時間',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=729 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_info`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_info`;
CREATE TABLE `gateway_info` (
  `zuulInstanceId` varchar(50) NOT NULL,
  `zuulHotsName` varchar(255) NOT NULL,
  `zuulGroupName` varchar(50) DEFAULT NULL,
  `zuulDesc` varchar(255) DEFAULT NULL,
  `zuulRouteEnable` tinyint(1) NOT NULL,
  `zuulLastStartTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最後一次重啓時間',
  `zuulStatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`zuulInstanceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_oauth_access_token`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_oauth_access_token`;
CREATE TABLE `gateway_oauth_access_token` (
  `routeid` varchar(50) NOT NULL,
  `client_secret` varchar(255) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `startTime` varchar(50) DEFAULT NULL COMMENT '修改時間',
  `endTime` varchar(50) DEFAULT NULL,
  `tokenUpdateTime` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`routeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `gateway_ribbon_map`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_ribbon_map`;
CREATE TABLE `gateway_ribbon_map` (
  `serviceid` varchar(50) NOT NULL,
  `routeid` varchar(50) DEFAULT NULL,
  `currentVersion` varchar(20) NOT NULL,
  `allVersions` varchar(50) NOT NULL,
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改時間',
  `strategy` varchar(20) DEFAULT NULL COMMENT '策略',
  `context` varchar(100) DEFAULT NULL COMMENT '上下文信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `gateway_route`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_route`;
CREATE TABLE `gateway_route` (
  `id` varchar(50) NOT NULL COMMENT '网关路由名称',
  `path` varchar(255) NOT NULL,
  `serviceid` varchar(50) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `retryable` tinyint(1) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `stripPrefix` varchar(25) DEFAULT NULL,
  `apiName` varchar(255) DEFAULT NULL,
  `zuulGroupName` varchar(50) DEFAULT NULL COMMENT '网关路由属于哪个ZUULGroup',
  `routeStatus` varchar(20) DEFAULT NULL COMMENT '网关路由狀態',
  `strategy` varchar(20) DEFAULT NULL COMMENT '网关路由策略',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_route_counter`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_route_counter`;
CREATE TABLE `gateway_route_counter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'alarmID',
  `url` varchar(255) NOT NULL,
  `routeid` varchar(50) NOT NULL,
  `groupName` varchar(50) NOT NULL,
  `sumCount` bigint(20) DEFAULT NULL,
  `failedCount` bigint(20) DEFAULT NULL,
  `sumSpan` bigint(20) DEFAULT NULL,
  `maxSpan` int DEFAULT NULL,
  `minSpan` int DEFAULT NULL,
  `avgSpan` int DEFAULT NULL,
  `lastInvokeTime` timestamp NOT NULL COMMENT '最近发生时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1508 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_route_ratelimit`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_route_ratelimit`;
CREATE TABLE `gateway_route_ratelimit` (
  `routeid` varchar(50) NOT NULL,
  `ratelimit` varchar(250) DEFAULT NULL COMMENT 'limit',
  PRIMARY KEY (`routeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `gateway_setting`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_setting`;
CREATE TABLE `gateway_setting` (
  `zuulGroupName` varchar(50) NOT NULL,
  `alarmEmailAddr` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`zuulGroupName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `gateway_setting`
-- ----------------------------
DROP TABLE IF EXISTS `gateway_fallback`;
CREATE TABLE `gateway_fallback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'fallbackId',
  `zuulGroupName` varchar(50) NOT NULL,
  `zuulInstance` varchar(255) NOT NULL,
  `fallbackType` varchar(50) NOT NULL,
  `fallbackMsg` varchar(500) NOT NULL,
  `stackTrace` varchar(8000) NOT NULL,
  `startTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '发生時間',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- admin审计表
DROP TABLE IF EXISTS `gateway_audit`;
CREATE TABLE `gateway_audit` (
  `id` int(11) NOT NULL auto_increment COMMENT '操作日志id',
  `username` varchar(20) default NULL COMMENT '操作人',
  `method` varchar(20) default NULL COMMENT '请求方式',
  `zuulGroupName` varchar(50) default NULL COMMENT '网关组',
  `url` varchar(50) default NULL COMMENT '请求路径',
  `ip` varchar(50) default NULL COMMENT 'IP地址',
  `start_time` datetime default NULL COMMENT '操作时间',
  `time_loss` int default NULL COMMENT '执行controller时间损耗,单位为毫秒',
  `status` tinyint(2) default NULL COMMENT '操作描述（1:执行成功、2:执行失败）',
  `params` text default null comment '请求参数',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 增加eureka urls表
DROP TABLE IF EXISTS `gateway_eureka`;
create table gateway_eureka
(
  zuulGroupName varchar(50)  not null
    primary key,
  eurekaUrls    varchar(255) not null,
  enable        tinyint(1)   not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (1,'请求日志组件','LogRequestFilter','PRE',10,'2018-10-16 16:32:23','log','','公共组件','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (2,'响应日志组件','LogResponsetFilter','POST',0,'2018-10-16 16:32:23','log','','公共组件','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (3,'统计组件','StatisticFilter','PRE',6,'2018-11-01 11:09:27','ok','','monitor','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (4,'限流组件','RateLimitFilter','PRE',100,'2018-10-19 11:27:33','ok','','limit','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (5,'蓝绿部署组件','RibbonBRRouteFilter','PRE',11,'2018-11-01 10:53:46','deploy','','公共组件','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (6,'安全认证组件','AuthAuthenticateFilter','PRE',200,'2018-11-01 11:04:36','security','','公共组件','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (7,'金丝雀组件','RibbonCanaryRouteFilter','PRE',200,'2018-11-01 11:04:36','deploy','','公共组件','ALL');
INSERT INTO `gateway_component` (`id`,`compName`,`compFilterName`,`compType`,`compOrder`,`compUpdateTime`,`status`,`routeidList`,`compdesc`,`zuulGroupName`) VALUES (8,'黑白名单组件','AuthWBListFilter','PRE',100,'2018-11-01 11:04:36','security','','公共组件','ALL');

ALTER TABLE gateway_counter ADD INDEX instanceIndex (`zuulInstance`);
ALTER TABLE gateway_counter ADD INDEX groupNameIndex (`zuulGroupName`);

alter table gateway_component  modify column routeidList text;

