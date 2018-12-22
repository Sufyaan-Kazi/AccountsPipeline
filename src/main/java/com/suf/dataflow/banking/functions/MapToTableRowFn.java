package com.suf.dataflow.banking.functions;

import com.google.api.services.bigquery.model.TableRow;
import com.suf.dataflow.banking.datamodels.StarlingTransaction;

import org.apache.beam.sdk.transforms.DoFn;

public final class MapToTableRowFn extends DoFn<StarlingTransaction, TableRow> {
  private static final long serialVersionUID = 1L;
  public static final MapToTableRowFn INSTANCE = new MapToTableRowFn();

  private MapToTableRowFn() {
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
