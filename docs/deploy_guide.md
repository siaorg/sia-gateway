# API网关安装部署指南

### 1、 环境
+ 编译环境
	+ Maven3+
	+ nodejs
	+ Jdk1.8+
+ 运行时第三方依赖
	+ Mysql5.6+
	+ elasticsearch 5.5.2
	+ kibana-5.5.2
	+ kafka 2.12-2.0.0
	+ redis 3.2.11
	+ eureka-server
+ 运行环境
   + 64bit OS，Linux/Mac/Windows/docker
   + JDK1.8+


### 2、源码下载
```
git clone https://github.com/siaorg/sia-gateway.git
```
+ 源码结构如下：

```
.
├── sia-gateway-admin-buildcomponent  网关admin组件集合
│   ├── sia-gateway-admin             网关admin监控系统组件
│   ├── sia-gateway-synchspeed        网关对下游服务实时感知组件
│   ├── sia-gateway-stream            网关日志组件
│   ├── sia-gateway-service           网关系统辅助组件
│   ├── sia-gateway-monitor           网关监控、日志组件
│   │    ├── sia-gateway-reactive    
│   │    ├── sia-gateway-messaging    基础依赖 
│   │    ├── sia-gateway-sink
│   │    ├── sia-gateway-esclient
│   │    ├── sia-gateway-base
├── sia-gateway-admin-display         网关系统前端代码
├── sia-gateway-buildcomponent        网关core-buildcomponent
│   ├── sia-gateway-core              网关Core节点
│   │   ├── sia-gateway-base
│   │   ├── sia-gateway-messaging     基础依赖
│   │   ├── sia-gateway-reactive
│   │   ├── sia-gateway-template
```



### 3、 初始化“API网关数据库”
1. MySQL的安装和配置详见MySQL官方文档

2. 请下载项目源码并解压，获取 "API网关数据库初始化SQL脚本" 并执行即可。
<br><p>
"API网关数据库初始化SQL脚本" 位置为:

```
/sia-gateway/sia-gateway-admin/src/main/resources/db/gateway_admin.sql
```


### 4、 配置“网关系统”
网关配置文件地址：

```
# 网关admin中心conf
/sia-gateway/sia-gateway-admin-buildcomponent/config/gateway_admin_test.yml

# 网关监控服务conf
/sia-gateway/sia-gateway-admin-buildcomponent/config/gateway_monitor_test.yml

# 网关辅助节点conf
/sia-gateway/sia-gateway-admin-buildcomponent/config/gateway_service_test.yml

# 网关日志服务conf
/sia-gateway/sia-gateway-admin-buildcomponent/config/gateway_stream_test.yml

# 网关实时感知服务conf
/sia-gateway/sia-gateway-admin-buildcomponent/config/gateway_synchspeed_test.yml

# 网关核心节点conf
/sia-gateway/sia-gateway-buildcomponent/config/gateway_test.yml

```

+  gateway\_admin_test.yml

![](/docs/static_files/conf1.png)
![](/docs/static_files/conf2.png)

+  gateway\_service_test.yml

![](/docs/static_files/conf4.png)

+  gateway\_stream_test.yml 
![](/docs/static_files/conf5.png)

+  gateway\_synchspeed_test.yml

![](/docs/static_files/conf3.png)

+  gateway\_monitor_test.yml

![](/docs/static_files/conf6.png)

+  gateway\_test.yml 

![](/docs/static_files/conf15.png)
![](/docs/static_files/conf16.png)
![](/docs/static_files/conf17.png)

说明：spring.application.name为网关组名称，开发者可以修改此属性，搭建新的网关组



+ maven 仓库地址配置

