#!/bin/sh
export GOOGLE_APPLICATION_CREDENTIALS=/home/sufyaankazi/keys/dflow-accounts.json 
mvn compile exec:java -Dexec.mainClass=com.suf.AccountsPrePrep -Dexec.args="--inputFile=pom.xml --output=counts" -Pdirect-runner
