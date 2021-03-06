#!/bin/sh
# Copyright 2018 Google LLC. This software is provided as-is, without warranty or representation for any use or purpose. Your use of it is subject to your agreements with Google. 
#

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

##This script is for common functions used by other shell scripts

#This is used to remove the previous output folder from the local directory
# and from GCS buckets
# param - The name of the bucket to remove file contents from
tidyUp() {
  if [ "$#" -eq 0 ]; then
    echo "Usage: tidyUp bucket name" >&2
    echo "e.g. $0 gs://bucketname/output/" >&2
    exit 1
  fi
  set +e
  echo "Deleting Bucket $1"
  rm -rf ./output/
  ditchBucket $1/*
  set -e
}

ditchBucket() {
  echo "*** Removing bucket: gs://${1}/ ***"
  gsutil -m rm -f -r gs://${1}/* || true > /dev/null 2>&1
  gsutil rb -f gs://${1}/ || true > /dev/null 2>&1
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
  echo "${PROGNAME}: ${1:-"Unknown Error"}" 1>&2
  exit 1
}
