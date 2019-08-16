FROM centos:latest
MAINTAINER Guohui Xie

# add third part
ADD ./third-libary/  /opt/

# add yum
RUN  curl -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo \
    && rpm -ivh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm \
    && rpm -ivh /opt/tcl-8.5.13-8.el7.x86_64.rpm \
    && rpm -ivh /opt/tcl-devel-8.5.13-8.el7.x86_64.rpm \
    && yum clean all \
    && yum makecache \
    && yum install -y gcc \
                    gcc-c++ \
                    glibc* \
                    automake \
                    autoconf \
                    libtool \
                    make \
                    libxml2-devel \
                    pcre-devel \
                    openssl \
                    openssl-devel \
                    libicu-devel \
                    file libaio \
                    libaio-devel \
                    libXext \
                    libmcrypt \
                    libmcrypt-devel \
                    numactl \
                    unzip \
                    zip \
                    groupinstall \
                    chinese-support \
                    vixie-cron \
                    crontabs  \
                    telnet-server  \
                    telnet.*  \
                    java-1.8.0-openjdk \
                    lsof \
                    sudo \
                    nginx

# language setting
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
ENV LC_ALL "zh_CN.UTF-8"

# add admin server
ADD sia-gateway-admin-buildcomponent/target/gateway_admin_1.0.zip  /app/jar/ROOT/

# add gateway-core cluster
ADD sia-gateway-buildcomponent/target/gateway_1.0.zip  /app/jar/ROOT/

# add admin-display
ADD  sia-gateway-admin-display/dist   /app/jar/ROOT/dist/

RUN     cd  /app/jar/ROOT \
        && unzip gateway_1.0.zip \
        && unzip gateway_admin_1.0.zip \
        && rm -rf  gateway_1.0.zip \
        && rm -rf  gateway_admin_1.0.zip \
        && cp /opt/docker-start.sh  /app/jar/ROOT \
        && chmod +x docker-start.sh \

