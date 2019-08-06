#!/bin/sh
nohup ./stop.sh gateway_admin_test >$(pwd)/gateway_admin_test.shutdown 2>&1 &
