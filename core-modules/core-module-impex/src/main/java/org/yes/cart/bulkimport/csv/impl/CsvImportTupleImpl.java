/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 12-07-31
 * Time: 8:06 AM
 */
public class CsvImportTupleImpl implements CsvImportTuple {

    private static final CsvValueAdapter SUB_TUPLE = new CsvPlainStringValueAdapter();

    private final String filename;
    private final long lineNumber;
    private final String[] line;

    public CsvImportTupleImpl(final String filename, final long lineNumber, final String[] line) {
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    /** {@inheritDoc} */
    @Override
    public String getSourceId() {
        return filename + ":" + lineNumber;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getData() {
        return line;
    }

    /** {@inheritDoc} */
    @Override
    public Object getColumnValue(final CsvImportColumn column, final CsvValueAdapter valueAdapter) {
        final int colIndex = column.getColumnIndex();
        String rawValue = null;
        if (colIndex > -1 && line != null && colIndex < line.length) {
            rawValue = line[colIndex];
        }
        if (rawValue != null && column.getGroupCount(rawValue) > 1) {
            return column.getValues(rawValue, valueAdapter, this);
        }
        return column.getValue(rawValue, valueAdapter, this);
    }



    /** {@inheritDoc} */
    @Override
    public List<CsvImportTuple> getSubTuples(final CsvImportDescriptor importDescriptor, final CsvImportColumn column, final CsvValueAdapter valueAdapter) {
        if (CsvImpExColumn.SLAVE_TUPLE_FIELD.equals(column.getFieldType())) {
            final String rawValue = (String) getColumnValue(column, SUB_TUPLE);
            final String[] rows = rawValue.split(",");
            final List<CsvImportTuple> subTuples = new ArrayList<>(rows.length);
            int subLine = 0;
            for (String row : rows) {
                subTuples.add(new CsvImportTupleImpl(filename + ":" + lineNumber + ":" + column.getName(), subLine++,
                        row.split(String.valueOf(((CsvImportDescriptor) importDescriptor).getImportFileDescriptor().getColumnDelimiter()))));
            }
            return subTuples;
        } else if (CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {
            return (List) Collections.singletonList(this);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("CsvImportTupleImpl{sid=");
        stringBuilder.append(getSourceId()).append(", line=[");
        if (line != null) {
            for (String column : line) {
                stringBuilder.append(column);
                stringBuilder.append(',');
            }
        }
        stringBuilder.append("]}");
        return stringBuilder.toString();
    }
}
