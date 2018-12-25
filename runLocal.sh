#!/bin/sh
set -e
. ./vars.txt
. ./common.sh
export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

main() {
  set -e
  trap 'abort' 0

  class=com.suf.dataflow.banking.AccountsPrePrep
  project=sufaccounts
  maven_runner=direct-runner
  mappingFile=gs://sufbankdata/starling.config
  sourceStarlingFolder=gs://sufbankdata/starling/*
  outputStarlingFolder=gs://sufbankdata/output/starling_accounts
  BQTable=sufaccounts:sufbankingds.starlingtxns

  mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --tempLocation=gs://suftempbucket/staging --mappingFile=$mappingFile --sourceStarlingFolder=$sourceStarlingFolder --outputStarlingFolder=$outputStarlingFolder --BQTable=$BQTable"

  trap : 0
}

prepareConfigMapping() {
  gsutil cp src/main/resources/starling.config gs://sufbankdata/
}

prepareConfigMapping
tidyUp
main
