#!/bin/sh
set -e
. ./vars.txt
. ./common.sh
export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

#Java related args
class=${MAIN_CLASS}
projectID=${PROJECT_ID}
maven_runner=direct-runner
mappingFile=gs://${BUCKET_NAME}/${CONFIG_FILE}

#GCS Bucket args
sourceFolder=gs://${BUCKET_NAME}/input/
outputStarlingFolder=gs://${BUCKET_NAME}/output/starling_accounts
outputBarclaysFolder=gs://${BUCKET_NAME}/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:${DATASET}.${TABLE}

main() {
  mvn_args="--project=$projectID \
--tempLocation=gs://${TEMPBUCKET}/staging \
--mappingFile=$mappingFile \
--sourceFolder=$sourceFolder \
--outputStarlingFolder=$outputStarlingFolder \
--BQTable=$BQTable \
--outputBarclaysFolder=$outputBarclaysFolder"
	 
  mvn -P$maven_runner compile exec:java \
      -Dexec.mainClass=$class \
      -Dexec.args="${mvn_args}"
}

prepareConfigMapping() {
  gsutil cp src/main/resources/${CONFIG_FILE} gs://${BUCKET_NAME}/
}

prepareConfigMapping
tidyUp gs://${BUCKET_NAME}/output/
tidyUp ${sourceFolder}
gsutil -m cp -r gs://${BUCKET_NAME}/starling/* ${sourceFolder}
gsutil -m cp -r gs://${BUCKET_NAME}/barclays/* ${sourceFolder}

set -e
trap 'abort' 0
SECONDS=0
main
trap : 0
echo "Script completed in ${SECONDS} seconds."
