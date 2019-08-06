#!/bin/sh
nohup ./stop.sh gateway_service_test >$(pwd)/gateway_service_test.shutdown 2>&1 &
