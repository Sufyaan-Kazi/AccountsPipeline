#!/bin/bash
set -e

main() {
  trap 'abort' 0

  SECONDS=0

  . ./vars.txt

  removeServiceAcc 
  removeDataset 

  trap : 0
  echo "Project Cleanup Complete in ${SECONDS} seconds."
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

removeDataset() {
  echo "Removing Dataset: " ${DATASET}
  bq rm -rf ${DATASET}
}

removeServiceAcc(){
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
      gcloud projects remove-iam-policy-binding ${PROJECT_ID} --member "serviceAccount:${SERVICE_ACC}.iam.gserviceaccount.com" --role "${role}" --quiet
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
