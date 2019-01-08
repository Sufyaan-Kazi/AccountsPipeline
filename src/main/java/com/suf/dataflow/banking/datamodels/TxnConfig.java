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

public class TxnConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String identifiyingText = "UNKNOWN";
    private String category = "UNKNOWN";
    private String spendType = "UNKNOWN";

    public TxnConfig() {
        super();
    }

    public TxnConfig(String csv) {
        this();

        if (csv == null || csv.trim().length() == 0) {
            throw new IllegalStateException("No mapping file to identify transactions type supplied");
        }

        StringReader csvReader = null;
        CSVParser parser = null;
        try {
            csvReader = new StringReader(csv);
            parser = new CSVParser(csvReader, CSVFormat.DEFAULT);
            CSVRecord rec = parser.iterator().next();

            this.setIdentifiyingText(rec.get(0).toUpperCase());
            this.setCategory(rec.get(1).toUpperCase());
            this.setSpendType(rec.get(2).toUpperCase());
        } catch (Exception e) {
            System.err.println("Exception with: " + csv + " -> " + e.getLocalizedMessage());
            throw new IllegalStateException("Could not understand TxnConfig: " + csv);
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

        str.append(getIdentifiyingText()).append(",").append(getCategory()).append(",").append(getSpendType())
                .append(",").append(",").append(",");

        return str.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((identifiyingText == null) ? 0 : identifiyingText.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((spendType == null) ? 0 : spendType.hashCode());

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
     * @return String return the identifiyingText
     */
    public String getIdentifiyingText() {
        return identifiyingText;
    }

    /**
     * @param identifiyingText the identifiyingText to set
     */
    public void setIdentifiyingText(String identifiyingText) {
        this.identifiyingText = identifiyingText;
    }

    /**
     * @return String return the Category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return Float return the spendType
     */
    public String getSpendType() {
        return spendType;
    }

    /**
     * @param spendType the spendType to set
     */
    public void setSpendType(String spendType) {
        this.spendType = spendType;
    }

}