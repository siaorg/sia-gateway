# SIA GATEWAY| [English](README-en.md) [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) 
SIA GATEWAY(SAG) 是基于SpringCloud微服务生态体系下开发的一个分布式API网关系统。具备简单易用、可视化、高可扩展、高可用性等特征，提供云原生、完整及成熟的边缘服务解决方案。


## 关键特性

* 简单易用, 支持基于Docker容器的快速部署及交付。
* 兼容性良好, 兼容SpringBoot微服务及传统HTTP-URL的负载均衡及路由服务。
* 高可扩展性, 支持基于Java语言的第三方插件扩展特性及动态加载机制。
* 支持多租户，多用户角色下的网关拆分管理。
* 可视化管理，提供实时路由拓扑、网关集群拓扑展示功能。
* 服务治理，支持网关集群Dashboard、实时日志、历史日志查询、熔断管理、预警管理等功能。
* 多注册中心支持，提供分布式网关集群下对多注册中心集群的切换管理功能。
* 动态路由组件绑定机制，提供包括URL统计、日志、灰度发布、限流、安全等公共服务组件。

## 架构
SAG 架构是由 **CORE** 和 **Admin Cluster**组成，其中：
* CORE承载网关HTTP请求的主要服务节点，CORE节点可以根据所属的网关组信息自动注册到Admin管理端。
* Admin是网关集群的管理后台，由Admin、Service、Stream、Monitor等服务组成。

下图展示了CORE, Admin管理集群以及微服务的关系：

![SIA GATEWAY 架构](docs/static_files/ark.png)

在此架构基础上我们除了实现了完整的API网关服务外，还实现了基于ElasticSearch、Kafka、Kibana的日志抓取归集、消息中间件服务、数据存储的简称（EKK）的实时数据收集、过滤、存储的服务框架。
详情可浏览[EKK使用指南](docs/user_ekk_zh.md).

## 使用指南
如何使用SIA GateWay可浏览[使用指南](docs/user_guide.md)。

## 构建指南

* 网关CORE节点构建命令:

  $ cd sia-gateway-buildcomponent && mvn clean install -Dmaven.test.skip=true
  
* 网关Admin Cluster构建命令:

  $ cd sia-gateway-admin-buildcomponent && mvn clean install -Dmaven.test.skip=true
  
## 部署指南
* 如何构建网关可浏览[部署指南](docs/deploy_guide.md)。

## 开发实例
* SAG第三方组件开发指南[示例](docs/third_guide.md)。
* SAG安全认证客户端开发指南[示例](docs/safe_guide.md).
* SAG蓝绿部署、金丝雀部署中微服务配置文件指南[示例](docs/ms_guide.md)
* EKK构建方法[示例](docs/user_ekk_zh.md).

## 相关文章：
+ [SIA-GateWay介绍](docs/art.md)
+ [API网关如何实现对服务下线的实时感知](https://mp.weixin.qq.com/s/f1LHALI3avVfnHyHTj-vuA)


## 联系我们

* 邮件交流：sia.list@creditease.cn

* 提交issue:

* 微信交流：

    <img src="https://github.com/lijun006788/sia-task/blob/master/docs/images/siaopenWechatIMG3.jpeg" width="30%" height="30%">