```xml
  <mirrors>
	   <mirror>
		      <id>alimaven</id>
		      <name>aliyun maven</name>
		      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
		      <mirrorOf>central</mirrorOf>        
	  </mirror>
  </mirrors>
  <profiles>
		<profile>
			<id>jdk-1.8</id>
			<activation>
				<activeByDefault>true</activeByDefault>
				<jdk>1.8</jdk>
			</activation>
			<properties>
				<maven.compiler.source>1.8</maven.compiler.source>
				<maven.compiler.target>1.8</maven.compiler.target>
				<maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
			</properties>
		</profile>

		<profile>
			<id>downloadSources</id>
			<properties>
				<downloadSources>true</downloadSources>
				<downloadJavadocs>true</downloadJavadocs>
			</properties>
		</profile>
		<profile>  
		    <id>spring plugins</id>  
		    <activation>  
		      <jdk>spring plugins</jdk>  
		    </activation>  
		    <pluginRepositories>  
		      <pluginRepository>  
		        <id>spring plugins</id>  
		        <name>Spring plugins</name>  
		        <url>https://maven.aliyun.com/repository/spring-plugin</url>  
		        <layout>default</layout>  
		        <snapshotPolicy>always</snapshotPolicy>  
		      </pluginRepository>  
		    </pluginRepositories>  
		</profile>  
	</profiles>

	<activeProfiles>
		<activeProfile>downloadSources</activeProfile>
	</activeProfiles>
  
```

### 5 构建部署项目
#### 5.1 编译项目
+ 如果已经正确进行上述配置，可将项目编译打包部署。
+ 操作步骤：

```sh
	cd sia-gateway
	
	chmod +x *.sh
	
	sh build.sh
```

+ 打包成功后，会出现以下标注文件。
  + /sia-gateway/sia-gateway-admin-buildcomponent/target/gateway_admin_1.0.zip
  
![](/docs/static_files/conf7.png)
  
 
 + /sia-gateway/sia-gateway-buildcomponent/target/gateway_1.0.zip
 
![](/docs/static_files/conf8.png)
  
  
 + /sia-gateway/sia-gateway-admin-display/dist/
  
![](/docs/static_files/conf9.png)

