#!/bin/sh

chmod +x *.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_admin_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_service_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_synchspeed_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_stream_test.sh
/app/jar/ROOT/gatewayadmin/bin/start_gateway_monitor_test.sh

echo "启动网关核心"
