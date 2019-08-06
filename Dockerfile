FROM centos:latest
MAINTAINER Guohui Xie

# add yum
RUN /bin/bash -c 'curl -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo;yum clean all;yum makecache'

RUN  yum install -y gcc gcc-c++ glibc* automake autoconf libtool make libxml2-devel pcre-devel openssl openssl-devel libicu-devel file libaio libaio-devel libXext libmcrypt libmcrypt-devel numactl unzip zip groupinstall chinese-support vixie-cron crontabs  telnet-server  telnet.*  java-1.8.0-openjdk lsof && localedef -c -f UTF-8 -i zh_CN zh_CN.utf8 

# add third part
ADD ./third-libary/  /opt/
RUN /bin/bash -c 'rpm -ivh /opt/tcl-8.5.13-8.el7.x86_64.rpm;rpm -ivh /opt/tcl-devel-8.5.13-8.el7.x86_64.rpm;rm -rf /opt/tcl*;cd /opt/;tar zxvf nginx.tar.gz;rm -rf nginx.tar.gz;useradd nginx -s /sbin/nologin;chown -R nginx.nginx /opt/nginx'
ENV LC_ALL "zh_CN.UTF-8"

# add admin server
 ADD sia-gateway-admin-buildcomponent/target/gateway_admin_1.0.zip  /app/jar/ROOT/
# add gateway core
ADD sia-gateway-buildcomponent/target/gateway_1.0.zip  /app/jar/ROOT/
# add UI
ADD  sia-gateway-admin-display/dist   /app/jar/ROOT/dist/

# add start sh
ADD docker-start.sh  /app/jar/ROOT/

RUN  /bin/bash -c 'cd  /app/jar/ROOT;chmod +x *.sh;unzip gateway_1.0.zip;unzip gateway_admin_1.0.zip;rm -rf  gateway_1.0.zip;rm -rf  gateway_admin_1.0.zip'
