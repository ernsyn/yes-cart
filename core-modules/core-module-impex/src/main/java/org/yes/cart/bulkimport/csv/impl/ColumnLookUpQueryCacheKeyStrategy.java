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

import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.service.support.csv.EntityCacheKeyStrategy;
import org.yes.cart.dao.GenericDAO;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:40 AM
 */
public class ColumnLookUpQueryCacheKeyStrategy implements EntityCacheKeyStrategy {

    private final LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> lookUpStrategy;
    private final GenericDAO<Object, Long> genericDAO;

    public ColumnLookUpQueryCacheKeyStrategy(final LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> lookUpStrategy,
                                             final GenericDAO<Object, Long> genericDAO) {
        this.lookUpStrategy = lookUpStrategy;
        this.genericDAO = genericDAO;
    }

    /** {@inheritDoc} */
    @Override
    public String keyFor(final CsvImportDescriptor descriptor,
                         final CsvImportColumn column,
                         final Object masterObject,
                         final CsvImportTuple tuple,
                         final CsvValueAdapter adapter) {

        final LookUpQuery query = lookUpStrategy.getQuery(descriptor, masterObject, tuple, adapter, column.getLookupQuery());

        final StringBuilder sb = new StringBuilder();
        sb.append(column.getName()).append('_').append(column.getColumnIndex()).append('_').append(query.getQueryString());
        for (Object obj : query.getParameters()) {
            sb.append('_').append(obj);
        }

        if (column.isUseMasterObject()) {
            final Object pk;
            if (masterObject != null) {
                pk = genericDAO.getEntityIdentifier(masterObject);
            } else {
                pk = null; // Not Available
            }
            if (pk != null) {
                sb.append('_').append(pk);
            } else {
                sb.append("_NA");
            }
        }
        return sb.toString();
    }
}
