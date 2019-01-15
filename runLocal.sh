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

## This script runs the Beam Pipeline using the Direct Runner on a local machine
# The script does the following:
#  - Reads in variables (vars.txt) and common functions (common.sh)
#  - maps the variables from vars.txt into Pipeline Options
#  - Copies the config mapping file from the project to Google Cloud Storage
#  - Removes previous output files
#  - Copies all the data from Bank specific output folders into one single input folder 
#  - Launch Beam
#
# Any errors are caught by a UNIX trap
#

# Catch errors and read in input
set -e
. ./vars.txt
. ./common.sh

#Set the creedentials needed by the GCP API invoked by BEAM
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
  printf "\n ** Getting Ready to execute **\n"
  # Copy the config mapping to GCS
  prepareConfigMapping

  ##Remove previous output locally and on GCS
  tidyUp gs://${BUCKET_NAME}/output/
  tidyUp ${sourceFolder}
  gsutil -m cp -r gs://${BUCKET_NAME}/starling/* ${sourceFolder}
  gsutil -m cp -r gs://${BUCKET_NAME}/barclays/* ${sourceFolder}

  #Since the tidy up process may throw errors if there is nothing to actually tidy up,
  #we only start trapping for errors here
  set -e
  trap 'abort' 0

  printf "\n\n*** About to launch pipeline ***\n"

  # Build up the pipeline options
  mvn_args="--project=$projectID \
--tempLocation=gs://${TEMPBUCKET}/staging \
--mappingFile=$mappingFile \
--sourceFolder=$sourceFolder \
--outputStarlingFolder=$outputStarlingFolder \
--BQTable=$BQTable \
--outputBarclaysFolder=$outputBarclaysFolder"
	
  #Launch the pipeline 
  mvn -P$maven_runner compile exec:java \
      -Dexec.mainClass=$class \
      -Dexec.args="${mvn_args}"

  trap : 0
}

prepareConfigMapping() {
  gsutil cp src/main/resources/${CONFIG_FILE} gs://${BUCKET_NAME}/
}

main
