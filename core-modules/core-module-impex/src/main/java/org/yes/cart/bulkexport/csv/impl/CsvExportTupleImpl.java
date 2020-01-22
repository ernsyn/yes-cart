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

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.csv.CsvExportTuple;
import org.yes.cart.domain.entity.Identifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 12:07
 */
public class CsvExportTupleImpl implements CsvExportTuple {

    private static final Logger LOG = LoggerFactory.getLogger(CsvExportTupleImpl.class);

    private final Object data;

    public CsvExportTupleImpl(final Object data) {
        this.data = data;
    }

    /** {@inheritDoc} */
    @Override
    public String getSourceId() {
        return data == null ? "NULL" :
                data.getClass().getSimpleName() + ":" + (data instanceof Identifiable ? ((Identifiable) data).getId() : "N/A");
    }

    /** {@inheritDoc} */
    @Override
    public Object getData() {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public Object getColumnValue(final CsvExportColumn column, final CsvValueAdapter adapter) {
        final Object rawValue = getObjectValue(column);
        return column.getValue(rawValue, adapter, this);
    }

    private Object getObjectValue(final CsvExportColumn column) {
        final String property = column.getName();
        Object rawValue = null;
        try {
            rawValue = PropertyUtils.getNestedProperty(getData(), property);
        } catch (NestedNullException nne) {
            // do not report, it is just null
        } catch (Exception exp) {
            LOG.error("Unable to read property: " + property, exp);
        }
        return rawValue;
    }

    /** {@inheritDoc} */
    @Override
    public List<CsvExportTuple> getSubTuples(final CsvExportDescriptor importDescriptor, final CsvExportColumn column, final CsvValueAdapter adapter) {
        if (CsvImpExColumn.SLAVE_TUPLE_FIELD.equals(column.getFieldType())
                || CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {
            final Object rawValue = getObjectValue(column);

            if (rawValue instanceof Collection) {
                final List<CsvExportTuple> tuples = new ArrayList<>();
                for (final Object rawValueItem : (Collection) rawValue) {
                    tuples.add(new CsvExportTupleImpl(rawValueItem));
                }
                return (List) Collections.unmodifiableList(tuples);
            }

            return (List) Collections.singletonList(new CsvExportTupleImpl(rawValue));
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        if (data != null) {
            return "CsvExportTupleImpl{data="
                    + data.getClass().getSimpleName() + ":"
                    + (data instanceof Identifiable ? ((Identifiable) data).getId() : data)
                    + "}";

        }
        return "CsvExportTupleImpl{data=NULL}";
    }
}
