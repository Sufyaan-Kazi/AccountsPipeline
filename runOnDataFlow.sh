#!/bin/sh
set -e
. ./vars.txt
. ./common.sh

export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}
class=com.suf.dataflow.banking.AccountsPrePrep
maven_runner=dataflow-runner
projectID=sufaccounts
mappingFile=gs://sufbankdata/starling.config
runner=DataflowRunner
region=europe-west1
zone=europe-west2-c

#GCS Bucket args
sourceFolder=gs://sufbankdata/input/
sourceBarclaysFolder=gs://sufbankdata/barclays/
outputBarclaysFolder=gs://sufbankdata/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:sufbankingds.starlingtxns

main() {
  trap 'abort' 0

  mvn_args="--project=$projectID \
--runner=$runner \
--tempLocation=gs://suftempbucket/staging \
--region=$region \
--zone=$zone \
--numWorkers=1 \
--mappingFile=$mappingFile \
--sourceFolder=$sourceFolder \
--outputStarlingFolder=$outputStarlingFolder \
--outputBarclaysFolder=$outputBarclaysFolder \
--BQTable=$BQTable"
         
  mvn -P$maven_runner compile exec:java \
      -Dexec.mainClass=$class \
      -Dexec.args="${mvn_args}"

  trap : 0
}

prepareConfigMapping() {
  gsutil cp src/main/resources/starling.config gs://sufbankdata/
}

prepareConfigMapping
tidyUp gs://sufbankdata/output/

tidyUp ${sourceFolder}
gsutil -m cp -r gs://sufbankdata/starling/* ${sourceFolder}
gsutil -m cp -r gs://sufbankdata/barclays/* ${sourceFolder}

main
