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
package com.suf.dataflow.banking.datamapping;

public class Categories {
  // Singleton
  private static final Categories INSTANCE = new Categories();

  public static final String GROCERIES = "GROCERIES";
  public static final String EATING_OUT = "EATING OUT";
  public static final String TRAVEL = "TRAVEL";
  public static final String SHOPPING = "SHOPPING";
  public static final String PARKING = "PARKING";
  public static final String INTEREST = "INTEREST";
  public static final String SALARY = "SALARY";
  public static final String TRANSFER = "TRANSFER";
  public static final String DONATIONS = "DONATIONS";
  public static final String MISC = "MISC";
  public static final String CAR = "CAR";
  public static final String ENTERTAINMENT = "ENTERTAINMENT";
  public static final String PHONE = "PHONE";
  public static final String HOME_IMPROV = "HOME_IMPROV";
  public static final String BEAUTY = "BEAUTY";
  public static final String DIY = "DIY";
  public static final String FUEL = "FUEL";
  public static final String WITHDRAWAL = "WITHDRAWAL";
  public static final String UTILITIES = "UTILITIES";
  public static final String HEALTH = "HEALTH";
  public static final String TV = "TV";
  public static final String POSTAGE = "POSTAGE";
  public static final String INSURANCE = "INSURANCE";
  public static final String BILLS = "BILLS";
  public static final String HOLIDAY = "HOLIDAY";
  public static final String CCARD = "CCARD";

  private Categories() {
    super();
  }
}
