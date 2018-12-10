#!/bin/bash
set -e

main() {
  trap 'abort' 0

  SECONDS=0

  . ./vars.txt

  #gcloud config set project $PROJECT_ID
  enableAPIS
  createServiceAccount
  createBQDataset

  #gsutil mb ${BUCKET_NAME}

  trap : 0
  echo "Project Setup Complete in ${SECONDS} seconds."
}

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

## Create the Big Query dataset
createBQDataset(){
  bq mk ${DATASET}
  bq mk ${DATASET}.${STARLING_TABLE}
}

## Create the Service accounts
createServiceAccount() {
  SERVICE_ACC_EXISTS=$(gcloud iam service-accounts list | grep ${KEY_FILE} | wc -l)
  if [ ${SERVICE_ACC_EXISTS} -eq 0 ]
  then
    #Create the service account
    echo "*** Creating Service Account - ${KEY_NAME}"
    gcloud iam service-accounts create ${KEY_NAME} --display-name=${KEY_NAME}

    # Add Role Policy Bindings
    declare -a roles=("roles/storage.objectCreator" "roles/storage.objectViewer")
    for role in "${roles[@]}"
    do
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

runBQCommand() {
   bq $@
   sleep 2
}

main
