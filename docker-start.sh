#!/usr/bin/env bash
/opt/nginx/sbin/nginx -c /opt/nginx/conf/nginx.conf

cd /app/jar/ROOT/gatewayadmin/bin
chmod +x *.sh 
/app/jar/ROOT/gatewayadmin/bin/start_gateway_admin_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_service_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_synchspeed_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_stream_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_monitor_test.sh

echo "启动网关核心"

cd /app/jar/ROOT/gateway/bin
chmod +x *.sh 
/app/jar/ROOT/gateway/bin/start_gateway_test.sh

echo "启动完毕"

while true;do
echo ">>>" > /tmp/acc.log
sleep 60
done

