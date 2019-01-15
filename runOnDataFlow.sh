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

## This scripts executes the Pipeline on Dataflow
# This script does the following:
#  - Reads in parameters (vars.txt) and common functions (common.sh)
#  - Map parameters into shell variables to be used as Pipeline options
#  - Copies the config mapping (used to map text in the transaction to a category) upto GCS
#  - Build the Pipeline Options var
#  - Launches the Pipeline on Dataflow
#

set -e
. ./vars.txt
. ./common.sh

export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}
class=${MAIN_CLASS}
maven_runner=dataflow-runner
projectID=${PROJECT_ID}
mappingFile=gs://${BUCKET_NAME}/${CONFIG_FILE}
runner=DataflowRunner
region=${DF_REGION}
zone=${DF_ZONE}

#GCS Bucket args
sourceFolder=gs://${BUCKET_NAME}/input/
sourceBarclaysFolder=gs://${BUCKET_NAME}/barclays/
outputBarclaysFolder=gs://${BUCKET_NAME}/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:${DATASET}.${TABLE}

main() {
  prepareConfigMapping
  tidyUp gs://${BUCKET_NAME}/output/

  tidyUp ${sourceFolder}
  gsutil -m cp -r gs://${BUCKET_NAME}/starling/* ${sourceFolder}
  gsutil -m cp -r gs://${BUCKET_NAME}/barclays/* ${sourceFolder}

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

SECONDS=0
main
