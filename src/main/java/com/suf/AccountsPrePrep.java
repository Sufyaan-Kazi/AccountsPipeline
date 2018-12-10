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
package com.suf;

import com.google.api.services.bigquery.model.TableRow;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.FloatCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
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

        static class FilterTransactionsFn extends DoFn<String, String> {
                private static final long serialVersionUID = 1L;

                @ProcessElement
                public void processElement(@Element String transactionData, OutputReceiver<String> receiver) {
                        if (transactionData.startsWith("Date")) {
                                return;
                        }

                        if (transactionData.startsWith(",")) {
                                return;
                        }

                        // otherwise
                        receiver.output(transactionData);
                }
        }

        static class WorkOutCategoryFn extends DoFn<String, StarlingTransaction> {
                private static final long serialVersionUID = 1L;

                @ProcessElement
                public void processElement(@Element String transactionData,
                                OutputReceiver<StarlingTransaction> receiver) {
                        StarlingTransaction starlingTrans = new StarlingTransaction(transactionData);

                        // Set category (may be null if no match found)
                        starlingTrans.setCategory(Desc2CategoryMap.INSTANCE.getCategoryForDesc(starlingTrans.getWho()));
                        receiver.output(starlingTrans);
                }
        }

        static class MapToTableRowFn extends DoFn<StarlingTransaction, TableRow> {
                private static final long serialVersionUID = 1L;

                @ProcessElement
                public void processElement(ProcessContext c) throws Exception {
                        TableRow row = new TableRow();
                        StarlingTransaction sTrans = c.element();
                        row.set("when", sTrans.getWhen());
                        row.set("what", sTrans.getWhat());
                        row.set("who", sTrans.getWho());
                        row.set("category", sTrans.getCategory());
                        row.set("type", sTrans.getType());
                        row.set("amount", sTrans.getAmount());
                        row.set("balance", sTrans.getBalance());
                        c.output(row);
                }
        }

        static class MapToStringFn extends DoFn<StarlingTransaction, String> {
                private static final long serialVersionUID = 1L;

                @ProcessElement
                public void processElement(ProcessContext c) throws Exception {
                        StarlingTransaction sTrans = c.element();
                        c.output(sTrans.toString());
                }
        }

        public static void main(String[] args) {

                // Initialise map
                // AccountsPrePrep prep = new AccountsPrePrep();

                // Create a PipelineOptions object. This object lets us set various execution
                // options for our pipeline, such as the runner you wish to use. This example
                // will run with the DirectRunner by default, based on the class path configured
                // in its dependencies.
                PipelineOptions options = PipelineOptionsFactory.create();
                options.setTempLocation("gs://suftempbucket/");

                // Create the Pipeline object with the options we defined above
                Pipeline p = Pipeline.create(options);

                // CoderRegistry cr = p.getCoderRegistry();
                // cr.registerCoderForClass(StarlingTransaction.class, AtomicCoder());

                // Apply Transforms
                PCollection<String> filteredTransaction = p.apply("ReadData", TextIO.read().from("gs://sufbankdata/*"))
                                // Filter out unnecessary rows
                                .apply("FilterTxns", ParDo.of(new FilterTransactionsFn()));

                // Work out the category
                PCollection<StarlingTransaction> pojos = filteredTransaction.apply("GetCategory",
                                ParDo.of(new WorkOutCategoryFn()));

                // write to BQ
                PCollection<TableRow> tblRows = pojos.apply("MapToTableRow", ParDo.of(new MapToTableRowFn()));
                tblRows.apply("WriteToBQ",
                                BigQueryIO.writeTableRows().to("sufaccounts:sufbankingds.starlingtxns")
                                                .withSchema(StarlingTransaction.getBQSchema())
                                                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

                // write text output
                PCollection<String> strData = pojos.apply("GetStringVersion", ParDo.of(new MapToStringFn()));
                strData.apply("WriteToDisk", TextIO.write().to("output/accountdata").withSuffix(".csv"));

                // Run
                p.run().waitUntilFinish();
        }
}
