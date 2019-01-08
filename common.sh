#!/bin/sh

tidyUp() {
  if [ "$#" -eq 0 ]; then
    echo "Usage: tidyUp bucket name" >&2
    echo "e.g. $0 gs://sufbankdata/output/" >&2
    exit 1
  fi
  set +e
  echo "Performing TidyUp"
  rm -rf ./output/
  gsutil -m rm -r $1
  set -e
}
