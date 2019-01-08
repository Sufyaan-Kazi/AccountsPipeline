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

import com.suf.dataflow.banking.datamapping.TxnConfigMap;
import com.suf.dataflow.banking.datamodels.BarclaysTransaction;
import com.suf.dataflow.banking.datamodels.TxnConfig;

import org.apache.beam.sdk.transforms.DoFn;

public final class CreateBarclaysTxnFn extends DoFn<String, BarclaysTransaction> {
  private static final long serialVersionUID = 1L;

  public CreateBarclaysTxnFn() {
    super();
  }

  @ProcessElement
  public void processElement(@Element String transactionData, OutputReceiver<BarclaysTransaction> receiver) {
    BarclaysTransaction barclaysTrans = new BarclaysTransaction(transactionData);
    if (barclaysTrans == null || barclaysTrans.getAmount() == null) {
      return;
    }
    // System.out.println("Processing: " + starlingTrans);

    // Set category (may be null if no match found)
    TxnConfig config = TxnConfigMap.getTxnConfig(barclaysTrans);
    if (config == null)
      System.out.println("\tConfig: " + barclaysTrans + " - " + config);

    barclaysTrans.setCategory(config.getCategory());
    // log("Category has been set to: " + starlingTrans.getCategory());
    receiver.output(barclaysTrans);
  }
}
