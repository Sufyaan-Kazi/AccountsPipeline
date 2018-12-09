#!/bin/sh
set -e

trap 'abort' 0

rm -rf ./output/
mvn compile exec:java -Dexec.mainClass=com.suf.AccountsPrePrep -Dexec.args="--inputFile=pom.xml --output=counts" -Pdirect-runner

trap : 0
