#!/bin/sh
nohup ./run.sh gateway_admin_test  sia-gateway-admin-1.0.jar >$(pwd)/gateway_admin_test.start 2>&1 &
