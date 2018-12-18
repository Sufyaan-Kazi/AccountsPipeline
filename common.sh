#!/bin/sh

tidyUp() {
  set +e
  echo "Performing TidyUp"
  rm -rf ./output/
  gsutil -m rm -r gs://sufbankdata/output/
  set -e
}
