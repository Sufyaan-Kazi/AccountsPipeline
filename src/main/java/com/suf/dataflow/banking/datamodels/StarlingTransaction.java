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
package com.suf.dataflow.banking.datamodels;

import java.io.Serializable;
import java.io.StringReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StarlingTransaction implements Serializable, BankTransaction {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");

    private LocalDate when = null;
    private String who = null;
    private String what = null;
    private String type = null;
    private String category = null;
    private Float amount = null;
    private Float balance = null;

    private StarlingTransaction() {
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
            // Is it this type of Transaction?
            // Sample Starling Transaction: 08/03/2018,KAZI S+B,INITIAL BALANCE,FASTER
            // PAYMENT,200.00,200.00
            if (rec == null || rec.size() != 6 || rec.get(0).trim().length() == 0) {
                return;
            }

            this.setWhen(LocalDate.parse(rec.get(0), fmt));
            this.setWhat(rec.get(1).toUpperCase());
            this.setWho(rec.get(2));
            this.setType(rec.get(3));
            this.setAmount(Float.valueOf(rec.get(4)));
            this.setBalance(Float.valueOf(rec.get(5)));
        } catch (Exception e) {
            String msg = "Exception parsing Starling Transaction: " + csv + " -> " + e.getLocalizedMessage();
            System.err.println(msg);
            throw new IllegalStateException(msg, e);
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

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getWhen()).append(",").append(getWho()).append(",").append(getWhat()).append(",").append(getType())
                .append(",");
        if (getCategory() != null) {
            str.append(getCategory());
        } else {
            str.append("UNKNOWN");
        }
        str.append(",").append(getAmount()).append(",").append(getBalance());

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return this.hashCode() == obj.hashCode();
    }

    /**
     * @return String return the when
     */
    public LocalDate getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen(LocalDate when) {
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