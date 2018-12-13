#!/bin/sh
set -e
trap 'abort' 0
. ./vars.txt
export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

#rm -rf ./output/

class=com.suf.AccountsPrePrep
project=sufaccounts
region=europe-west1
maven_runner=dataflow-runner
#maven_runner=direct-runner
runner=DataflowRunner
zone=europe-west2-c

mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --runner=$runner --tempLocation=gs://suftempbucket/staging --region=$region --zone=$zone"
#mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --tempLocation=gs://suftempbucket/staging"

trap : 0
