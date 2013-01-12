#!/bin/bash

PROJECT_DIR=`pwd`
MANIFEST=$PROJECT_DIR/conf/MANIFEST.MF
BUILD_DIR=$PROJECT_DIR/build

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/classes

CP="."; for i in `find ./lib/ -iname '*.jar'`; do CP="$CP:$i"; done
javac -cp $CP -d $BUILD_DIR/classes -source 5 -target 5 $PROJECT_DIR/src/com/epickrram/tool/tracker/*.java $PROJECT_DIR/src/com/epickrram/tool/tracker/client/*.java $PROJECT_DIR/src/com/epickrram/tool/tracker/agent/*.java $PROJECT_DIR/src/com/epickrram/tool/tracker/client/gui/*.java

mkdir $BUILD_DIR/dist

cd $BUILD_DIR/classes

jar cmf $MANIFEST $BUILD_DIR/dist/object-tracker-agent-0.1.jar *

cd $PROJECT_DIR
