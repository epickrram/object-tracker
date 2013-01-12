#!/bin/bash

PROJECT_DIR=`pwd`
BUILD_DIR=$PROJECT_DIR/build
LIB_DIR=$PROJECT_DIR/lib

mkdir -p $BUILD_DIR/release/object-tracker-agent-0.1

cd $BUILD_DIR/release

cp $LIB_DIR/*.jar ./object-tracker-agent-0.1
cp $BUILD_DIR/dist/*.jar ./object-tracker-agent-0.1

jar cf object-tracker-agent-0.1.zip *

cd $PROJECT_DIR
