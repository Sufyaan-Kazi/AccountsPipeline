/*
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
package com.suf.dataflow.banking.datamodels;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;

public class BQSchemaFactory {
  private static TableSchema starlingSchema = null;

  public static TableSchema getStarlingBQSchema() {
    if (starlingSchema == null) {
      List<TableFieldSchema> fields = new ArrayList<>();
      fields.add(new TableFieldSchema().setName("when").setType("DATE"));
      fields.add(new TableFieldSchema().setName("what").setType("STRING"));
      fields.add(new TableFieldSchema().setName("who").setType("STRING"));
      fields.add(new TableFieldSchema().setName("type").setType("STRING"));
      fields.add(new TableFieldSchema().setName("category").setType("STRING"));
      fields.add(new TableFieldSchema().setName("amount").setType("FLOAT"));
      fields.add(new TableFieldSchema().setName("balance").setType("FLOAT"));
      starlingSchema = new TableSchema().setFields(fields);
    }

    return starlingSchema;
  }

}