# SIA GATEWAY| [中文](README.md) [![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) 


SIA-GateWay is project provide an API Gateway built on top of the SpringCloud Ecosystem with the features of Simple,Easy Maintenance, Visualized,High Scalable,High availability.


## Key Features

* The Project is Compatible with Netflix-zuul all the features.
* Easy to Maintenance, Support to deploy and deliver based on Docker or Kubernetes .
* Dynamic Load Balancing, Both Support SpringBoot Discovery and Multi HTTP-URL Load Balance。
* Support Java based plugin with dynamic install and uninstall mechanism.
* Multi Tenant support, management gateway group by different role and Authorization.
* Visualized and self-service maintenanc, provide real time route topology and gateway topology Display.
* Provide Dashboard、log、monitoring、metrics、fallback management and alarm function.
* Multi register support , provide dynamic register Switch function.
* Plugin dynamic bind with route mechanism while providing load balancing, logging, authentication, rate-limiting, and more Customize plugins.

## Architecture
SIA-GateWay framework is composed by **CORE** and **Admin Cluster**：
* CORE Cluster plays as the Realtime proxy Node , it is responsible for HTTP traffic processing and deploy distributed.
* Admin Cluster plays as coordinator on Management side. it composed by Admin、Service、Stream、Monitor and deploy compacted。

Below chart display CORE, Admin and MicroService relationShip:
![ServiceComb Pack 架构](docs/static_files/ark.png)

Based on SIA-GateWay  Architecture, we have implements an EKK(ElasticSearch、Kafka、Kibana) Framework which provide message streaming processing middleware.
[EKK Use Guide](docs/user_ekk_zh.md).

## User Guide
How to use SIA-GateWay  [User Guide](docs/user_guide.md)。

## Build Guide

* Build the source code of core module:

  $ cd sia-gateway-buildcomponent && mvn clean install -Dmaven.test.skip=true
  
* Build the source code of admin cluster module:

  $ cd sia-gateway-admin-buildcomponent && mvn clean install -Dmaven.test.skip=true
  
## Deploy Guide
How it build and startup[ Deploy Guide](docs/deploy_guide.md)。

## Demo 
* SIA-GateWay Third party plugin [demo](docs/third_guide.md)。
* SIA-GateWay Auth-Client [demo](docs/safe_guide.md).
* SIA-GateWay Micro Service configuration guide[demo](docs/ms_guide.md)
* EKK startup guide[demo](docs/user_ekk_zh.md).

## Contact Us

* email：sia.list@creditease.cn

* issue:

* wechat：

    <img src="https://github.com/lijun006788/sia-task/blob/master/docs/images/siaopenWechatIMG3.jpeg" width="30%" height="30%">