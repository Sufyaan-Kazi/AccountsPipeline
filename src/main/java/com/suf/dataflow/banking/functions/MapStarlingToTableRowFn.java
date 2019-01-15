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
package com.suf.dataflow.banking.functions;

import com.google.api.services.bigquery.model.TableRow;
import com.suf.dataflow.banking.datamodels.StarlingTransaction;

import org.apache.beam.sdk.transforms.DoFn;

public final class MapStarlingToTableRowFn extends DoFn<StarlingTransaction, TableRow> {
  private static final long serialVersionUID = 1L;

  public MapStarlingToTableRowFn() {
    super();
  }

  @ProcessElement
  public void processElement(ProcessContext c) throws Exception {
    TableRow row = new TableRow();

    row.set("when", c.element().getWhen().toString());
    row.set("what", c.element().getWhat());
    row.set("who", c.element().getWho());
    row.set("category", c.element().getCategory());
    row.set("type", c.element().getType());
    row.set("amount", c.element().getAmount());
    row.set("balance", c.element().getBalance());

    c.output(row);
  }
}
