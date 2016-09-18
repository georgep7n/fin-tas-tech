#!/bin/sh

gradlew clean
gradlew build
pushd build/distributions
tar -xf *.tar
fintastech/bin/fintastech