注：前端打包需要用到nodeJs,如未安装，可请参考 [nodejs安装文档](https://nodejs.org/zh-cn/download/)。
#### 5.2 部署项目
+ API网关系统的部署方式为分布式部署集中式管理模式，即网关Core节点可以按业务线划分为不同的网关组，网关管理端作为网关的管理中心，供统一的管理界面，用户可在此进行 API、组件、系统基础信息的设置和维护，收集监控日志、生成各种运维管理报表、自动告警等。
	+ sia-gateway-admin-buildcomponent是网关管理端组件集，包括：admin、stream、service、synchspeed、monitor；管理端部署单个节点即可（目前不支持集群）。
	+ sia-gateway-buildcomponent为网关Core组件，可以按业务线分组，组内以单节点或集群方式部署。




	
#### Vmware方式部署

+ 后端部署

```sh
unzip gateway_admin_1.0.zip

cd /gatewayadmin/bin

chmod +x *.sh 

#启动网关管理服务，包括：admin、stream、service、synchspeed、monitor。
sh onekey_start.sh

---------------------------------------------------
unzip gateway_1.0.zip

cd /gateway/bin

chmod +x *.sh 

#启动网关Core服务
sh start_gateway_test.sh
```

+ 前端部署

	+  修改前端site-map.js
	
![](/docs/static_files/conf11.png)
	
	+ nginx的代理配置，进入nginx的目录下nginx.conf，添加如下配置：
	
	```conf
	    upstream apigateway.open.location1 { 
		   #### sia-gateway-admin服务IP
	      server *******:8090 ; 
	    }
	    server {
	    	 # nginx 监听端口
	        listen       18086;
	        server_name  localhost;
	        access_log  logs/host.access.log  main;
	        #access_log  "pipe:rollback logs/host.access_log interval=1d baknum=7 maxsize=2G"  main;
	        location / {
	            #root   html;
	            #index  index.html index.htm;
	            root  /app/jar/ROOT/dist;
	            index  index.html index.htm;
	        }
	        # 后端服务location 
	        location ^~ /vv1/  {
	                proxy_pass http://apigateway.open.location1/;  
	                proxy_set_header   Host  $host; 
	                proxy_set_header   X-Real-IP $http_x_forwarded_for; 
	                proxy_set_header   X-Forwarded-For  $proxy_add_x_forwarded_for; 
	                proxy_http_version 1.1;
	        }
	        error_page   500 502 503 504  /50xcn.html;
	        location = /50xcn.html {
	            root   html/error_page;
	            index 50xcn.html;
	        }
	        location /check_status {
	            vhost_traffic_status_display;
	            vhost_traffic_status_display_format json;
	        }
	    }
	```
	+ 重启nginx

	```sh
	#校验配置是否正确
	./nginx -t
	
	# 重新启动
	./nginx -s reload
	
	```
	
#### Docker 镜像方式部署

+ 说明：如果docker环境和编译环境是在同一个操作系统上，可直接执行以下步骤；反之，需要先将以下文件按原目录结构上传docker环境所在服务器。
     + /sia-gateway/sia-gateway-admin-buildcomponent/target/gateway_admin_1.0.zip
     + /sia-gateway/sia-gateway-buildcomponent/target/gateway_1.0.zip
     + /sia-gateway/sia-gateway-admin-display/dist/
     + /sia-gateway/third-libary
     + /sia-gateway/build.sh
     + /sia-gateway/docker-start.sh
     + /sia-gateway/docker-run.sh
     + /sia-gateway/Dockerfile
+ 步骤：

	1. 配置
		+ 修改site-map.js，位置：/dist/static/site-map.js
		
```js
	    /**
         * vmware部署： 127.0.0.1 ----> nginx的ip地址
         * docker镜像部署： 127.0.0.1 ----> docker容器的宿主机ip
         */
        'CESHI_API_HOST': '127.0.0.1:18086/vv1',
        
        /**
         * 127.0.0.1 ----> kibana的ip地址
         */
        'CESHI_API_HOST_LOG': '127.0.0.1:5601'
```
		
	3. 下载centos基础镜像，如果已经下载，此步忽略。
	4. 修改Dockerfile,FROM 镜像名:版本号
	
![](/docs/static_files/conf12.png)

	5. 配置yum源，如果部署机器能够使用阿里yum源，此步忽略。
		+ 将yum源文件名称修改成 CentOS-Base.repo，并替换到/sia-gateway/third-libary/下
	6. 构建镜像，并启动容器和服务
	
```sh
	# 构建镜像
	cd /sia-gateway/ 
	
	# 授权
	chmod +x *.sh
	
	# 构建镜像
	sh docker-build.sh
	
	# 启动容器和服务
	sh docker-run.sh
	
	# 查看容器是否启动成功
	docker ps 
	
	# 进入容器查看服务运行情况
	docker exec -it gateway-test:v1 bash

```
	
访问地址：http://宿主机IP:18086/ ，具体使用可浏览[使用指南](docs/user_guide.md)。
	
![](/docs/static_files/conf13.png)
	
![](/docs/static_files/conf14.png)
	

	
	
**说明：**

+ Docker镜像部署方式是我们为方便开发者简单、快速地基于docker环境搭建网关系统而提供的一种ALL-IN-ONE形式的网关部署Demo，即网关监控服务、预警、网关核心节点等都构建在一个docker镜像中；开发者可以根据开发环境条件来灵活选择部署方案，推荐使用vmware+docker镜像部署方式，即将网关管理端服务部署在vmware上，网关核心节点部署在docker环境中。
+ Docker容器启动的时候，如果想要挂载宿主机的目录作为网关系统的配置文件，可通过以下方式启动：
```sh
# 该脚本默认挂载配置文件路径为：
# 宿主机/sia-gateway/sia-gateway-admin-buildcomponent/config/
# 宿主机/sia-gateway/sia-gateway-buildcomponent/config/
sh docker-run.sh  v

```
+ 目前该套Docker镜像部署配置适用于linux、unix版本的docker环境中，对于window-docker环境可能会出现：编码问题、window路径找不到等问题；如果一定要运行在window版本docker环境中，开发人员需要自行解决这些问题。

	

 












