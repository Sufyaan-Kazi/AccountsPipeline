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

# This script is used to setup and initialise required resources used by the main project scripts (runXXX.sh)
# The script does the following
#  - Read in parameters defined in vars.txt
#  - Enable the project API's defined in vars.txt if they have not been enabled already
#  - Creates a Service account with the right permissions for this project
#  - Creates the dataset in BigQuery

set -e
. ./common.sh

main() {
  # Read in Vars
  . ./vars.txt

  # Do stuff
  enableAPIS
  #getCredentials
  createServiceAccount
  createBQDataset
  freshenData
}

#
## Create the Big Query dataset
# The script does the following:
#  - Create the dataset in the region defined in vars.txt and then creates the table
#  - Uses a template json (bq.json) file with the correct permissions mapped to create a version of it 
#    with the required dataset, tablename and service account name and then writes this to a temporary file
#  - Updates BigQuery with the permissions file
#  - Removes the temporary json file used in the previous step
#
createBQDataset(){
## Create the dataset and table
  bq mk --location $BQ_REGION -d ${DATASET}
  bq mk -t ${DATASET}.${TABLE}

  # Give the service account permission on the dataset using the generic file
  # Use sed to replace generic param text with the runtime variables
  sed -e 's/DATASET/'"${DATASET}"'/g' bq.json | sed -e 's/SERVICE_ACC/'"${SERVICE_ACC}"'/g' | sed -e 's/PROJECT_ID/'"${PROJECT_ID}"'/g' | sed -e 's/TABLE/'"${TABLE}"'/g' > bq_${DATE}.json
  bq update --source ./bq_${DATE}.json ${PROJECT_ID}:${DATASET}

  # Tidy up
  rm bq_${DATE}.json
  #bq show --format=prettyjson ${DATASET}
}

#
# get Credentials for default compute service account
# Not used
#
getCredentials() {
  SERVICE_ACC=$(gcloud iam service-accounts list | grep '\-compute\@' | xargs | cut -d " " -f6)
  echo "Getting key for $SERVICE_ACC"
  gcloud iam service-accounts keys create $KEY_FILE --iam-account $SERVICE_ACC

  mv ${KEY_FILE} ${KEY_DIR}

  #Store the key in env variable
  export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}
  echo ${GOOGLE_APPLICATION_CREDENTIALS}
}

#
## Create the Service account
# This script does the following:
#  - Determine if the service account exists or not
#  - Assuming it doesn't, it creates it using the values defined in vars.txt
#  - Adds a policy binding for the list of roles defined in vars.txt
#  - Waits for the policy bindings to propagate
#  - Creates a key for the service account and stores this json in the directory defined in vars.txt
#  - Exports this key to the Google env variable which will be used by the GCP API invoked by Beam
#
createServiceAccount() {
  # Check if the service account exists or not?
  SERVICE_ACC_EXISTS=$(gcloud iam service-accounts list | grep ${KEY_FILE} | wc -l)

  ## Otherwise
  if [ ${SERVICE_ACC_EXISTS} -eq 0 ]
  then
    #Create the service account
    echo "*** Creating Service Account - ${KEY_NAME}"
    gcloud iam service-accounts create ${KEY_NAME} --display-name=${KEY_NAME}

    # Add Role Policy Bindings
    # Dataflow needs to create and then delete temp objects so we give admin in lieu of creating a user defined role
    echo "*** Adding Role Policy Bindings ***"
    declare -a roles=(${SERVICE_ACC_ROLES})
    for role in "${roles[@]}"
    do
      echo "Adding role: ${role} to service account $SERVICE_ACC"
      gcloud projects add-iam-policy-binding ${PROJECT_ID} --member "serviceAccount:${SERVICE_ACC}.iam.gserviceaccount.com" --role "${role}" --quiet > /dev/null || true
    done

    # We need a few seconds for the policies to propagate ot the bucket and other resources
    echo "*** Waiting for Policy Bindings to Propagate ... ***"
    sleep 15

    # create the key
    gcloud iam service-accounts keys create $KEY_FILE --iam-account ${SERVICE_ACC}.iam.gserviceaccount.com
    #gcloud auth activate-service-account --key-file ${KEY_FILE}
    mv ${KEY_FILE} ${KEY_DIR}

    #Store the key in env variable
    export GOOGLE_APPLICATION_CREDENTIALS=${KEY_DIR}/${KEY_FILE}
    echo ${GOOGLE_APPLICATION_CREDENTIALS}
  fi
}

## Enable GCloud APIS
# This functions does the following
#  - Grab the list of currently enabled API's in the project
#  - Loop through the list of API's required in vars.txt
#  - For each API
#    - Check if it has been enabled already
#    - Assuming it hasn't, then enable it
#
enableAPIS() {
  ENABLED_APIS=$(gcloud services list --enabled | grep -v NAME | sort | cut -d " " -f1)
  #echo "Current APIs enabled are: ${ENABLED_APIS}"

  declare -a REQ_APIS=(${APIS})
  for api in "${REQ_APIS[@]}"
  do
    EXISTS=$(echo ${ENABLED_APIS} | grep ${api} | wc -l)
    if [ ${EXISTS} -eq 0 ]
    then
      echo "*** Enabling ${api} API"
      gcloud services enable "${api}.googleapis.com"
      sleep 2
    fi
  done
}

## Tidy up previous output and copy the source data
freshenData() {
  ditchBucket ${BUCKET_NAME}
  ditchBucket ${TEMPBUCKET}

  #In my proj, the source region is the same used for BigQuery, i.e. London
  gsutil mb -c regional -l ${BQ_REGION} gs://${BUCKET_NAME}/
  gsutil mb -c regional -l ${BQ_REGION} gs://${TEMPBUCKET}/

  echo "Assigning read permissions to ${SERVICE_ACC} on gs://${SRC_BUCKET_NAME}"
  gsutil -m acl ch -R -u ${SERVICE_ACC}.iam.gserviceaccount.com:READ gs://${SRC_BUCKET_NAME}/ > /dev/null 2>&1
  echo "Copying source files into this project."
  gsutil -m cp -r gs://${SRC_BUCKET_NAME}/starling/* gs://${BUCKET_NAME}/${INPUT_FOLDER}
  gsutil -m cp -r gs://${SRC_BUCKET_NAME}/barclays/* gs://${BUCKET_NAME}/${INPUT_FOLDER}
}

trap 'abort' 0
SECONDS=0
main
trap : 0
printf "\nProject Setup Complete in ${SECONDS} seconds.\n"
