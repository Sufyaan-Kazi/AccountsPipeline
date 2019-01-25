#!/bin/sh
# Copyright 2018 Google LLC. This software is provided as-is, without warranty or representation for any use or purpose. Your use of it is subject to your agreements with Google. 

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
projectID=${PROJECT_ID}
mappingFile=gs://${BUCKET_NAME}/${CONFIG_FILE}

#GCS Bucket args
sourceFolder=gs://${BUCKET_NAME}/input/
sourceBarclaysFolder=gs://${BUCKET_NAME}/barclays/
outputBarclaysFolder=gs://${BUCKET_NAME}/output/barclays_accounts

#BigQuery related args
BQTable=${projectID}:${DATASET}.${TABLE}

main() {
  # Copy Config
  gsutil cp src/main/resources/${CONFIG_FILE} gs://${BUCKET_NAME}/

  #Map input args into required args for Dataflow or Direct runnner
  processArgs $@

  mvn -P$maven_runner compile exec:java \
      -Dexec.mainClass=$class \
      -Dexec.args="${mvn_args}"
}

buildDFlowMavenArgs() {
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
}

buildDirectArgs() {
  maven_runner=direct-runner

  mvn_args="--project=$projectID \
--tempLocation=gs://${TEMPBUCKET}/staging \
--mappingFile=$mappingFile \
--sourceFolder=$sourceFolder \
--outputStarlingFolder=$outputStarlingFolder \
--BQTable=$BQTable \
--outputBarclaysFolder=$outputBarclaysFolder"
}

processArgs() {
  if [ "$#" -eq 0 ]
  then
    buildDirectArgs
  else
    maven_runner=dataflow-runner
    runner=DataflowRunner
    region=${DF_REGION}
    zone=${DF_ZONE}
    buildDFlowMavenArgs
  fi
}

trap 'abort' 0

if [ "$#" -eq 0 ]
then
  echo "No arguments supplied, so this pipeline will be run in direct mode"
  echo "To run on dataflow please launch again with a param, e.g. $0 blah"
  echo ""
else
  echo "Arguments supplied, so this pipeline will be run in dataflow mode"
  echo "To run in direct mode please launch again with no params, e.g. $0"
  echo ""
fi

main $@
trap : 0
