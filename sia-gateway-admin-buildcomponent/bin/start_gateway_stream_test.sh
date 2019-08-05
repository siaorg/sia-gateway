#!/bin/sh
nohup ./run4stream.sh gateway_stream_test  sia-gateway-stream-1.0.jar >$(pwd)/gateway_stream_test.start 2>&1 &
