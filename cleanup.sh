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
  bq rm -rf ${DATASET}
}

removeServiceAcc(){
  SERVICE_ACC_EXISTS=$(gcloud iam service-accounts list | grep $SERVICE_ACC | wc -l)
  if [ $SERVICE_ACC_EXISTS -ne 0 ]
  then
    gcloud iam service-accounts delete ${SERVICE_ACC}.iam.gserviceaccount.com -q
  fi
}

main
