#!/bin/sh
set -e
. ./vars.txt
. ./common.sh
export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}

#Java related args
class=com.suf.dataflow.banking.AccountsPrePrep
projectID=sufaccounts
maven_runner=direct-runner
mappingFile=gs://sufbankdata/starling.config

#GCS Bucket args
sourceFolder=gs://sufbankdata/input/
outputStarlingFolder=gs://sufbankdata/output/starling_accounts
outputBarclaysFolder=gs://sufbankdata/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:sufbankingds.starlingtxns

main() {
  mvn_args="--project=$projectID \
--tempLocation=gs://suftempbucket/staging \
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
  gsutil cp src/main/resources/starling.config gs://sufbankdata/
}

prepareConfigMapping
tidyUp gs://sufbankdata/output/
tidyUp ${sourceFolder}
gsutil -m cp -r gs://sufbankdata/starling/* ${sourceFolder}
gsutil -m cp -r gs://sufbankdata/barclays/* ${sourceFolder}

set -e
trap 'abort' 0
SECONDS=0
main
trap : 0
echo "Script completed in ${SECONDS} seconds."
