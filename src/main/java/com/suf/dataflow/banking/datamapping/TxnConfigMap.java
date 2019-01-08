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
package com.suf.dataflow.banking.datamapping;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.suf.dataflow.banking.AccountsPrePrep;
import com.suf.dataflow.banking.datamodels.BarclaysTransaction;
import com.suf.dataflow.banking.datamodels.StarlingTransaction;
import com.suf.dataflow.banking.datamodels.TxnConfig;

public class TxnConfigMap {
  private static final TxnConfigMap INSTANCE = new TxnConfigMap();
  private static final ConcurrentMap<String, TxnConfig> txnConfigMap = new ConcurrentHashMap<String, TxnConfig>();

  private TxnConfigMap() {
    super();
  }

  public static void addTxnConfig(TxnConfig item) {
    if (item == null)
      return;

    txnConfigMap.put(item.getIdentifiyingText().toUpperCase(), item);
  }

  public static TxnConfig getTxnConfig(StarlingTransaction sTrans) {
    if (sTrans == null)
      return new TxnConfig();

    // AccountsPrePrep.log("Assessing: " + sTrans);

    if (sTrans.getType().equalsIgnoreCase("ATM")) {
      return txnConfigMap.get("ATM");
    }

    if (sTrans.getType().equalsIgnoreCase("donate")) {
      return txnConfigMap.get("DONATE");
    }

    /*
     * Look at who was paid to see if it is known
     */
    for (String key : txnConfigMap.keySet()) {
      if (key.indexOf(sTrans.getWhat().toUpperCase()) > -1) {
        // AccountsPrePrep.log("Found match from the what: " + key + "->" +
        // sTrans.getWhat().toUpperCase());
        return txnConfigMap.get(key);
      }
    }

    /*
     * Alternately, look at the description of the payment
     */
    for (String key : txnConfigMap.keySet()) {
      if (sTrans.getWho().toUpperCase().indexOf(key) > -1) {
        // AccountsPrePrep.log("Found match from description: " + key + "-> " +
        // sTrans.getWho().toUpperCase());
        return txnConfigMap.get(key);
      }
    }

    // Otherwise return unknown one
    return new TxnConfig();
  }

  public static TxnConfig getTxnConfig(BarclaysTransaction sTrans) {
    if (sTrans == null)
      return new TxnConfig();

    /*
     * Alternately, look at the description of the payment
     */
    for (String key : txnConfigMap.keySet()) {
      if (sTrans.getWho().toUpperCase().indexOf(key) > -1) {
        return txnConfigMap.get(key);
      }
    }

    // Otherwise return unknown one
    return new TxnConfig();
  }
}