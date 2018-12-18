#!/bin/sh
set -e
. ./vars.txt
. ./common.sh

export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

main() {
  trap 'abort' 0

  class=com.suf.AccountsPrePrep
  project=sufaccounts
  region=europe-west1
  maven_runner=dataflow-runner
  runner=DataflowRunner
  zone=europe-west2-c

  tidyUp
  mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --runner=$runner --tempLocation=gs://suftempbucket/staging --region=$region --zone=$zone --numWorkers=1"

  trap : 0
}

main
