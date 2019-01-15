#!/bin/sh
set -e
. ./vars.txt
. ./common.sh

export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}
class=${MAIN_CLASS}
maven_runner=dataflow-runner
projectID=${PROJECT_ID}
mappingFile=gs://${BUCKET_NAME}/${CONFIG_FILE}
runner=DataflowRunner
region=europe-west1
zone=europe-west2-c

#GCS Bucket args
sourceFolder=gs://${BUCKET_NAME}/input/
sourceBarclaysFolder=gs://${BUCKET_NAME}/barclays/
outputBarclaysFolder=gs://${BUCKET_NAME}/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:${DATASET}.${TABLE}

main() {
  trap 'abort' 0

  mvn_args="--project=$projectID \
--runner=$runner \
--tempLocation=gs://${TEMPBUCKET}/staging \
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
  gsutil cp src/main/resources/${CONFIG_FILE} gs://${BUCKET_NAME}/
}

prepareConfigMapping
tidyUp gs://${BUCKET_NAME}/output/

tidyUp ${sourceFolder}
gsutil -m cp -r gs://${BUCKET_NAME}/starling/* ${sourceFolder}
gsutil -m cp -r gs://${BUCKET_NAME}/barclays/* ${sourceFolder}

main
