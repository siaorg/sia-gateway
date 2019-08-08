#!/usr/bin/env bash
str=$(cd "$(dirname "$0")";pwd)

volums_path="-v ${str}/sia-gateway-admin-buildcomponent/config:/app/jar/ROOT/gatewayadmin/config -v ${str}/sia-gateway-buildcomponent/config:/app/jar/ROOT/gateway/config"
# volums_path="-v ${str}/config:/app/jar/ROOT/gatewayadmin/config -v ${str}/config:/app/jar/ROOT/gateway/config"
if  [ ! $1 ]; then
volums_path=""
fi

# -v /etc/localtime:/etc/localtime
docker run --name gateway-test -d  ${volums_path}  -p 18086:18086 -p 8080:8080 -p 8040:8040  --restart=on-failure:10  --privileged=true reg.caiwu/sia/gateway:v1   /bin/bash -c " /app/jar/ROOT/docker-start.sh "

