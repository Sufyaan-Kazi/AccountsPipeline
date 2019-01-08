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

import com.suf.dataflow.banking.datamodels.BarclaysTransaction;
import com.suf.dataflow.banking.datamodels.StarlingTransaction;
import com.suf.dataflow.banking.datamodels.TxnConfig;
import com.suf.dataflow.banking.functions.CreateBarclaysTxnFn;
import com.suf.dataflow.banking.functions.CreateStarlingTxnFn;
import com.suf.dataflow.banking.functions.CreateTxnConfigFn;
import com.suf.dataflow.banking.functions.FilterTransactionsFn;
import com.suf.dataflow.banking.functions.ObjectToStringFn;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Wait;
import org.apache.beam.sdk.values.PCollection;

/**
 * An example that counts words in Shakespeare.
 *
 * <p>
 * This class, {@link MinimalWordCount}, is the first in a series of four
 * successively more detailed 'word count' examples. Here, for simplicity, we
 * don't show any error-checking or argument processing, and focus on
 * construction of the pipeline, which chains together the application of core
 * transforms.
 *
 * <p>
 * Next, see the {@link WordCount} pipeline, then the
 * {@link DebuggingWordCount}, and finally the {@link WindowedWordCount}
 * pipeline, for more detailed examples that introduce additional concepts.
 *
 * <p>
 * Concepts:
 *
 * <pre>
 *   1. Reading data from text files
 *   2. Specifying 'inline' transforms
 *   3. Counting items in a PCollection
 *   4. Writing data to text files
 * </pre>
 *
 * <p>
 * No arguments are required to run this pipeline. It will be executed with the
 * DirectRunner. You can see the results in the output files in your current
 * working directory, with names like "wordcounts-00001-of-00005. When running
 * on a distributed service, you would use an appropriate file service.
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

                        // Initialise map
                        // AccountsPrePrep prep = new AccountsPrePrep();

                        // Create a PipelineOptions object. This object lets us set various execution
                        // options for our pipeline, such as the runner you wish to use. This example
                        // will run with the DirectRunner by default, based on the class path configured
                        // in its dependencies.
                        PipelineOptionsFactory.register(AccountsPipelineOptions.class);
                        // PipelineOptions options =
                        // PipelineOptionsFactory.fromArgs(args).withValidation().create();

                        AccountsPipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                                        .as(AccountsPipelineOptions.class);
                        log(options.toString() + options.getBQTable());

                        // Create the Pipeline object with the options we defined above
                        Pipeline bankDataPipeline = Pipeline.create(options);

                        // Get config descriptions for StarlingTransactions
                        PCollection<TxnConfig> txnConfig = bankDataPipeline
                                        .apply("ReadConfig", TextIO.read().from(options.getMappingFile()))
                                        // ConvertToConfig
                                        .apply("ConvertToTxnConfig", ParDo.of(new CreateTxnConfigFn()));

                        // Get Starling Data into usable format
                        PCollection<String> filteredTxns = bankDataPipeline
                                        .apply("ReadData", TextIO.read().from(options.getSourceFolder() + "*"))
                                        // Filter out unnecessary rows
                                        .apply("FilterStarlingTxns", ParDo.of(new FilterTransactionsFn()))
                                        // Make sure the config has been loaded
                                        .apply("WaitForConfig", Wait.on(txnConfig));

                        // Get Starling Data into usable format
                        PCollection<StarlingTransaction> starlingTxns = filteredTxns
                                        // Determine Category
                                        .apply("ConvertToStarlingTxns", ParDo.of(new CreateStarlingTxnFn()));

                        // Get Barclays Data into usable format
                        PCollection<BarclaysTransaction> barclaysTxns = filteredTxns
                                        // Determine Category
                                        .apply("ConvertToBarclaysTxns", ParDo.of(new CreateBarclaysTxnFn()));

                        // write filtered and enhanced output to GCS buckets
                        PCollection<String> starlingOutputCSV = starlingTxns.apply("ConvertStarlingToCSV",
                                        ParDo.of(new ObjectToStringFn()));
                        starlingOutputCSV.apply("WriteStarlingToDisk",
                                        TextIO.write().to(options.getOutputStarlingFolder()).withSuffix(".csv"));

                        // write filtered and enhanced output to GCS buckets
                        PCollection<String> barclaysOutputCSV = barclaysTxns.apply("ConvertBarclaysToCSV",
                                        ParDo.of(new ObjectToStringFn()));
                        barclaysOutputCSV.apply("WriteBarclaysToDisk",
                                        TextIO.write().to(options.getOutputBarclaysFolder()).withSuffix(".csv"));

                        // also write to BQ
                        /*
                         * PCollection<TableRow> tblRows = starlingTxns.apply("MapStarlingToBQRows",
                         * ParDo.of(new MapToTableRowFn())); tblRows.apply("InsertStarlingBQRows",
                         * BigQueryIO.writeTableRows().to(options.getBQTable())
                         * .withSchema(BQSchemaFactory.getStarlingBQSchema())
                         * .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                         * .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_TRUNCATE));
                         */

                        // Run
                        bankDataPipeline.run().waitUntilFinish();
                } catch (Throwable t) {
                        System.err.println("******************* An Exception occurred *****************");
                        System.err.println(t.getLocalizedMessage());
                        t.printStackTrace();
                }
        }
}
