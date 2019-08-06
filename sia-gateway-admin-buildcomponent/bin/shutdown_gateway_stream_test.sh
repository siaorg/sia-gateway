#!/bin/sh
nohup ./stop.sh gateway_stream_test >$(pwd)/gateway_stream_test.shutdown 2>&1 &
