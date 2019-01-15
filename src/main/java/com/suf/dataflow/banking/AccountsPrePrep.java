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
package com.suf.dataflow.banking;

import com.google.api.services.bigquery.model.TableRow;
import com.suf.dataflow.banking.datamodels.BQSchemaFactory;
import com.suf.dataflow.banking.datamodels.BarclaysTransaction;
import com.suf.dataflow.banking.datamodels.StarlingTransaction;
import com.suf.dataflow.banking.datamodels.TxnConfig;
import com.suf.dataflow.banking.functions.CreateBarclaysTxnFn;
import com.suf.dataflow.banking.functions.CreateStarlingTxnFn;
import com.suf.dataflow.banking.functions.CreateTxnConfigFn;
import com.suf.dataflow.banking.functions.FilterTransactionsFn;
import com.suf.dataflow.banking.functions.MapBarclaysToTableRowFn;
import com.suf.dataflow.banking.functions.MapStarlingToTableRowFn;
import com.suf.dataflow.banking.functions.ObjectToStringFn;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Wait;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * An example pipeline that processes bank account transactions.
 *
 * <p>
 * This class processes bank transactions, by filtering out header rows and
 * invalid lines, mapping the transaction to a category or transaciton type and
 * then writes the enriched/filtered data into BigQuery. The data is windowed by
 * day, amd transactions may come from either Barclays or Starling bank for the
 * purposes of this example.
 * <p>
 * Concepts:
 *
 * <pre>
 *   1. Reading data from text files on Google Cloud Storage
 *   2. Specifying 'inline' transforms
 *   3. Deducing a category and adding this as a new column to the data
 *   4. Writing data to text files and into Big Query
 * </pre>
 *
 */
public class AccountsPrePrep {

        public static void log(Object o) {
                if (o == null) {
                        return;
                }

                System.out.println(o.toString());
        }

        public static void main(String[] args) {

                try {
                        // Create a PipelineOptions object and populate from the supplied options, then
                        // create the pipeline object with these options
                        PipelineOptionsFactory.register(AccountsPipelineOptions.class);
                        AccountsPipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                                        .as(AccountsPipelineOptions.class);
                        log(options.toString());
                        Pipeline bankDataPipeline = Pipeline.create(options);

                        // Create a mapping between possible words in the transaction description and
                        // the
                        // category to assign, e.g. '....tescos...' is most likely groceries shopping
                        PCollection<TxnConfig> txnConfig = bankDataPipeline
                                        .apply("ReadConfig", TextIO.read().from(options.getMappingFile()))
                                        // ConvertToConfig
                                        .apply("ConvertToTxnConfig", ParDo.of(new CreateTxnConfigFn()));

                        // Once the config mapping has been loaded
                        // Read in the Bank Transaciton Data, filter out invalid transactions
                        PCollection<String> filteredTxns = bankDataPipeline
                                        .apply("ReadData", TextIO.read().from(options.getSourceFolder() + "*"))
                                        // Filter out unnecessary rows
                                        .apply("FilterOutInvalidTxns", ParDo.of(new FilterTransactionsFn()))
                                        // Make sure the config has been loaded
                                        .apply("WaitForConfig", Wait.on(txnConfig));

                        // Create a colleciton of Starling Bank Transactions from the input, windowed by
                        // day
                        PCollection<StarlingTransaction> starlingTxns = filteredTxns
                                        // Determine Category
                                        .apply("ConvertToStarlingTxns", ParDo.of(new CreateStarlingTxnFn()))
                                        .apply("WindowStarlingTxns",
                                                        Window.<StarlingTransaction>into(
                                                                        FixedWindows.of(Duration.standardDays(1))))
                                        .apply("AddStarlingTimestamp",
                                                        ParDo.of(new DoFn<StarlingTransaction, StarlingTransaction>() {
                                                                private static final long serialVersionUID = 1L;

                                                                @ProcessElement
                                                                public void processElement(
                                                                                @Element StarlingTransaction transactionData,
                                                                                OutputReceiver<StarlingTransaction> receiver) {
                                                                        receiver.outputWithTimestamp(transactionData,
                                                                                        new Instant(transactionData
                                                                                                        .getWhen()
                                                                                                        .toDate()
                                                                                                        .getTime()));
                                                                }
                                                        }));

                        // Create a PColleciton of Barclays Transactions from the input, windowed by day
                        PCollection<BarclaysTransaction> barclaysTxns = filteredTxns
                                        // Determine Category
                                        .apply("ConvertToBarclaysTxns", ParDo.of(new CreateBarclaysTxnFn()))
                                        .apply("WindowBarclaysTxns",
                                                        Window.<BarclaysTransaction>into(
                                                                        FixedWindows.of(Duration.standardDays(1))))
                                        .apply("AddBarclaysTimestamp",
                                                        ParDo.of(new DoFn<BarclaysTransaction, BarclaysTransaction>() {
                                                                private static final long serialVersionUID = 1L;

                                                                @ProcessElement
                                                                public void processElement(
                                                                                @Element BarclaysTransaction transactionData,
                                                                                OutputReceiver<BarclaysTransaction> receiver) {
                                                                        receiver.outputWithTimestamp(transactionData,
                                                                                        new Instant(transactionData
                                                                                                        .getWhen()
                                                                                                        .toDate()
                                                                                                        .getTime()));
                                                                }
                                                        }));

                        // write out the Starling Bank data to a GCS bucket
                        PCollection<String> starlingOutputCSV = starlingTxns.apply("ConvertStarlingToCSV",
                                        ParDo.of(new ObjectToStringFn()));
                        starlingOutputCSV.apply("WriteStarlingToGCS",
                                        TextIO.write().to(options.getOutputStarlingFolder()).withSuffix(".csv"));

                        // Write out the Barclays data to a GCS Bucket
                        PCollection<String> barclaysOutputCSV = barclaysTxns.apply("ConvertBarclaysToCSV",
                                        ParDo.of(new ObjectToStringFn()));
                        barclaysOutputCSV.apply("WriteBarclaysToGCS",
                                        TextIO.write().to(options.getOutputBarclaysFolder()).withSuffix(".csv"));

                        // also write to BQ
                        PCollection<TableRow> starlingTblRows = starlingTxns.apply("MapStarlingToBQRows",
                                        ParDo.of(new MapStarlingToTableRowFn()));
                        starlingTblRows.apply("InsertStarlingBQRows", BigQueryIO.writeTableRows()
                                        .to(options.getBQTable()).withSchema(BQSchemaFactory.getStarlingBQSchema())
                                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

                        PCollection<TableRow> barclaysTblRows = barclaysTxns.apply("MapBarclaysToBQRows",
                                        ParDo.of(new MapBarclaysToTableRowFn()));
                        barclaysTblRows.apply("InsertBarclaysBQRows", BigQueryIO.writeTableRows()
                                        .to(options.getBQTable()).withSchema(BQSchemaFactory.getStarlingBQSchema())
                                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));

                        // Run
                        bankDataPipeline.run().waitUntilFinish();
                } catch (Throwable t) {
                        System.err.println("******************* An Exception occurred *****************");
                        System.err.println(t.getLocalizedMessage());
                        t.printStackTrace();
                }
        }
}
