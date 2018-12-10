#!/bin/bash
set -e

main() {
  # Read in Vars
  . ./vars.txt

  # Do stuff
  enableAPIS
  createServiceAccount
  createBQDataset
}

#
# Handle an error in the script
#
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

#
## Create the Big Query dataset
#
createBQDataset(){
## Create the dataset and table
  bq mk --location europe-west2 -d ${DATASET}
  bq mk -t ${DATASET}.${STARLING_TABLE}

  # Give the service account permission on the dataset using the generic file
  # Use sed to replace generic param text with the runtime variables
  sed -e 's/DATASET/'"${DATASET}"'/g' bq.json | sed -e 's/SERVICE_ACC/'"${SERVICE_ACC}"'/g' | sed -e 's/PROJECT_ID/'"${PROJECT_ID}"'/g' | sed -e 's/TABLE/'"${STARLING_TABLE}"'/g' > bq_${DATE}.json
  bq update --source ./bq_${DATE}.json ${PROJECT_ID}:${DATASET}

  # Tidy up
  rm bq_${DATE}.json
  bq show --format=prettyjson ${DATASET}
}

#
## Create the Service accounts
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
    declare -a roles=(${SERVICE_ACC_ROLES})
    for role in "${roles[@]}"
    do
      echo "Adding role: ${role} to service account $SERVICE_ACC"
      gcloud projects add-iam-policy-binding ${PROJECT_ID} --member "serviceAccount:${SERVICE_ACC}.iam.gserviceaccount.com" --role "${role}" --quiet
    done

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
enableAPIS() {
  declare -a REQ_APIS=("iam" "compute" "dataflow" "logging" "storage-component" "storage-api" "bigquery" "pubsub")

  ENABLED_APIS=$(gcloud services list --enabled | grep -v NAME | sort | cut -d " " -f1)
  echo "Current APIs enabled are: ${ENABLED_APIS}"
  for api in "${REQ_APIS[@]}"
  do
    EXISTS=$(echo ${ENABLED_APIS} | grep ${api} | wc -l)
    if [ ${EXISTS} -eq 0 ]
    then
      echo "*** Enabling ${api} API"
      gcloud services enable "${api}.googleapis.com"
    fi
  done
}

trap 'abort' 0
SECONDS=0
main
trap : 0
echo "Project Setup Complete in ${SECONDS} seconds."
