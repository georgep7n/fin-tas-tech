#!/bin/sh

gradlew clean
gradlew build
cp ConfigBuilder.groovy build/distributions
pushd build/distributions
tar -xf *.tar
fintastech/bin/fintastech ConfigBuilder.groovy
