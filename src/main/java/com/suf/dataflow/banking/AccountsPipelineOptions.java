/*
 *
 * “Copyright 2018 Google LLC. This software is provided as-is, without warranty or representation for any use or purpose. Your use of it is subject to your agreements with Google.”  
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suf.dataflow.banking;

import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

// Creates a user defined property called "myProperty"
public interface AccountsPipelineOptions extends PipelineOptions {
  @Description("Location of transaction mapping file")
  String getMappingFile();

  void setMappingFile(String mappingFile);

  @Description("Source location for Data")
  String getSourceFolder();

  void setSourceFolder(String sourceFolder);

  @Description("Output location for Starling Data")
  String getOutputStarlingFolder();

  void setOutputStarlingFolder(String outputStarlingFolder);

  @Description("Output location for Barclays Data")
  String getOutputBarclaysFolder();

  void setOutputBarclaysFolder(String outpuBarclaysFolder);

  @Description("BigQuery Table")
  String getBQTable();

  void setBQTable(String BQTable);
}
