#!/bin/sh
nohup ./run4service.sh gateway_service_test sia-gateway-service-1.0.jar >$(pwd)/gateway_service_test.start 2>&1 &
