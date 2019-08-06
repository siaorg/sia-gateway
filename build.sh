#!/usr/bin/env bash
cd  ./sia-gateway-admin-buildcomponent/ &&  mvn clean install -Dmaven.test.skip=true
cd  ../sia-gateway-buildcomponent/ &&  mvn clean install -Dmaven.test.skip=true
cd  ../sia-gateway-admin-display/ && sudo npm install -g npm && npm run build



