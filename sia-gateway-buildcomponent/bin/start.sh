#!/bin/sh
#while true;do
#echo aaa 
#sleep 10
#done

mkdir -p /app/jar/logs/{{HOSTNAME}}/applog
mkdir -p /app/jar/logs/{{HOSTNAME}}/logbak

sh /app/jar/ROOT/gateway/bin/k8s_start_gateway_test.sh

