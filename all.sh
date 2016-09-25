#!/bin/sh

CONFIG_FILE=$PWD/ConfigBuilder.groovy
LOANS_DIR=$PWD/lendingclub
gradlew clean
gradlew build
pushd build/distributions
tar -xf *.tar
fintastech/bin/fintastech $CONFIG_FILE $LOANS_DIR
