#!/bin/bash

PROJECT_DIR=`pwd`
BUILD_DIR=$PROJECT_DIR/build
rm -rf $BUILD_DIR/test-classes
mkdir -p $BUILD_DIR/test-classes

CP="."; for i in `find ./lib/ -iname '*.jar'`; do CP="$CP:$i"; done
javac -cp $CP:$BUILD_DIR/classes -d $BUILD_DIR/test-classes $PROJECT_DIR/test/com/epickrram/testing/*.java

JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=9999"
AGENT_OPTS="-javaagent:./build/dist/object-tracker-agent-0.1.jar -Xbootclasspath/a:./lib/*.jar"

CMD="java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -cp $CP:$BUILD_DIR/test-classes $JMX_OPTS $AGENT_OPTS -Dcom.epickrram.tool.object-tracker.config.file=$PROJECT_DIR/test/config.properties com.epickrram.testing.Main"
echo $CMD
$CMD

