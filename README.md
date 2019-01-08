# AccountsPipeline

This is a pipeline for processing data (in batch for now) using Apache Beam and sending that data to BigQuery, it can use the directrunner or Dataflow. In due course this will be amended to include streaming, windowing and other cool stuff. The aim of the program is to read banking csv transaction data (from different banks i.e. different formats) and look at characteristics such as description to try and infer a category. It uses a mapping file that maps a partial phrase to category. Once the data is in BigQuery, it can be analysed using DataStudio, looker etc.

runLocal.sh or runOnDataFlow.sh are the main entry points. The shell/code does the following:

0) Delete previous output data from Google Cloud Storage (GCS)
1) Copy all input data (banking transactions) from separate input gcs folders into a single folder
2) Read an the category mapping file into memory
3) Map and process the raw transaction data into categorised transactions, write them to BigQuery and then as csv's to an out GCS folder.

![Graph](/docs/dflow.png)
