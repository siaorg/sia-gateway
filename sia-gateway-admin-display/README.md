## 前端部署
### 环境准备
（1）node环境安装 => https://nodejs.org/en/download/
    
（2）Nginx安装

### 前端项目打包
（1）进入本地的项目执行命令进行打包 => npm run build 

    注：1、打包完成在改文件夹下面生成dist文件夹，改文件夹为前端工程
    
        2、dist/static文件夹下面的site.map.js为后端服务配置（ip：port形式），根据项目需求自行更改
### 前端项目部署
（1）nginx的代理配置

进入nginx的目录下nginx.conf，添加如下代理：


```
server {
    listen       8080; // 前端页面监听端口
    server_name  localhost;
    location / {
        root  app/dist; // 前端包存放目录
        index  index.html index.htm;
        try_files $uri $uri/ @router;
    }
    location @router {
         rewrite ^.*$ /index.html last;
    }
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   html;
    }
}

server {
    listen       80;
    server_name  localhost;
    location / {
        proxy_pass http://*.*.*.*:8081; // 后端服务地址
        add_header 'Access-Control-Allow-Origin' 'http://*.*.*.*:8080';
        add_header 'Access-Control-Allow-Credentials' 'true';
    }
}
```