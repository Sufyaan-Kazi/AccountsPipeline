# AccountsPipeline

This is a sample pipeline (to be used for education and inspiration purposes only) for processing financial data using Apache Beam. In this sample, personal baking transacitons are processed so they can be inserted into BigQuery. The transaciton data is filtered to removed bad data, enhanced using primitive deduction work to infer the category or type of spending (e.g. groceries vs clothe shopping etc), and the data is windowed on a day basis. 

This project can be executed by using either runLocal.sh or runOnDataflow.sh.

This project uses parameters such as source and destination bucket locaitons, BigQuery table names etc, which are all defined in a config file (vars.txt).

This project has a script called projSetup.sh which can be used to enable the required GCP APi's to create the required service account, and to create the BigQuery dataset/table. Similarly there is a script alled cleanup.sh which removes the objects created for when a fresh start is needed.

TO DO: Think about dataflow templates, amend from batch to streaming as well

runLocal.sh or runOnDataFlow.sh are the main entry points. The shell/code does the following:

0) Delete previous output data from Google Cloud Storage (GCS)
1) Copy input data (banking transactions) from their separate input gcs folders (this assumes data is taken from Barclays Bank and Starling Bank), and places the data into a single GCS folder
2) Load the config which is used for the primitve category/type decision making
3) Execute the pipeline

The Beam pipeline itself does the following:

0) Load config (and waits till this is complete)
1) Reads all the Banking Transactions and filters out header rows or rows that are empty or missing key fields
2) Converts the data from Starling into a Starling format and likewise the BArclays Data into a Barclays format, both windowed by a day
3) Writes the final transactions as CSV onto Google Cloud Storage and into BigQuery

Because of the nature of Beam, there is no need in the code or the config to specify the number of threads, workers etc and/or the precise ordering of steps. The Beam runners will dynamically work this out, scaling up underlying compute resources as needed and re-balancing work between idle workers. In this way if there was a deluge of Barclays data then that part of the pipeline could be given more workers and indeed may be prioritised over starling, the runner does this for me automatically. Also, Beam automatically handles the parrellisation for me to the constraints of the data and compute as needed.

![Graph](/docs/dflow.png)
