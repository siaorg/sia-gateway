#!/bin/sh
if [ $# -eq 0 ]; then
  sh /app/jar/ROOT/gateway/bin/k8s_run.sh gateway_test sia-gateway-core-1.0.jar
else
  sh /app/jar/ROOT/gateway/bin/k8s_run.sh $1 sia-gateway-core-1.0.jar
fi