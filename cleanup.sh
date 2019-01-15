#!/bin/bash

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

# This script is used to remove objects created in previous eexecutions of this projects' scripts
# It does the following:
#  - Read in parameters (vars.txt)
#  - Removes the previous service account once it has removed mapped policy bindings
#  - Removes the dataset and table from BigQuery
#  - Ends
# The script will print it's execution time in seconds and use UNIX trap to capture errors during execution

set -e

#Main logic
main() {
  trap 'abort' 0

  SECONDS=0

  . ./vars.txt

  removeServiceAcc 
  removeDataset 

  trap : 0
  echo "Project Cleanup Complete in ${SECONDS} seconds."
}

#Handle error
abort()
{
  echo >&2 '
  ***************
  *** ABORTED ***
  ***************
  '
  echo "An error occurred. Exiting..." >&2
  exit 1
}

#Remove the dataset from BigQuery
removeDataset() {
  echo "Removing Dataset: " ${DATASET}
  bq rm -rf ${DATASET}
}

#Remove the Service Account named in vars.txt (SERVICE_ACC)
# - It checks to see if the service account exists in the project
# - Assuming it does, it removes the policy bindings for any roles listed in vars.txt for this service account
# - Then it deletes the service account
removeServiceAcc(){
  #Remove permissions from the bucket
  #gsutil defacl ch -d ${SERVICE_ACC}.iam.gserviceaccount.com gs://${BUCKET_NAME}/

  #
  # Remove Service Account Roles
  #
  SERVICE_ACC_ROLES_EXIST=$(gcloud projects get-iam-policy ${PROJECT_ID} | grep ${SERVICE_ACC}.iam.gserviceaccount.com | wc -l)
  if [ $SERVICE_ACC_ROLES_EXIST -ne 0 ]
  then
    declare -a roles=(${SERVICE_ACC_ROLES})
    for role in "${roles[@]}"
    do
      echo "Removing role: ${role} for service account: $SERVICE_ACC"
      gcloud projects remove-iam-policy-binding ${PROJECT_ID} --member "serviceAccount:${SERVICE_ACC}.iam.gserviceaccount.com" --role "${role}" --quiet > /dev/null || true
    done
  fi

  #
  # Remove Service Account
  #
  SERVICE_ACC_EXISTS=$(gcloud iam service-accounts list | grep $SERVICE_ACC | wc -l)
  if [ $SERVICE_ACC_EXISTS -ne 0 ]
  then
    gcloud iam service-accounts delete ${SERVICE_ACC}.iam.gserviceaccount.com -q
  fi
}

main
