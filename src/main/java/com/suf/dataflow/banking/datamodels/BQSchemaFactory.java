package com.suf.dataflow.banking.datamodels;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;

public class BQSchemaFactory {
  private static TableSchema starlingSchema = null;

  public static TableSchema getStarlingBQSchema() {
    if (starlingSchema == null) {
      List<TableFieldSchema> fields = new ArrayList<>();
      fields.add(new TableFieldSchema().setName("when").setType("DATE"));
      fields.add(new TableFieldSchema().setName("what").setType("STRING"));
      fields.add(new TableFieldSchema().setName("who").setType("STRING"));
      fields.add(new TableFieldSchema().setName("type").setType("STRING"));
      fields.add(new TableFieldSchema().setName("category").setType("STRING"));
      fields.add(new TableFieldSchema().setName("amount").setType("FLOAT"));
      fields.add(new TableFieldSchema().setName("balance").setType("FLOAT"));
      starlingSchema = new TableSchema().setFields(fields);
    }

    return starlingSchema;
  }

}