# AccountsPipeline

This is a draft pipeline for processing data (in batch for now) using Beam and sending that data to BigQuery. In due course this data will be amended to include streaming, windowing and other cool stuff

runLocal.sh or runOnDataFlow.sh are the main entry points. The shell/code does the following:

1) Copy all input data (banking transactions) from input gcs folders into a single folder)
2) Read an input mapping files used to infer the transaction type from text in a transaction
3) Map and process the raw transaction data into categorised transactions, write them to BigQuery and then as csv's to an out GCS folder.

![Graph](/docs/dflow.png)
