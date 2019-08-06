#!/bin/sh
java -server -Xms128m -Xmx256m -Xss256k -jar /app/gantrygw/gantry-boot-admin-1.0-SNAPSHOT.jar --group.name=gantry --server.port=10000
 &