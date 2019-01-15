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

import com.suf.dataflow.banking.AccountsPrePrep;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class BarclaysTransaction implements Serializable, BankTransaction {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");

    private LocalDate when = null;
    private String who = null;
    private String category = null;
    private Float amount = null;
    public static final String SOURCE = "BARCLAYS";

    private BarclaysTransaction() {
        super();
    }

    public BarclaysTransaction(String csv) {
        this();

        StringReader csvReader = null;
        CSVParser parser = null;
        try {
            csvReader = new StringReader(csv);
            parser = new CSVParser(csvReader, CSVFormat.DEFAULT);

            CSVRecord rec = parser.iterator().next();
            // Is it this type of Transaction?
            // Sample Barclays Transaction: 28/03/2017,-66.45,BT GROUP PLC GB09431096-000074
            // DDR,phone
            // PAYMENT,200.00,200.00
            if (rec == null || rec.size() != 4 || rec.get(0).trim().length() == 1) {
                return;
            }

            this.setWhen(LocalDate.parse(rec.get(0), fmt));
            this.setAmount(Float.valueOf(rec.get(1)));
            // Filter out sensitive stuff
            if (this.getAmount() > 2000) {
                return;
            }
            this.setWho(rec.get(2));
        } catch (Exception e) {
            String msg = "Exception parsing Barclays Transaction: " + csv + " -> " + e.getLocalizedMessage();
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
        StringBuffer str = new StringBuffer(SOURCE);

        str.append(getWhen()).append(",").append(getWho()).append(",").append(",");
        if (getCategory() != null) {
            str.append(getCategory());
        } else {
            str.append("UNKNOWN");
        }
        str.append(",").append(getAmount());

        return str.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + SOURCE.hashCode();
        result = prime * result + ((when == null) ? 0 : when.hashCode());
        result = prime * result + ((who == null) ? 0 : who.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());

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