#!/bin/sh
set -e
. ./vars.txt
. ./common.sh
export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

main() {
  tidyUp

  set -e
  trap 'abort' 0

  class=com.suf.dataflow.banking.AccountsPrePrep
  project=sufaccounts
  maven_runner=direct-runner

  mvn -P$maven_runner compile exec:java -Dexec.mainClass=$class -Dexec.args="--project=$project --tempLocation=gs://suftempbucket/staging"

  trap : 0
}

main
