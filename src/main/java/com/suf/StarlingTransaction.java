package com.suf;

import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;

import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@DefaultCoder(AtomicCoder.class)
public class StarlingTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date when = null;
    private String who = null;
    private String what = null;
    private String type = null;
    private String category = null;
    private Float amount = null;
    private Float balance = null;

    public StarlingTransaction() {
        super();
    }

    public StarlingTransaction(String csv) {
        this();

        StringReader csvReader = null;
        CSVParser parser = null;
        try {
            csvReader = new StringReader(csv);
            parser = new CSVParser(csvReader, CSVFormat.DEFAULT);
            CSVRecord rec = parser.iterator().next();

            SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
            this.setWhen(ft.parse(rec.get(0)));
            this.setWhat(rec.get(1));
            this.setWho(rec.get(2));
            this.setType(rec.get(3));
            this.setAmount(Float.valueOf(rec.get(4)));
            this.setBalance(Float.valueOf(rec.get(5)));
        } catch (Exception e) {
            System.err.println("Exception with: " + csv + " -> " + e.getLocalizedMessage());
            throw new IllegalStateException(e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                } finally {
                    csvReader = null;
                }
            }

            if (parser != null) {
                try {
                    parser.close();
                } catch (Exception e) {
                } finally {
                    parser = null;
                }
            }
        }
    }

    public static TableSchema getBQSchema() {
        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("when").setType("STRING"));
        fields.add(new TableFieldSchema().setName("what").setType("STRING"));
        fields.add(new TableFieldSchema().setName("who").setType("STRING"));
        fields.add(new TableFieldSchema().setName("type").setType("STRING"));
        fields.add(new TableFieldSchema().setName("category").setType("STRING"));
        fields.add(new TableFieldSchema().setName("amount").setType("FLOAT"));
        fields.add(new TableFieldSchema().setName("balnace").setType("FLOAT"));
        TableSchema schema = new TableSchema().setFields(fields);

        return schema;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getWhen()).append(",").append(getWho()).append(",").append(getWhat()).append(",");
        if (getCategory() != null) {
            str.append(getCategory()).append(",");
        }

        str.append(getAmount()).append(",").append(getBalance());

        return str.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((when == null) ? 0 : when.hashCode());
        result = prime * result + ((who == null) ? 0 : who.hashCode());
        result = prime * result + ((what == null) ? 0 : what.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());

        return result;
    }

    /**
     * @return String return the when
     */
    public Date getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen(Date when) {
        this.when = when;
    }

    /**
     * @return String return the who
     */
    public String getWho() {
        return who;
    }

    /**
     * @param who the who to set
     */
    public void setWho(String who) {
        this.who = who;
    }

    /**
     * @return String return the what
     */
    public String getWhat() {
        return what;
    }

    /**
     * @param what the what to set
     */
    public void setWhat(String what) {
        this.what = what;
    }

    /**
     * @return String return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Float return the amount
     */
    public Float getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Float amount) {
        this.amount = amount;
    }

    /**
     * @return Float return the balance
     */
    public Float getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(Float balance) {
        this.balance = balance;
    }

    /**
     * @return String return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

}