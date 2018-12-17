#!/bin/sh
set -e

trap 'abort' 0
. ./vars.txt

export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

rm -rf ./output/

class=com.suf.AccountsPrePrep
project=sufaccounts
maven_runner=direct-runner

mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --tempLocation=gs://suftempbucket/staging"

trap : 0
