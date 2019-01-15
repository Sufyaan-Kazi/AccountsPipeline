#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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
