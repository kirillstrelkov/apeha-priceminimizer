#!/usr/bin/env bash
PREV_DIR=$(pwd)
SCRIPT_DIR="$( dirname "${BASH_SOURCE[0]}" )"
BUILD_DIR=${SCRIPT_DIR}/build
mkdir -p ${BUILD_DIR}
cd ${SCRIPT_DIR}
pwd
mvn clean
echo "Building Windows x32"
mvn package -P win32
echo "Building Windows x64"
mvn package -P win64
echo "Building Linux x32"
mvn package -P linux32
echo "Building Linux x64"
mvn package -P linux64
echo "Building Mac x64"
mvn package -P mac64
cd ${PREV_DIR}
pwd
cp ${SCRIPT_DIR}/target/pricemin-*.jar ${BUILD_DIR}
chmod +x ${SCRIPT_DIR}/target/*.jar
chmod +x ${BUILD_DIR}/*.jar