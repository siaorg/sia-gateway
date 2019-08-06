#!/bin/sh

# First, find suitable JDK

version=$("java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
jdk_home="no"
if [[ "$version" > "1.8" ]]; then
       jdk_home=${JAVA_HOME}
       echo "default JDK version is OK, JDK home is $jdk_home"
else
      jdk_path=/opt
      echo "begin to find suitable JDK...."
      for path in `find $jdk_path -name jmap`
      do
         _java=${path%/*}/java
         version=$("$_java" -version 2>&1 | awk -F '"' '{print $2}')
         if [[ "$version" > "1.8" ]]; then
             jdk_home=${_java%/bin*}
             echo "find out suitable JDK, JDK home is $jdk_home"
             break
         fi
      done
fi

if [ "$jdk_home" == "no" ] ;then
  echo "no suitable JDK was found, which is required jdk1.8, exit"
  exit 0
fi

JAVA_HOME=$jdk_home
CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/jre/lib
export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
echo "-------------------------java info-------------------------"
echo $(java -version)
echo "-------------------------pwd-------------------------"
echo $(pwd)

# Second, choose profile
working_directory="/app/jar/ROOT/gateway/bin"
config_directory="/app/jar/ROOT/gateway/config"
gateway_config="$1.yml"

echo "using workspace: $working_directory"
echo "config_directory: $config_directory"
echo "gateway_config: $config_directory/$gateway_config"

if [ ! -f "$config_directory/$gateway_config" ]; then
  echo "$config_directory/$gateway_config is not exists!"
  exit 0
fi

javaOpts="-server -Xms3550m -Xmx3550m -Xmn2g -Xss256k -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:CMSIncrementalDutyCycleMin=0 -XX:CMSIncrementalDutyCycle=10 -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:-CMSParallelRemarkEnabled -XX:CMSFullGCsBeforeCompaction=0 -XX:CMSInitiatingOccupancyFraction=70 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=."
java $javaOpts -XX:OnOutOfMemoryError='kill -9 %p'  -Dspring.config.location=$config_directory/$gateway_config  -jar  $working_directory/$2 
